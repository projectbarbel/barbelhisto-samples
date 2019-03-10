package org.projectbarbel.samples.springboot.support;

import org.bson.Document;
import org.projectbarbel.histo.BarbelHistoContext;
import org.projectbarbel.samples.springboot.model.Customer;
import org.springframework.core.convert.converter.Converter;

import com.google.gson.Gson;

public class CustomerReadConverter implements Converter<Document, Customer>{

    private Gson gson = BarbelHistoContext.getDefaultGson();
    @Override
    public Customer convert(Document source) {
        return gson.fromJson(source.toJson(), Customer.class);
    }

}
