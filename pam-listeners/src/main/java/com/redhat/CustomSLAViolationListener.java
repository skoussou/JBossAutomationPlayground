package com.redhat;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;

public class CustomSLAViolationListener extends DefaultProcessEventListener{


    /**
     * This listener method is invoked right before a process/node instance's SLA has been violated.
     * @param event
     */
    @Override
    public void beforeSLAViolated(SLAViolatedEvent event){
       //System.out.println("PID ["+event.getProcessInstance().getProcessId()+"] ** ABOUT TO ** violate SLA ");
    }
    
    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {
    	System.out.println("PID ["+event.getProcessInstance().getProcessId()+"] violated SLA ");
    }
}
