package de.esko.dfs.xps.statemachine;

import de.esko.dfs.statemachine.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<State, Event> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config.withConfiguration().autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.INIT)
                .states(EnumSet.allOf(State.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions.withExternal().source(State.INIT).event(Event.GLOBAL_RDY).target(State.MAIN_ON).action(setExtendedStateToManual());
        transitions.withExternal().source(State.MAIN_ON).event(XpsEvent.XPS_S1).target(State.BUSY).guard(manualOperation());
        transitions.withExternal().source(State.MAIN_ON).event(XpsEvent.XPS_S2).target(State.BUSY).guard(manualOperation());
        transitions.withExternal().source(State.BUSY).event(XpsEvent.XPS_RESET).target(State.MAIN_ON).guard(manualOperation());

        transitions.withExternal().source(State.MAIN_ON).event(Event.AUTO_SETUP).target(State.REMOTE_CONTROL)
                .action(setExtendedStateToAuto());
        transitions.withExternal().source(State.REMOTE_CONTROL).event(Event.AUTO_S1).target(State.REMOTE_CONTROL)
                .guard(autoOperation())
                .action(remoteS1());
        transitions.withExternal().source(State.REMOTE_CONTROL).event(Event.AUTO_S2).target(State.REMOTE_CONTROL)
                .guard(autoOperation())
                .action(remoteS2());
        transitions.withExternal().source(State.REMOTE_CONTROL).event(Event.AUTO_RESET).target(State.MAIN_ON)
                .guard(autoOperation())
                .action(setExtendedStateToManual())
                .action(resetExtendedState());
    }

    private Action<State, Event> remoteS2() {
        return ctx -> ctx.getExtendedState().getVariables().put("S2_WIP", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> setExtendedStateToAuto() {
        return ctx -> ctx.getExtendedState().getVariables().put("IS_AUTO", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> setExtendedStateToManual() {
        return ctx -> ctx.getExtendedState().getVariables().put("IS_AUTO", Boolean.FALSE);
    }

    @Bean
    public Guard<State, Event> manualOperation() {
        return ctx -> !ctx.getExtendedState().get("IS_AUTO", Boolean.class);
    }

    @Bean
    public Guard<State, Event> autoOperation() {
        return ctx -> ctx.getExtendedState().get("IS_AUTO", Boolean.class);
    }

    @Bean
    public Action<State, Event> remoteS1() {
        return ctx -> ctx.getExtendedState().getVariables().put("S1_WIP", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> resetExtendedState() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put("IS_AUTO", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("S1_WIP", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("S2_WIP", Boolean.FALSE);
        };
    }
}
