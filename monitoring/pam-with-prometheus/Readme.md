# Visualize & Monitor RH PAM Project Stats with Prometheus & Grafana


Inspired by documentation at https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.6/html-single/managing_and_monitoring_process_server/index#prometheus-monitoring-con_execution-server

also locally in repo available Red_Hat_Process_Automation_Manager-7.6-Managing_and_monitoring_Process_Server-en-US.pdf

## Setup PAM for DEv & Runtime

- Setup In OCP

  Operator based: https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.6/html-single/deploying_a_red_hat_process_automation_manager_environment_on_red_hat_openshift_container_platform_using_operators/index

- On Prem
installer

- Import Evaluation_Process in BC 

   one of the existing example
   update all tasks to have as GROUP 'kie-server'
   deploy on KIE Server


## Setup prometheus to monitor KIE Server(s)

see Install-Prometheus.md



## Setup monitoring with GRafana

### Install Grafana

oc new-app grafana/grafana && oc expose svc/grafana

Verify Grafana is up and running:

oc rollout status -w dc/grafana

You should see replication controller "grafana-1" successfully rolled out

Open Grafana Dashboard
Click on this link to open the Grafana Dashboard in your browser, and login using the default credentails:

Username: admin
Password: admin
Grafana UI
At the password change prompt, use any password you wish.

Add Prometheus as a data source
You’ll land on the Data Source screen. Click Add Data Source, and select Prometheus as the Data Source Type.

In the URL box, type http://prometheus:9090 (this is the hostname and port of our running Prometheus in our namespace):

Connect promethes with grafana (
- SVC (Not working) http://prometheus.dev-rhpam-operator.svc.cluster.local)
- ROUTE (Working) http://prometheus-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com


Grafana UI
Click Save and Test. You should see:

Grafana UI

### Create Dashboards

see How-To-Monitor.md
then see

├── ready-dashboards
│   ├── RHPAM-Evaluations-Process&Monitoring-GrafanaDashBoard-API-Based-Templete.json
│   └── RHPAM-Evaluations-Process&Monitoring-GrafanaDashBoard.json


### Create Traffic for the metrics

![RHPAM Dashboard](./images/prometheus-grafana-rhpam-monitoring.png)




