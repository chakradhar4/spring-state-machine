package com.workflow;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;


public class CDplayer extends StateMachineConfigurerAdapter<String,String>{

	@Override
	public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
	       
	}
               
}
