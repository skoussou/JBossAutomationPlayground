package com.redhat;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

public class CustomProcessEventListener extends DefaultProcessEventListener{


    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        //System.out.println("         ---> PID ["+event.getProcessInstance().getProcessId()+"] Node "+event.getNodeInstance().getNodeName()+" was LEFT");
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.out.println("         ---> PID ["+event.getProcessInstance().getProcessId()+"] Node "+event.getNodeInstance().getNodeName()+" was Triggered");
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        System.out.println("         ---> PID ["+event.getProcessInstance().getProcessId()+"] was Completed");
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        System.out.println("         ---> PID ["+event.getProcessInstance().getProcessId()+"] was Started");
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        System.out.println("         ---> PID ["+event.getProcessInstance().getProcessId()+"] Variable ["+event.getVariableId()+"] changed value from ["+event.getOldValue()+"] --> ["+event.getNewValue()+"]");
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        //System.out.println("         ---> PID ["+event.getProcessInstance().getProcessId()+"] Node "+event.getNodeInstance().getNodeName())+" is about to be LEFT");
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        //System.out.println("         ---> PID ["+event.getProcessInstance().getProcessId()+"] Node "+event.getNodeInstance().getNodeName())+" is TRIGGERED");
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        // intentionally left blank
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {
        // intentionally left blank
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // intentionally left blank
    }
    
}
