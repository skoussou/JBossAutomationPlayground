-- Queries in maestro APP DB that nothing has been saved
------------------------------------------------------------
select InstanceId, processId from ProcessInstanceInfo ORDER BY InstanceId ASC;
select taskId, processInstanceId, createdOn, lastModificationDate, status, actualOwner, deploymentId, processId, name  from AuditTaskImpl;
--  Task
select id, name, deploymentId, processId, processInstanceId, status, actualOwner_id, createdBy_id from Task ORDER BY id ASC;


-- SELECT Specific Task Audit entry
select taskId, createdOn, lastModificationDate, status, actualOwner, deploymentId, processId, name, processInstanceId  from AuditTaskImpl where id = '61'
-- select Task Event for specific task (ADDED, STARTED, CLAIMED etc.)
select * from TaskEvent where taskId = '61';
-- variables for specific task
select * from TaskVariableImpl where taskId = '61';

-- New Instance for process
------------------------------------------------------------
select InstanceId, processId, lastModificationDate from ProcessInstanceInfo ORDER BY InstanceId ASC;

-- ProcessInstanceLog EMPTY in RHPAM Runtime DB
select correlationKey, externalId, processId, processInstanceId, processType, processVersion, status from ProcessInstanceLog;

-- Timers checking
------------------------------------------------------------
select id, TIMED_OBJECT_ID, INITIAL_DATE, REPEAT_INTERVAL, TIMER_STATE, PARTITION_NAME, SCHEDULE_EXPR_SECOND from JBOSS_EJB_TIMER