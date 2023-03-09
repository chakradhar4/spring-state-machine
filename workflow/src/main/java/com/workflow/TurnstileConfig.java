package com.workflow;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;


public class TurnstileConfig extends StateMachineConfigurerAdapter<String,String>{

	@Override
	public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
		  states
		        .withStates()
		        .initial("LOCK")
		        .state("UNLOCK");
		         
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
		transitions
					.withExternal().source("LOCK").target("UNLOCK").event("COIN")
					.and()
					.withExternal().source("UNLOCK").target("LOCK").event("EXIT");
	}
	

	
	
   
}
