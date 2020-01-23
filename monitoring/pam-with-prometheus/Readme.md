# Visualize & Monitor RH PAM Project Stats with Prometheus & Grafana


Inspired by documentation managing_and_monitoring_process_server https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.6/html-single/managing_and_monitoring_process_server/index#prometheus-monitoring-con_execution-server

* Inspired by [documentation managing_and_monitoring_process_server](https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.6/html-single/managing_and_monitoring_process_server/index#prometheus-monitoring-con_execution-server)
* Also locally in repo available in this repository [Red_Hat_Process_Automation_Manager-7.6-Managing_and_monitoring_Process_Server-en-US.pdf](.Red_Hat_Process_Automation_Manager-7.6-Managing_and_monitoring_Process_Server-en-US.pd)



## Setup PAM for DEv & Runtime

* Setup In OCP
  * Operator based installation [documentation managing_and_monitoring_process_server](https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.6/html-single) 
  * non Operator based installer [installer](http://github.com/jbossdemocentral/rhpam7-install-demo)
 
* On Prem
  * [installer](http://github.com/jbossdemocentral/rhpam7-install-demo)

* Import Evaluation_Process Business Project in BC 
  * Import Examples --> Evaluations
  * Update 'evaluations' Business Process all tasks to have as GROUP 'kie-server' and remove all other possible ownership (eg. ActorId or group)
  * deploy on KIE Server
* Ensure the following property is configured (dependent on environment)


```bash
vim ./org.drools-droolsjbpm-integration-7.26.0.Final/kie-server-parent/kie-server-services/kie-server-services-prometheus/src/main/java/org/kie/server/services/prometheus

SPRINGBOOT
kieserver.prometheus.enabled=true (application.properties)

KIE Server
NON-OCP: org.kie.prometheus.server.ext.disabled=false 
OCP:         servers:
    - env:
      - name: PROMETHEUS_SERVER_EXT_DISABLED
        value: "false"
```



* Test metrics working by calling the following to create processes and accessing 'KIESERVER-ROUTE/services/rest/metrics
```bash
curl -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_1.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-2\",    \"reason\": \"some-reason\"}"
```

  * Also generate processes in a loop (see [loop-process-creation.sh](./scripts/loop-process-creation.sh))

```bash
#!/bin/bash
for i in {1..5}
do
   echo "curl -u 'adminUser:RedHat'  -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_1.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-$i\",    \"reason\": \"some-reason\"}""

   curl -u 'adminUser:RedHat' -X POST "http://enable-prometheus-kieserver-http-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com/services/rest/server/containers/evaluation_1.0.0-SNAPSHOT/processes/evaluation/instances" -H  "accept: application/json" -H  "content-type: application/json" -d "{    \"employee\": \"employee-$i\",    \"reason\": \"some-reason\"}"

done
```


## Setup prometheus to monitor KIE Server(s)

see Prometheus [Installation & Configuration to pull RHPAM Metrics](./Install-Prometheus.md)



## Setup monitoring with GRafana

### Install Grafana


* Install Grafana App
```bash
oc new-app grafana/grafana && oc expose svc/grafana
```

* Verify Grafana is up and running:

```bash
oc rollout status -w dc/grafana
```

You should see replication controller "grafana-1" successfully rolled out

* Open Grafana Dashboard (via ROUTE)
Click on this link to open the Grafana Dashboard in your browser, and login using the default credentails:

Username: admin
Password: admin
Grafana UI
At the password change prompt, use any password you wish.

* Add Prometheus as a data source
You’ll land on the Data Source screen. Click Add Data Source, and select Prometheus as the Data Source Type.

  * Connect promethes with grafana
   * In the URL box, type http://prometheus:9090 (this is the hostname and port of our running Prometheus in our namespace):
   * SVC (Not working) http://prometheus.dev-rhpam-operator.svc.cluster.local)
   * ROUTE (Working) http://prometheus-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com


* Grafana UI
Click Save and Test. You should see:


### Create Dashboards

For more info on metrics to use see [How-To-Monitor.md](./How-To-Monitor.md) and Configuring Prometheus metrics monitoring for Process Server on Red Hat OpenShift Container Platform](https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.6/html-single/managing_and_monitoring_process_server/index#prometheus-monitoring-ocp-proc_execution-server)
then see for examples of dashboards
```bash
├── ready-dashboards
│   ├── RHPAM-Evaluations-Process&Monitoring-GrafanaDashBoard-API-Based-Templete.json
│   └── RHPAM-Evaluations-Process&Monitoring-GrafanaDashBoard.json
```
![RHPAM Dashboard](./images/prometheus-grafana-rhpam-monitoring.png)

### Create Traffic for the metrics

Note, you need to modify the script below to point to your KIE Server

* Run first [./scripts/loop-process-creation.sh](./scripts/loop-process-creation.sh)
* Monitor the dashboard for a little while before
* Run the [./scripts/loop-claim-start-complete-tasks.sh](./scripts/loop-process-creation.sh) to claim/complete tasks




