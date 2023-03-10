package com.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class WorkflowApplication {

	
	@Autowired
	private StateMachine<String,String> sm;
	
	public static void main(String[] args) {
		SpringApplication.run(WorkflowApplication.class, args);
	}
	
	@PostConstruct
	public void started() {
		
		   sm.start();
		   System.out.println(sm.getState().getId());
		   sm.sendEvent("A");
		   System.out.println(sm.getState().getId());
		   sm.sendEvent("B");
		   System.out.println(sm.getState().getId());
		
		/*
		 * *****************washer***************************
		 sm.start();
		 System.out.println(sm.getState());
		 sm.sendEvent("ON");
		 System.out.println(sm.getState());
		 sm.sendEvent("RINSE");
		 System.out.println(sm.getState());
		 sm.sendEvent("CUTPOWER");
		 System.out.println(sm.getState());
		 sm.sendEvent("RESTOREPOWER");
		 System.out.println(sm.getState());
		 sm.sendEvent("DRY");
		 System.out.println(sm.getState());
		 sm.sendEvent("OFF");
		 System.out.println(sm.getState());
		 
	  */
		 
		/*
		 *******TURNSTILE********


		 * sm.start();
		System.out.println(sm.getState());
		sm.sendEvent("COIN");
		System.out.println(sm.getState());
		sm.sendEvent("EXIT");
		System.out.println(sm.getState());
		
		*/
		
		
		
	}

}
