package com.workflow;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer.History;


public class WasherConfig extends StateMachineConfigurerAdapter<String,String> {

	@Override
	public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
		states
			   .withStates()
			   .initial("START")
			   .state("RUNNING")
			   .state("POWEROFF")
			   .end("END")
			   .and()
			   .withStates()
			   			.parent("RUNNING")
			   			.initial("WASHING")
			   			.state("RINSING")
			   			.state("DRYING")
			   		    .history("HISTORY",History.SHALLOW );
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
		transitions
		  			.withExternal()
		  			.source("START").target("RUNNING").event("ON")
		  			.and()
		  			.withExternal().source("WASHING").target("RINSING").event("RINSE")
		  			.and()
		  			.withExternal().source("RINSING").target("DRYING").event("DRY")
		  			.and()
		  			.withExternal().source("RUNNING").target("POWEROFF").event("CUTPOWER")
		  			.and()
		  			.withExternal().source("POWEROFF").target("HISTORY").event("RESTOREPOWER")
		  			.and()
		  			.withExternal().source("RUNNING").target("END").event("OFF");
	}
}
