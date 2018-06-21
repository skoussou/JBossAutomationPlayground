# BRMS Kie Scanner CI Example

# TODO - Don't put on github it needs work

test-kie-scanner: uses two rule project

releaseId = kieServices.newReleaseId("org.jboss.pmenon.brms", "simple-rules", "LATEST");
releaseIdSim = kieServices.newReleaseId("org.jboss.pmenon.brms", "simple-rules-sim", "LATEST");

which basically are the same project but simply changing the artifact-id

and also using the simple-fact-model to showcase rule updates via kie-scanner when changes are done on either the model OR the rules

Some changes are required to happen in
PersonFact : for model changes
see (public Integer getAge() {
		if (age == null) {
			age = new Integer(0);
		}
		return age+2044;
	}
)

scanner-rule.dlr : to showcase rule updates (version change is needed and same as pom.xml)
rule "always fire"
	when
		eval(true)
	then
		System.out.println("RULE V 1.19");
end




