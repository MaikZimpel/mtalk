package de.esko.dfs.xps.statemachine;

import de.esko.dfs.statemachine.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<State, Event> {

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.INIT)
                .states(EnumSet.allOf(State.class))
                .end(State.IDLE)
                .end(State.ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions.withExternal().source(State.INIT).event(Event.GLOBAL_RDY).target(State.MAIN_ON);
        transitions.withExternal().source(State.MAIN_ON).event(Event.XPS_S1).target(State.BUSY);
        transitions.withExternal().source(State.MAIN_ON).event(Event.XPS_S2).target(State.BUSY);
        transitions.withExternal().source(State.BUSY).event(Event.XPS_RESET).target(State.MAIN_ON);
    }
}
