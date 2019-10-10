package org.projectbarbel.samples.springboot.listener;

import java.time.ZonedDateTime;

import org.projectbarbel.histo.BarbelHisto;
import org.projectbarbel.samples.springboot.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerService {

    @Autowired
    private BarbelHisto<Customer> barbel;

    public void saveCustomer(Customer customer, ZonedDateTime from, ZonedDateTime until) {

        barbel.save(customer, from, until);

    }

}
