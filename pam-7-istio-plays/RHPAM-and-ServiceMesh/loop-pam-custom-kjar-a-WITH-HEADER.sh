#!/bin/bash

LOOPS=$1
APPS_NAMESPACE=$2
APP_SUBDOMAIN=$3
for i in {1..100};
do

# watch -n1 "curl -v -H 'bizversion: version-kjar-a-110'  http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${APP_SUBDOMAIN}/docs"
# watch -n1 "curl -v http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${APP_SUBDOMAIN}/docs"

done

