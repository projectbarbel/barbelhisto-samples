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

import org.projectbarbel.histo.DocumentId;
import org.projectbarbel.histo.model.Bitemporal;
import org.projectbarbel.histo.model.BitemporalStamp;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Customer implements Bitemporal {

    @Id
    private String id;
    
	@DocumentId
	private String clientId;
	
	// version stamp
	private BitemporalStamp bitemporalStamp;
	
	private String firstName;
	private String lastName;
	private String street;
	private String city;
	private String postalcode;
    public Customer(String clientId, String firstName, String lastName, String street, String city, String postalcode) {
        super();
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.postalcode = postalcode;
    }
	
}
