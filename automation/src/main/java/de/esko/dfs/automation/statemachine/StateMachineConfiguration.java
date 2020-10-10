package de.esko.dfs.automation.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;

@Configuration
@EnableStateMachine
@Slf4j
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<State, Event> {

}
