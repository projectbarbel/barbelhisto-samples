package org.projectbarbel.samples.springboot.listener;

import java.time.LocalDate;

import org.projectbarbel.histo.BarbelHisto;
import org.projectbarbel.histo.BarbelHistoBuilder;
import org.projectbarbel.histo.BarbelMode;
import org.projectbarbel.histo.model.BitemporalObjectState;
import org.projectbarbel.histo.model.BitemporalUpdate;
import org.projectbarbel.samples.springboot.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public void saveCustomer(Customer customer, LocalDate from, LocalDate until) {

        // create BarbelHisto helper instance
        BarbelHisto<Customer> bitemporalHelper = BarbelHistoBuilder.barbel().withMode(BarbelMode.BITEMPORAL).build();

        // load active records of the current Customer journal
        bitemporalHelper.load(customerRepository.findByClientIdAndBitemporalStampRecordTimeState(customer.getClientId(),
                BitemporalObjectState.ACTIVE));

        // make a bitemporal update
        BitemporalUpdate<Customer> update = bitemporalHelper.save(customer, from, until);

        // replace inactivated versions
        update.getInactivations().stream().forEach(i -> customerRepository.save(i));

        // prepare inserts: clear IDs of new version records
        update.getInserts().stream().forEach(d -> d.setId(null));

        // perform inserts of new version data
        customerRepository.insert(update.getInserts());

    }

}
