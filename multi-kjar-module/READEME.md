# BRMS Multi KieModule Example

This multi-module project  demonstrates how to segragate BRMS rules based on the focus area and then combine them to Rule Sets for specific business scenarios

### The scenario

- central-module: Contains rules from a central government law body applicable to all munincipalities
- municipality-module: Contains rules defined by the local municipality law body applicable only to the local activities
- laws-module: Contains the business objects shared by all rules

#### Combining rules

The two rule application components (central-module, municipality-module) contain the DRL files with the law rules and in addition a kmodule.xml descriptor file 
to define how these rules will end up in KnowledgeBases. In BRMS in order to import the rules of one knowledgeBase to another you need to include the knowledgeBase
name. See on the following paths the definitions and combinations of knowledgeBase rules.

./municipality-module/src/main/resources/META-INF/kmodule.xml
./central-module/src/main/resources/META-INF/kmodule.xml

Note it is crucial to also ensure the correct packages (namespaces) are defined on top of including just the KBase name as otherwise althought the rules are made
available the KieSession created for the runtime execution will only include rules from the defined (in the KBase) packages (namespaces).

### Building

The example can be built with

    mvn clean install



