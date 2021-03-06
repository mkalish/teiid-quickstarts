/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.client.quickstart.hibernate4.controller;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.hibernate.Session;

import com.client.quickstart.hibernate4.model.Product;

// The @Stateful annotation eliminates the need for manual transaction demarcation
@Stateful
// The @Model stereotype is a convenience mechanism to make this a
// request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://sfwk.org/Documentation/WhatIsThePurposeOfTheModelAnnotation
@Model
public class ProductRegistration {

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	@Inject
	private Event<Product> ProductEventSrc;

	private Product newProduct;

	@Produces
	@Named
	public Product getNewProduct() {
		return newProduct;
	}

	@Produces
	public void register() throws Exception {
		log.info("Registering " + newProduct.getCompanyName());

		// using Hibernate session(Native API) and JPA entitymanager
		Session session = (Session) em.getDelegate();
		session.persist(newProduct);

		try {
			ProductEventSrc.fire(newProduct);
		} catch (Exception e) {
			log.info("Registration Failed!You may have tried to insert a duplicate Product!!!");
			e.printStackTrace();
		}

		initNewProduct();
	}

	@PostConstruct
	public void initNewProduct() {
		newProduct = new Product();
	}
}
