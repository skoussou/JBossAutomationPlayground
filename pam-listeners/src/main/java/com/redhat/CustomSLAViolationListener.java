package com.redhat;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;

public class CustomSLAViolationListener extends DefaultProcessEventListener{

    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {
    	System.out.println("PID ["+event.getProcessInstance().getProcessId()+"] violated SLA ");
    }
}
