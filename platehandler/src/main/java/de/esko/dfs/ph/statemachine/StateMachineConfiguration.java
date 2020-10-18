package de.esko.dfs.ph.statemachine;

import de.esko.dfs.statemachine.Event;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
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
        transitions.withExternal().source(State.INIT).event(Event.GLOBAL_RDY).target(State.MAINON).action(setExtendedStateToManual());
        transitions.withExternal().source(State.MAINON).event(PhEvent.PH_LOAD_CDI).target(State.LOAD2CDI).action(loadCdiAction(), errorAction()).guard(manualOperation());
        transitions.withExternal().source(State.LOAD2CDI).event(PhEvent.PH_RESET).target(State.MAINON).guard(manualOperation());
        transitions.withExternal().source(State.MAINON).event(PhEvent.PH_UNLOAD_CDI).target(State.LOADFROMCDI).action(unloadCdiAction(), errorAction()).guard(manualOperation());
        transitions.withExternal().source(State.LOADFROMCDI).event(PhEvent.PH_RESET).target(State.MAINON).guard(manualOperation());

        transitions.withExternal().source(State.MAINON).event(Event.AUTO_SETUP).target(State.REMOTE_CONTROL).action(setExtendedStateToAuto());

        transitions.withExternal().source(State.REMOTE_CONTROL).event(Event.AUTO_RESET).target(State.MAINON).action(resetExtendedState()).guard(autoOperation());
        transitions.withExternal().source(State.REMOTE_CONTROL).event(Event.AUTO_LOAD_CDI).target(State.REMOTE_CONTROL).action(remoteLoadCdi()).guard(autoOperation());
        transitions.withExternal().source(State.REMOTE_CONTROL).event(Event.AUTO_UNLOAD_CDI).target(State.REMOTE_CONTROL).action(remoteUnloadCdi()).guard(autoOperation());

    }

    @Bean
    public Action<State, Event> loadCdiAction() {
        return ctx -> {
            log.info("Received " + ctx.getEvent().name());
        };
    }

    @Bean
    public Action<State, Event> unloadCdiAction() {
        return ctx -> {
            log.info("Uloading CDI");
        };
    }

    @Bean
    public Action<State, Event> errorAction() {
        return ctx -> {
            log.error(ctx.getSource().toString());
        };
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
    public Action<State, Event> remoteLoadCdi() {
        return ctx -> ctx.getExtendedState().getVariables().put("REMOTE_LOADING_CDI", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> remoteUnloadCdi() {
        return ctx -> ctx.getExtendedState().getVariables().put("REMOTE_UNLOADING_CDI", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> resetExtendedState() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put("IS_AUTO", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("REMOTE_LOADING_CDI", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("REMOTE_UNLOADING_CDI", Boolean.FALSE);
        };
    }
}