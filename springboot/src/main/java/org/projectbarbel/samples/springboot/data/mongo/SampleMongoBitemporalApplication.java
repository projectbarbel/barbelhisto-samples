/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectbarbel.samples.springboot.data.mongo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.projectbarbel.histo.model.BitemporalObjectState;
import org.projectbarbel.histo.model.EffectivePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.MongoConfigurationSupport;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.util.Assert;

@SpringBootApplication
public class SampleMongoBitemporalApplication extends MongoConfigurationSupport implements CommandLineRunner {

    @Autowired
    private CustomerService service;
    
    @Autowired
    private CustomerRepository repository;
    

    public static void main(String[] args) throws Exception {
	    SpringApplication.run(SampleMongoBitemporalApplication.class, args);
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

    }
}
