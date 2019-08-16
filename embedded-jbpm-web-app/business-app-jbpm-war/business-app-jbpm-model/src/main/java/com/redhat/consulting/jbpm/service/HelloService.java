package com.redhat.consulting.jbpm.service;

import com.redhat.consulting.jbpm.data.Person;



public class HelloService {
	private static final HelloService INSTANCE = new HelloService();

	public static HelloService getInstance() {
		return INSTANCE;
	}

	public void sayHello(String name) {
		System.out.println("sayHello : " + name);
	}

	public void sayHelloToPerson(Person name) {
		System.out.println("sayHelloToPerson : " + name.getName());
	}
}
