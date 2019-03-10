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

package org.projectbarbel.samples.springboot.listener;

import java.util.List;

import org.projectbarbel.histo.model.Bitemporal;
import org.projectbarbel.histo.model.BitemporalObjectState;
import org.projectbarbel.samples.springboot.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

	public List<Customer> findByLastName(String lastName);
	
	public List<Bitemporal> findByClientId(String clientId);
	
	public List<Bitemporal> findByClientIdAndBitemporalStampRecordTimeState(String clientId, BitemporalObjectState state);
	
}
