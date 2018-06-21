# BRMS DMN Example

This is based on the first version (currently in beta3) of the Drools DMN funcionality. It shows various DMN Constructs

├── 0001-input-data-string.dmn
├── 0004-simpletable-U.dmn
├── BuildingStructureNotification.dmn
├── marinaexploitation 					(The test is disbaled as imports have not been yet implemented)
│   ├── Marina-applicability.dmn
│   ├── Marina-permit.dmn
│   └── Permissions-needed-with-import.dmn 	
├── MarinaExploitationPermit.dmn			(A scenario Testing the Exploitation Permit with two Decisions and one feeding input to another)
├── notification4.dmn
├── NotificationsTest2.dmn
├── NotificationsTest3.dmn
└── Permissions-needed-one-file.dmn                    (A full permissions test)



### Building

The examples can be built with

    mvn clean install


### Running the example locally

   mvn test
