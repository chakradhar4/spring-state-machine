package com.workflow;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineSystemConstants;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.util.ObjectUtils;

@Configuration
@EnableStateMachine
public class TasksConfig extends StateMachineConfigurerAdapter<String,String>  {

	@Override
	public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
		states
		       .withStates()
				.initial("READY")
				.fork("FORK")
				.state("TASKS")
				.join("JOIN")
				.choice("CHOICE")
				.state("ERROR")
				.and()
						.withStates()
						.parent("TASKS")
						.initial("T1")
						.end("T1E")
			    .and()
			    .withStates()
			    		.parent("TASKS")
			    		.initial("T2")
			    		.end("T2E")
			   .and()
			   .withStates()
			   			.parent("ERRO")
			   			.initial("AUTOMATIC")
			   			.state("AUTOMATIC",automaticAction(),null)
			   			.state("MANUAL");
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
		transitions
		           .withExternal()
		           		.source("READY").target("FORK").event("FORK")
		     .and().withFork()
		                .source("FORK").target("TASKS")
		     .and().withExternal()
		     			.source("T1").target("T1E")
		    .and().withExternal()
		    			.source("T2").target("T2E")
		    .and().withExternal()
		    			.source("T3").target("T3E")
		   .and().withJoin()
		    			.source("TASKS").target("JOIN")
		   .and().withChoice()
		   				.source("CHOICE").first("ERROR",taskChoiceGuard())
						.last("READY")
		   .and().withExternal()
		   				.source("ERROR")
		   				.target("READY")
		   				.event("CONTINUE")
		   .and().withExternal()
		   .source("AUTOMATIC").target("MANUAL").event("FALLBACK")
		   			.and().withInternal().source("MANUAL").action(fixAction()).event("FiX");	
		    	
	}
	
	@Bean
	public Guard<String,String> taskChoiceGuard(){
		 return new Guard<String,String>(){

			@Override
			public boolean evaluate(StateContext<String, String> context) {
				  Map<Object,Object> variables = context.getExtendedState().getVariables();
				  return !(ObjectUtils.nullSafeEquals(variables.get("T1"),true)
						  && ObjectUtils.nullSafeEquals(variables.get("T2"),true)
						  && ObjectUtils.nullSafeEquals(variables.get("T3"),true));
			}
		 };
	}
	
	
	@Bean
	public Action<String,String> automaticAction() {
		return new Action<String,String>() {

			@SuppressWarnings("deprecation")
			public void execute(StateContext<String,String> context) {
				Map<Object, Object> variables = context.getExtendedState().getVariables();
				if (ObjectUtils.nullSafeEquals(variables.get("T1"), true)
						&& ObjectUtils.nullSafeEquals(variables.get("T2"), true)
						&& ObjectUtils.nullSafeEquals(variables.get("T3"), true)) {
					context.getStateMachine().sendEvent("CONTINUE");
				} else {
					context.getStateMachine().sendEvent("FALLBACK");
				}
			}
		};
	}

	@Bean
	public Action<String,String> fixAction(){
		return new Action<String,String>(){ 

			@SuppressWarnings("deprecation")
			@Override
			public void execute(StateContext<String, String> context) {
				Map<Object, Object> variables = context.getExtendedState().getVariables();
				variables.put("T1", true);
				variables.put("T2", true);
				variables.put("T3", true);
				context.getStateMachine().sendEvent("CONTINUE");
				
			}
		};
	}
	
	@Bean(name = StateMachineSystemConstants.DEFAULT_ID_EVENT_PUBLISHER)
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(5);
		return taskExecutor;
	}
}
