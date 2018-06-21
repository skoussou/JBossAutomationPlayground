package com.skoussou.brms.util;

import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;

/**
 * 
 * @author skoussou@gmail.com:Wq
 *
 */
public class RulesFiredListernerHelper extends DefaultAgendaEventListener {

	private long nrOfRulesFired = 0;

    private String ruleName = null;

    @Override
    public synchronized void afterMatchFired(AfterMatchFiredEvent event) {
         nrOfRulesFired++;
         this.ruleName = event.getMatch().getRule().getName();
    }

    public long getNrOfRulesFired() {
        return nrOfRulesFired;
    }

    public void setNrOfRulesFired(long nrOfRulesFired) {
        this.nrOfRulesFired = nrOfRulesFired;
    }

    public String getRulesFiredName(){ return ruleName;}



}


