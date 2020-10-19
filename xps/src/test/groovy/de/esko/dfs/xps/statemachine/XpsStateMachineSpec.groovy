package de.esko.dfs.xps.statemachine

import de.esko.dfs.statemachine.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.statemachine.StateMachine
import spock.lang.Specification

@SpringBootTest(classes = [StateMachineConfiguration.class])
class XpsStateMachineSpec extends Specification {

    @Autowired(required = true)
    private StateMachine<State, Event> stateMachine;

    def "Spring context creates and initialises StateMachine instance"() {

        expect:
        stateMachine.getState().id == State.INIT
        stateMachine
    }

    def "Green scenario manual operation" () {
        given: "A a normal series of events (GLOBAL_RDY -> XPS_S1 -> XPS_S2 -> RESET"
        stateMachine
        stateMachine.sendEvent(Event.GLOBAL_RDY)
        stateMachine.sendEvent(XpsEvent.XPS_S1)
        stateMachine.sendEvent(XpsEvent.XPS_S2)
        stateMachine.sendEvent(XpsEvent.XPS_RESET)
        expect: "StateMachine to be in state MAIN_ON"
        stateMachine.getState().id == State.MAIN_ON
    }

}