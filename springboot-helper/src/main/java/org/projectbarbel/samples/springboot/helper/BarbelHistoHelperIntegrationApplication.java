package org.projectbarbel.samples.springboot.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.projectbarbel.histo.model.BitemporalObjectState;
import org.projectbarbel.histo.model.EffectivePeriod;
import org.projectbarbel.samples.springboot.model.Customer;
import org.projectbarbel.samples.springboot.support.ZonedDateTimeReadConverter;
import org.projectbarbel.samples.springboot.support.ZonedDateTimeWriteConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.MongoConfigurationSupport;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.util.Assert;

@SpringBootApplication
public class BarbelHistoHelperIntegrationApplication extends MongoConfigurationSupport implements CommandLineRunner {

    @Autowired
    private CustomerService service;
    
    @Autowired
    private CustomerRepository repository;
    

    public static void main(String[] args) throws Exception {
	    SpringApplication.run(BarbelHistoHelperIntegrationApplication.class, args);
	}
	
    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoCustomConversions customConversions() {
        final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
        converters.add(new ZonedDateTimeReadConverter());
        converters.add(new ZonedDateTimeWriteConverter());
        return new MongoCustomConversions(converters);
    }

	@Override
    public void run(String... args) throws Exception {

	    Customer customer = new Customer("1234", "Alice", "Smith", "Some Street 10", "Houston", "77001");
	    
        // save a couple of customers
	    service.saveCustomer(customer, LocalDate.now(), EffectivePeriod.INFINITE);
	    service.saveCustomer(customer, LocalDate.now().plusDays(10), EffectivePeriod.INFINITE);
	    service.saveCustomer(customer, LocalDate.now().plusDays(20), EffectivePeriod.INFINITE);
	    
	    // query the state of the journal
	    Assert.isTrue(repository.findByClientIdAndBitemporalStampRecordTimeState("1234", BitemporalObjectState.ACTIVE).size() == 3, "must contain 3 active records");
	    Assert.isTrue(repository.findByClientIdAndBitemporalStampRecordTimeState("1234", BitemporalObjectState.INACTIVE).size() == 2, "must contain 2 inactive records");

	    System.out.println(repository.findAll().toString());
    }
}
