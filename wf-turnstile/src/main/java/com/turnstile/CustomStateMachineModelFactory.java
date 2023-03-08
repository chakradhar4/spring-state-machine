package com.turnstile;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.statemachine.config.model.ConfigurationData;
import org.springframework.statemachine.config.model.DefaultStateMachineModel;
import org.springframework.statemachine.config.model.StateData;
import org.springframework.statemachine.config.model.StateMachineModel;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.config.model.StatesData;
import org.springframework.statemachine.config.model.TransitionData;
import org.springframework.statemachine.config.model.TransitionsData;

public class CustomStateMachineModelFactory implements StateMachineModelFactory<String,String>{

	@Override
	public StateMachineModel<String,String> build() {
		ConfigurationData<String,String> configurationData = new ConfigurationData<>();
		
	    Collection<StateData<String,String>> stateData = new ArrayList<>();
	    
	    stateData.add(new StateData<String,String>("LOCKED",true));
	    stateData.add(new StateData<String,String>("UNLOCKED"));
	    
	    StatesData<String,String> statesData = new StatesData<>(stateData);
	    
	    Collection<TransitionData<String,String>> transitionData = new ArrayList<>();
	    transitionData.add(new TransitionData<String,String>("LOCKED","UNLOCKED","COIN"));
	    transitionData.add(new TransitionData<String,String>("UNLOCKED","LOCKED","EXIT"));
	    
	    TransitionsData<String,String> transitionsData = new TransitionsData<>(transitionData);
	    
	    StateMachineModel<String,String> stateMachineModel = new DefaultStateMachineModel<String,String>(configurationData,statesData,transitionsData);
	    
	    return stateMachineModel;
	}
	

	@Override
	public StateMachineModel<String,String> build(String machineId) {
		return build();
	}

}
