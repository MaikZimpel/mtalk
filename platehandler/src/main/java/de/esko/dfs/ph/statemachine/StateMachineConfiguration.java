package de.esko.dfs.ph.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
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
                .initial(State.MAINON)
                .states(EnumSet.allOf(State.class))
                .end(State.IDLE)
                .end(State.ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions.withExternal().source(State.MAINON).event(Event.PH_LOAD_CDI).target(State.LOAD2CDI).action(loadCdiAction());
        transitions.withExternal().source(State.LOAD2CDI).event(Event.PH_RESET).target(State.MAINON);

    }


    public Action<State, Event> loadCdiAction() {
        return ctx -> log.info("Received " + ctx.getEvent().name());
    }

    public Action<State, Event> errorAction() {
        return ctx -> {
            log.error(ctx.getSource().toString());
        };
    }

}
