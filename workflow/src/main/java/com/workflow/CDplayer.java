package com.workflow;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;


public class CDplayer extends StateMachineConfigurerAdapter<String,String>{

	@Override
	public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
	       states
	        	.withStates()
	        	  .initial("IDLE")
	        	  .state("IDLE")
	        	  .and()
	        	  .withStates()
	        	  		.parent("IDLE")
	        	  		.initial("CLOSED")
	        	  		.state("CLOSED",closedEntryAction(),null)   //ClosedEntryAction is an entry action for the CLOSED state, to send a PLAY event to the state machine if a disc is present
	        	  		.state("OPEN")
	        	  		.and()
	        	  .withStates()
	        	  	.state("Busy")
	        	  	.and()
	        	  	.withStates()
	        	  		.parent("BUSY")
	        	  		.initial("PLAYING")
	        	  		.state("PLAYING")
	        	  		.state("PAUSED");  	  		
	}
	
	
	
	@Override
	public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
		transitions
				.withExternal().source("CLOSED").target("OPEN").event("EJECT")
			.and()
				.withExternal().source("OPEN").target("CLOSED").event("EJECT")
			.and()
				.withExternal().source("OPEN").target("CLOSED").event("PLAY")
			.and()
			    .withExternal().source("PLAYING").target("PAUSED").event("PAUSE")
			.and()
			    .withInternal().source("PLAYING").action(playingAction()).timer(1000)    //PlayingAction updates an extended state variable called ELAPSEDTIME, which the player can use to read and update its LCD status display. PlayingAction also handles track shifting when the user goe back or forward in tracks
			.and()
			    .withInternal().source("PLAYING").event("BACK").action(trackAction())  
			.and()
			    .withInternal().source("PLAYING").event("FORWARD").action(trackAction())
			.and()
				.withExternal().source("BUSY").target("IDLE").event("STOP")
			.and()
				.withExternal().source("IDLE").target("BUSY").event("PLAY")
				.action(playAction())		//PlayAction resets the player’s elapsed time
				.guard(playGuard())
			.and()
				.withInternal().source("OPEN").event("LOAD").action(loadAction());
			   		
	}
	
	
	@Bean
	public ClosedEntryAction closedEntryAction() {
		return new ClosedEntryAction();
	}
	
	@Bean
	public PlayingAction playingAction() {
		return new PlayingAction();
	}
			
	@Bean
	public PlayAction playAction() {
		return new PlayAction();
	}
	
	@Bean
	public TrackAction trackAction() {
		return new TrackAction();
	}
	
	@Bean
	public PlayGuard playGuard() {
		return new PlayGuard();
	}
	
	@Bean
	public LoadAction loadAction() {
		return new LoadAction();
	}
	
	
	
	public static class ClosedEntryAction implements Action<String,String>{

		@Override
		public void execute(StateContext<String, String> context) {
				if(context.getTransition() !=  null 
					&& context.getEvent() == "PLAY"
					&& context.getTransition().getTarget().getId() == "CLOSED"
					&& context.getExtendedState().getVariables().get("CD") != null ) {
					context.getStateMachine().sendEvent("PLAY");
				}	
			}
	 
		}
	
	public static class PlayingAction implements Action<String,String>{

		@SuppressWarnings("deprecation")
		@Override
		public void execute(StateContext<String, String> context) {
			Map<Object,Object> variables = context.getExtendedState().getVariables();
			Object elapsed = variables.get("ELAPSEDTIME");
			Object cd = variables.get("CD");
			Object track = variables.get("TRACK");
			if(elapsed instanceof Long) {
				Long e = ((Long)elapsed) + 1000L;
				if(e > ((Cd)cd).getTracks()[((Integer) track)].getLength()*1000){
					context.getStateMachine().sendEvent(MessageBuilder
							.withPayload("FORWARD")
							.setHeader("TRACKSHIFT".toString(),1).build());
				}else {
					variables.put("ELAPSEDTIME", e);
				}
		      }	
		    }	
	      }
	
	
	public static class TrackAction implements Action<String,String>{

		@Override
		public void execute(StateContext<String, String> context) {
            Map<Object,Object> variables = context.getExtendedState().getVariables();
            Object trackshift = context.getMessageHeader("TRACKSHIFT".toString());
            Object track = variables.get("TRACK");
            Object cd = variables.get("CD");
            if(trackshift instanceof Integer && track instanceof Integer && cd instanceof Cd ) {
            	int next = ((Integer) track ) + ((Integer) trackshift);
            	if( next >0  && ((Cd) cd).getTracks().length > next) {
            		variables.put("ELAPSEDTIME", 0L);
            		variables.put("TRACK",next);
            	}else if(((Cd) cd).getTracks.length <= next) {
            		context.getStateMachine().sendEvent("STOP");
            	}
            }
		}
		
	}
		
	 public static class PlayAction implements Action<String,String>{

		@Override
		public void execute(StateContext<String, String> context) {
			 context.getExtendedState().getVariables().put("ELAPSEDTIME",01);
			 context.getExtendedState().getVariables().put("TRACK",0);	
		}
	 }
	  public static class PlayGuard implements Guard<String,String>{

		@Override
		public boolean evaluate(StateContext<String, String> context) {
			 ExtendedState extendedState = context.getExtendedState();
			  return extendedState.getVariables().get("CD") != null;
		} 
	  }
	  
	  public static class LoadAction implements Action<String,String>{

		@Override
		public void execute(StateContext<String, String> context) {
		     Object cd = context.getMessageHeader("CD");
		     context.getExtendedState().getVariables().put("CD",cd);
			
		}
		  
	  }
	}
              
