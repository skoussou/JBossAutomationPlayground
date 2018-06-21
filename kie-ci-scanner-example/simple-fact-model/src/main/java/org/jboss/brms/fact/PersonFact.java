package org.jboss.brms.fact;

public class PersonFact {

	private String name;
	private Integer age;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		if (age == null) {
			age = new Integer(0);
		}
		return age+2000;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	
}
