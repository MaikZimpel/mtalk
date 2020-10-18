package de.esko.dfs.ph.statemachine

import de.esko.dfs.statemachine.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.statemachine.StateMachine
import spock.lang.Specification

@SpringBootTest(classes = [StateMachineConfiguration.class])
class PlatehandlerStateMachineSpec extends Specification {

    @Autowired(required = true)
    private StateMachine<State, Event> plateHandlerStateMachine

    def "when context is loaded"() {
        expect: "statemachine to be in state MAINON"
        plateHandlerStateMachine.getState().id == State.MAINON
    }


}