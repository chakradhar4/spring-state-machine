package com.turnstile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class WfTurnstileApplication {

	@Autowired
	private StateMachine<String,String> stateMachine;
	
	public static void main(String[] args) {
		SpringApplication.run(WfTurnstileApplication.class, args);
	}
	
	@SuppressWarnings("deprecation")
	@PostConstruct
	public void started() {
		 stateMachine.start();
		 System.out.println("inserting coin...."+stateMachine.sendEvent("COIN"));
		 System.out.println("Coin inserted successfully current state : "+stateMachine.getState().getId());
		 stateMachine.sendEvent("EXIT");
		 System.out.println("workdone! processing exit...."+ stateMachine.sendEvent("EXIT"));
		 System.out.println("EXIT successfull current state : "+stateMachine.getState().getId());
	}
}
