#!/bin/bash

LOOPS=$1
APPS_NAMESPACE=$2
APP_SUBDOMAIN=$3
for i in {1..100};
do
  curl -v http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${APP_SUBDOMAIN}/docs
done

