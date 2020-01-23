#!/bin/bash
for i in {1..10000}
do
   echo "curl -u 'adminUser:RedHat'  -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_2.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-$i\",    \"reason\": \"some-reason\"}""

   curl -u 'adminUser:RedHat' -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_2.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-$i\",    \"reason\": \"some-reason\"}"



done
