package de.esko.dfs.automation.statemachine

import de.esko.dfs.statemachine.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.statemachine.StateMachine
import spock.lang.Specification
import static State.*

@SpringBootTest(classes = [StateMachineConfiguration.class])
class AutomationStateMachineSpec extends Specification {

    @Autowired(required = true)
    private StateMachine<State, Event> automationStateMachine;

    def "check statemachine green scenario"() {
        given: "State Machine is initialized."
        automationStateMachine
        and: "State is INIT"
        automationStateMachine.state.id == INIT
        and: "Extended State is cleared"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when: "AUTO SETUP Event is received"
        automationStateMachine.sendEvent(Event.AUTO_SETUP)
        then: "State is WAITING"
        automationStateMachine.state.id == WAITING
        when: "AUTO PH RDY Event is received"
        automationStateMachine.sendEvent(Event.AUTO_PH_RDY)
        then: "Extended State PH_RDY is true"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        and: "State is WAITING"
        automationStateMachine.state.id == WAITING
        when: "AUTO XPS RDY Event is received."
        automationStateMachine.sendEvent(Event.AUTO_XPS_RDY)
        then: "Extended State XPS_RDY is true"
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        and: "State is SETUP"
        automationStateMachine.state.id == SETUP

        when: "PROGRAM START Event is received"
        automationStateMachine.sendEvent(Event.AUTO_START)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows:"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when:"AUTO LOAD CDI Event is received"
        automationStateMachine.sendEvent(Event.AUTO_LOAD_CDI)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when:"AUTO CDI LOADED Event is received"
        automationStateMachine.sendEvent(Event.AUTO_CDI_LOADED)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when:"AUTO S1 Event received"
        automationStateMachine.sendEvent(Event.AUTO_S1)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when: "AUTO_S1_FINISHED Event received"
        automationStateMachine.sendEvent(Event.AUTO_S1_FINISHED)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when: "AUTO_UNLOAD_CDI Event is received"
        automationStateMachine.sendEvent(Event.AUTO_UNLOAD_CDI)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when: "AUTO_UNLOAD_CDI_FINISHED Event is received"
        automationStateMachine.sendEvent(Event.AUTO_UNLOAD_CDI_FINISHED)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when:"AUTO_S2 Event is received"
        automationStateMachine.sendEvent(Event.AUTO_S2)
        then: "State is BUSY"
        automationStateMachine.state.id == BUSY
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE

        when:
        automationStateMachine.sendEvent(Event.AUTO_S2_FINISHED)
        then: "State is BUSY"
        and: "Extended State is as follows"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.TRUE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.TRUE

        when: "AUTO RESET Event is received"
        automationStateMachine.sendEvent(Event.AUTO_RESET)
        then: "State is RDY"
        and: "Extended State is cleared"
        automationStateMachine.extendedState.variables.get("PH_RDY", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("XPS_RDY", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("AUTO_STARTED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_LOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_LOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S1_DONE", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADING", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("CDI_UNLOADED", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_WIP", Boolean.class) == Boolean.FALSE
        automationStateMachine.extendedState.variables.get("S2_DONE", Boolean.class) == Boolean.FALSE
    }

}