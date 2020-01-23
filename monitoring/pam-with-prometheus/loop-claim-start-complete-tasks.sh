#!/bin/bash
for i in {1..10000}
do

   echo "curl -X PUT "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_2.0.0-SNAPSHOT/tasks/$i/states/completed?auto-progress=true" -H  "accept: application/json" -H  "content-type: application/json" -d "{}""

   curl -u 'adminUser:RedHat' -X PUT "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_2.0.0-SNAPSHOT/tasks/$i/states/completed?auto-progress=true" -H  "accept: application/json" -H  "content-type: application/json" -d "{}"


done
