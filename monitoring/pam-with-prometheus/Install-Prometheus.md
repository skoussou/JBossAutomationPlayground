To install it, first create a Kubernetes ConfigMap that will hold the Prometheus configuration. Click on the following command to create this file:

USING PROMETHEUS OPERATOR & Service & ServiceMonitor to expose RHPAM metrics
-----------------------------------------------------------------------------
cat <<EOF > prometheus.yml
global:
  scrape_interval:     15s
  evaluation_interval: 15s
alerting:
  alertmanagers:
  - static_configs:
    - targets:
scrape_configs:
  - job_name: 'prometheus'
    static_configs:
    - targets: ['localhost:9090']
  - job_name: 'enable-prometheus-kieserver'
    basic_auth:
      username: adminUser
      password: RedHat
    static_configs:
    - targets: ['rhpam-app-metrics:8080']
EOF

USING Non-Operator based prometheus & directly scrping from KIE Servers
-----------------------------------------------------------------------------
global:
  scrape_interval:     15s
  evaluation_interval: 15s
alerting:
  alertmanagers:
  - static_configs:
    - targets:
scrape_configs:
  - job_name: 'prometheus'
    static_configs:
    - targets: ['localhost:9090']
  - job_name: 'enable-prometheus-kieserver'
    metrics_path: /services/rest/metrics
    basic_auth:
      username: adminUser
      password: RedHat
    static_configs:
    - targets: ['enable-prometheus-kieserver:8080']




This file contains basic Prometheus configuration, plus a specific scrape_configwhich instructs Prometheus to look for application metrics from both Prometheus itself, and a Quarkus app called primes which we'll create later, on HTTP port 8080 at the /metrics endpoint.

Next, click this command to create a ConfigMap with the above file:

---
oc create configmap prom --from-file=prometheus.yml=.prometheus.yml
---

Deploy Prometheus
Next, deploy and expose Prometheus using its public Docker Hub image:

---
oc new-app prom/prometheus && oc expose svc/prometheus
---

And finally, mount the ConfigMap into the running container:

---
oc set volume dc/prometheus --add -t configmap --configmap-name=prom -m /etc/prometheus/prometheus.yml --sub-path=prometheus.yml
---

This will cause the contents of the ConfigMap data to be mounted at /etc/prometheus/prometheus.yml inside its container where Prometheus is expecting it.

Verify Prometheus is up and running:

---
oc rollout status -w dc/prometheus
---

You should see replication controller "prometheus-2" successfully rolled o




curl -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_1.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-2\",    \"reason\": \"some-reason\"}"

#!/bin/bash
for i in {1..5}
do
   echo "curl -u 'adminUser:RedHat'  -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_1.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-$i\",    \"reason\": \"some-reason\"}""

   curl -u 'adminUser:RedHat' -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_1.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-$i\",    \"reason\": \"some-reason\"}"



done




