Start Loop for Processes

Start Loop for Tasks auto Claim-Start-Complete

Connect promethes with grafana (
- SVC (Not working) http://prometheus.dev-rhpam-operator.svc.cluster.local)
- ROUTE (Working) http://prometheus-dev-rhpam-operator.apps.cluster-instabul-9983.instabul-9983.example.opentlc.com

STATS
===================


--------------
TASKS
--------------
# TYPE kie_server_data_set_execution_time_seconds summary
kie_server_data_set_execution_time_seconds_count{uuid="tasksMonitoring",} 56.0
kie_server_data_set_execution_time_seconds_sum{uuid="tasksMonitoring",} 2.8059999999999996
kie_server_data_set_execution_time_seconds_count{uuid="jbpmHumanTasksWithUser",} 48.0
kie_server_data_set_execution_time_seconds_sum{uuid="jbpmHumanTasksWithUser",} 124.89800000000001
kie_server_data_set_execution_time_seconds_count{uuid="jbpmHumanTasksWithAdmin",} 8.0
kie_server_data_set_execution_time_seconds_sum{uuid="jbpmHumanTasksWithAdmin",} 0.12400000000000001


# TYPE kie_server_task_duration_seconds summary
kie_server_task_duration_seconds_count{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="HR Evaluation",} 35.0
kie_server_task_duration_seconds_sum{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="HR Evaluation",} 5502.030999999999
kie_server_task_duration_seconds_count{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="PM Evaluation",} 35.0
kie_server_task_duration_seconds_sum{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="PM Evaluation",} 5512.688
kie_server_task_duration_seconds_count{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="Self Evaluation",} 229.0
kie_server_task_duration_seconds_sum{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="Self Evaluation",} 25857.7039999999

 TYPE kie_server_task_added_total counter
kie_server_task_added_total{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="HR Evaluation",} 229.0
kie_server_task_added_total{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="PM Evaluation",} 229.0
kie_server_task_added_total{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="Self Evaluation",} 674.0

# HELP kie_server_task_completed_total Kie Server Completed Tasks
# TYPE kie_server_task_completed_total counter
kie_server_task_completed_total{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="HR Evaluation",} 1032.0
kie_server_task_completed_total{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="PM Evaluation",} 1032.0
kie_server_task_completed_total{deployment_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",task_name="Self Evaluation",} 2084.0


--------------
PROCESSES
--------------
# HELP kie_server_process_instance_running_total Kie Server Running Process Instances
# TYPE kie_server_process_instance_running_total gauge
kie_server_process_instance_running_total{container_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",} 639.0

# HELP kie_server_process_instance_completed_total Kie Server Completed Process Instances
# TYPE kie_server_process_instance_completed_total counter
kie_server_process_instance_completed_total{container_id="evaluation_2.0.0-SNAPSHOT",process_id="evaluation",status="2",} 35.0









