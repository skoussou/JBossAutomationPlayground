-- INITIAL TASK CHECK IN MaestroRHPAM
-- select id, name, deploymentId, processId, processInstanceId, processSessionId, status, taskInitiator_id, actualOwner_id, createdBy_id from Task;

-- SELECT Specific Task Audit entry
select taskId, createdOn, lastModificationDate, status, actualOwner, deploymentId, processId, name, processInstanceId  from AuditTaskImpl where id = '56'
-- select Task Event for specific task (ADDED, STARTED, CLAIMED etc.)
select * from TaskEvent where taskId = '56';
-- variables for specific task
select * from TaskVariableImpl where taskId = '56';

-- all audited variables
select * from TaskVariableImpl;
-- all audited Task Events
select * from TaskEvent;
-- all Audited Tasks
select taskId, processInstanceId, createdOn, lastModificationDate, status, actualOwner, deploymentId, processId, name  from AuditTaskImpl;
-- Process Instance Log
select correlationKey, externalId, processId, processInstanceId, processType, processVersion, status from ProcessInstanceLog;