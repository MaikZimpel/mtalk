package de.esko.dfs.automation.statemachine;

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

import static de.esko.dfs.automation.statemachine.State.*;

@Configuration
@EnableStateMachine
@Slf4j
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<State, Event> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config.withConfiguration().autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.INIT, clearExtendedState())
                .states(EnumSet.allOf(State.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions.withExternal().source(INIT).event(Event.AUTO_SETUP).target(WAITING);
        transitions.withExternal().source(INIT).event(Event.AUTO_RESET).target(RDY);
        transitions.withExternal().source(RDY).event(Event.AUTO_SETUP).target(WAITING);
        transitions.withExternal().source(WAITING).target(SETUP).event(Event.AUTO_PH_RDY).guard(isXpsRdy());
        transitions.withExternal().source(WAITING).target(SETUP).event(Event.AUTO_XPS_RDY).guard(isPhRdy());
        transitions.withExternal().source(SETUP).event(Event.AUTO_START).guard(canStartProgram()).target(BUSY).action(startProgram());
        transitions.withExternal().source(BUSY).event(Event.AUTO_LOAD_CDI).target(BUSY).action(loadingCdi());
        transitions.withExternal().source(BUSY).event(Event.AUTO_CDI_LOADED).target(BUSY).action(cdiLoaded());
        transitions.withExternal().source(BUSY).event(Event.AUTO_S1).target(BUSY).guard(isCdiLoaded()).action(s1());
        transitions.withExternal().source(BUSY).event(Event.AUTO_S1_FINISHED).target(BUSY).action(s1Finished());
        transitions.withExternal().source(BUSY).event(Event.AUTO_UNLOAD_CDI).target(BUSY).guard(isCdiLoaded()).guard(isS1Done()).action(unloadCdi());
        transitions.withExternal().source(BUSY).event(Event.AUTO_UNLOAD_CDI_FINISHED).target(BUSY).action(cdiUnloaded());
        transitions.withExternal().source(BUSY).event(Event.AUTO_S2).target(BUSY).guard(isCdiUnloaded()).action(s2());
        transitions.withExternal().source(BUSY).event(Event.AUTO_S2_FINISHED).target(BUSY).action(s2Finished());
        transitions.withExternal().source(BUSY).event(Event.AUTO_RESET).target(RDY).guard(isCdiUnloaded()).guard(isS1Done()).guard(isS2Done()).action(clearExtendedState());
    }

    @Bean
    public Action<State, Event> unloadCdi() {
        return ctx -> ctx.getExtendedState().getVariables().put("CDI_UNLOADING", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> cdiUnloaded() {
        return ctx -> ctx.getExtendedState().getVariables().put("CDI_UNLOADED", Boolean.TRUE);
    }

    @Bean
    public Guard<State, Event> isXpsRdy() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put("PH_RDY", Boolean.TRUE);
            return ctx.getExtendedState().get("XPS_RDY", Boolean.class);
        };
    }

    @Bean
    public Guard<State, Event> isPhRdy() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put("XPS_RDY", Boolean.TRUE);
            return ctx.getExtendedState().get("PH_RDY", Boolean.class);
        };
    }

    @Bean
    public Action<State, Event> setPhRdy() {
        return ctx -> ctx.getExtendedState().getVariables().put("PH_RDY", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> setXpsRdy() {
        return ctx ->  ctx.getExtendedState().getVariables().put("XPS_RDY", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> clearExtendedState() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put("PH_RDY", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("XPS_RDY", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("AUTO_STARTED", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("CDI_LOADING", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("CDI_LOADED", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("S1_WIP", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("S1_DONE", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("S2_WIP", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("S2_DONE", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("CDI_UNLOADING", Boolean.FALSE);
            ctx.getExtendedState().getVariables().put("CDI_UNLOADED", Boolean.FALSE);
        };
    }

    @Bean
    public Action<State, Event> startProgram() {
        return ctx -> ctx.getExtendedState().getVariables().put("AUTO_STARTED", Boolean.TRUE);
    }

    @Bean
    public Guard<State, Event> canStartProgram() {
        return ctx -> ctx.getExtendedState().get("PH_RDY", Boolean.class) && ctx.getExtendedState().get("XPS_RDY", Boolean.class);
    }

    @Bean
    public Action<State, Event> loadingCdi() {
        return ctx -> ctx.getExtendedState().getVariables().put("CDI_LOADING", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> cdiLoaded() {
        return ctx -> ctx.getExtendedState().getVariables().put("CDI_LOADED", Boolean.TRUE);
    }

    @Bean
    public Guard<State, Event> isCdiLoaded() {
        return ctx -> ctx.getExtendedState().get("CDI_LOADED", Boolean.class);
    }

    @Bean
    public Action<State, Event> s1() {
        return ctx -> ctx.getExtendedState().getVariables().put("S1_WIP", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> s1Finished() {
        return ctx -> ctx.getExtendedState().getVariables().put("S1_DONE", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> s2() {
        return ctx -> ctx.getExtendedState().getVariables().put("S2_WIP", Boolean.TRUE);
    }

    @Bean
    public Action<State, Event> s2Finished() {
        return ctx -> ctx.getExtendedState().getVariables().put("S2_DONE", Boolean.TRUE);
    }

    @Bean
    public Guard<State, Event> isS1Done() {
        return ctx -> ctx.getExtendedState().get("S1_DONE", Boolean.class);
    }

    @Bean
    public Guard<State, Event> isS2Done() {
        return ctx -> ctx.getExtendedState().get("S2_DONE", Boolean.class);
    }

    @Bean
    public Guard<State, Event> isCdiUnloaded() {
        return ctx -> ctx.getExtendedState().get("CDI_UNLOADED", Boolean.class);
    }

}
