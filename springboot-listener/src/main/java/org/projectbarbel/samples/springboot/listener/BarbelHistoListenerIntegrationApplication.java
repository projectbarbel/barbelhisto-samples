package org.projectbarbel.samples.springboot.listener;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.projectbarbel.histo.BarbelHisto;
import org.projectbarbel.histo.BarbelHistoBuilder;
import org.projectbarbel.histo.BarbelHistoContext;
import org.projectbarbel.histo.BarbelMode;
import org.projectbarbel.histo.model.BitemporalObjectState;
import org.projectbarbel.histo.model.EffectivePeriod;
import org.projectbarbel.samples.springboot.model.Customer;
import org.projectbarbel.samples.springboot.support.CustomerReadConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.MongoConfigurationSupport;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.util.Assert;

import com.mongodb.MongoClient;
import com.projectbarbel.histo.persistence.mongo.MongoPessimisticLockingListener;
import com.projectbarbel.histo.persistence.mongo.SimpleMongoLazyLoadingListener;
import com.projectbarbel.histo.persistence.mongo.SimpleMongoUpdateListener;


@SpringBootApplication
public class BarbelHistoListenerIntegrationApplication extends MongoConfigurationSupport implements CommandLineRunner {

    @Autowired
    private CustomerService service;
    
    @Autowired
    private CustomerRepository repository;

    @Bean
    public BarbelHisto<Customer> barbel(MongoClient client) {
        SimpleMongoUpdateListener updateListener = SimpleMongoUpdateListener.create(
                client, "test", "customer", Customer.class,
                BarbelHistoContext.getDefaultGson());
        SimpleMongoLazyLoadingListener loadingListener = SimpleMongoLazyLoadingListener.create(
                client, "test", "customer", Customer.class,
                BarbelHistoContext.getDefaultGson(), true, true);
        MongoPessimisticLockingListener locking = MongoPessimisticLockingListener
                .create(client, "tsLockDb", "docLocks");
        return BarbelHistoBuilder.barbel().withMode(BarbelMode.BITEMPORAL).withSynchronousEventListener(updateListener)
                .withSynchronousEventListener(loadingListener).withSynchronousEventListener(locking).build();
    }

    public static void main(String[] args) throws Exception {
	    SpringApplication.run(BarbelHistoListenerIntegrationApplication.class, args);
	}
	
    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoCustomConversions customConversions() {
        final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
        converters.add(new CustomerReadConverter());
        return new MongoCustomConversions(converters);
    }

	@Override
    public void run(String... args) throws Exception {

	    Customer customer = new Customer("1234", "Alice", "Smith", "Some Street 10", "Houston", "77001");
	    
        // save a couple of customers
	    service.saveCustomer(customer, ZonedDateTime.now(), EffectivePeriod.INFINITE);
	    service.saveCustomer(customer, ZonedDateTime.now().plusDays(10), EffectivePeriod.INFINITE);
	    service.saveCustomer(customer, ZonedDateTime.now().plusDays(20), EffectivePeriod.INFINITE);
	    
        // validate the state of the journal
	    Assert.isTrue(repository.findByClientIdAndBitemporalStampRecordTimeState("1234", BitemporalObjectState.ACTIVE).size() == 3, "must contain 3 active records");
	    Assert.isTrue(repository.findByClientIdAndBitemporalStampRecordTimeState("1234", BitemporalObjectState.INACTIVE).size() == 2, "must contain 2 inactive records");

	    service.saveCustomer(customer, ZonedDateTime.now().minusDays(1), EffectivePeriod.INFINITE);
	    
	    // validate the state of the journal
        Assert.isTrue(repository.findByClientIdAndBitemporalStampRecordTimeState("1234", BitemporalObjectState.ACTIVE).size() == 1, "must contain 1 active records");
        Assert.isTrue(repository.findByClientIdAndBitemporalStampRecordTimeState("1234", BitemporalObjectState.INACTIVE).size() == 5, "must contain 5 inactive records");
	    
	    System.out.println(repository.findAll().toString());
    }
}
