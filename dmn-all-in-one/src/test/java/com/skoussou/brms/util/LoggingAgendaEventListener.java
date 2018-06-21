package com.skoussou.brms.util;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAgendaEventListener extends DefaultAgendaEventListener { 

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAgendaEventListener.class);
	
	/*
	@Override
	public void matchCreated(MatchCreatedEvent event) {
		super.matchCreated(event);
		LOGGER.debug("Match created: " + event.getMatch().getRule().getName());
	}
	*/
	
	
	@Override
	public void beforeMatchFired(BeforeMatchFiredEvent event) {
		super.beforeMatchFired(event);
		//LOGGER.debug("After match fired: " + event.getMatch().getRule().getName());
		System.out.println("Match found: " + event.getMatch().getRule().getName());	}
	
	/*
	@Override
	public void afterMatchFired(AfterMatchFiredEvent event) {
		super.afterMatchFired(event);
		//LOGGER.debug("After match fired: " + event.getMatch().getRule().getName());
		System.out.println("After match fired: " + event.getMatch().getRule().getName());
	}*/
}
