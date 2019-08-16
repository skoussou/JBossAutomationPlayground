package com.redhat.consulting.jbpm.serialization;

import java.util.ArrayList;
import java.util.List;

//import org.codehaus.jackson.map.ObjectMapper;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.process.ProcessVariableIndexer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.consulting.jbpm.data.Person;
import com.thoughtworks.xstream.XStream;

public class PersonProcessVariablesIndexer implements ProcessVariableIndexer {

	ObjectMapper objectMapper = new ObjectMapper();
	XStream xstream = new XStream();

	@Override
	public boolean accept(Object variable) {
		if (variable instanceof Person) {
			return true;
		}

		return false;
	}

	@Override
	public List<VariableInstanceLog> index(String name, Object variable) {
		List<VariableInstanceLog> indexed = new ArrayList<VariableInstanceLog>();

		Person person = (Person) variable;

		org.jbpm.process.audit.VariableInstanceLog personVar = new org.jbpm.process.audit.VariableInstanceLog();
		personVar.setVariableId(name);
		personVar.setValue(person.toString());
		indexed.add(personVar);

		org.jbpm.process.audit.VariableInstanceLog personNameVar = new org.jbpm.process.audit.VariableInstanceLog();
		personNameVar.setVariableId(name + ".name");
		personNameVar.setValue(person.getName());
		indexed.add(personNameVar);

		org.jbpm.process.audit.VariableInstanceLog personAgeVar = new org.jbpm.process.audit.VariableInstanceLog();
		personAgeVar.setVariableId(name + ".age");
		personAgeVar.setValue(person.getAge() + "");
		indexed.add(personAgeVar);

		org.jbpm.process.audit.VariableInstanceLog personJSONVar = new org.jbpm.process.audit.VariableInstanceLog();
		personJSONVar.setVariableId(name);
		personJSONVar.setValue(xstream.toXML(person));

		indexed.add(personJSONVar);

		return indexed;
	}
}