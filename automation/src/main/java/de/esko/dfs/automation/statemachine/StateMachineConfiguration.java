package de.esko.dfs.automation.statemachine;

import de.esko.dfs.statemachine.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
@Slf4j
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<State, Event> {

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.RDY)
                .states(EnumSet.allOf(State.class))
                .end(State.RDY);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions.withExternal().source(State.RDY).event(Event.PH_RDY).target(State.RDY).action(this::setPhState);
        transitions.withExternal().source(State.RDY).event(Event.XPS_RDY).target(State.RDY).action(this::setXpsState);

    }

    private void setPhState(StateContext<State, Event> ctx) {
        ctx.getExtendedState().getVariables().put("PH_STATE", ctx.getEvent().name());
    }

    private void setXpsState(StateContext<State, Event> ctx) {
        ctx.getExtendedState().getVariables().put("XPS_STATE", ctx.getEvent().name());
    }

}
