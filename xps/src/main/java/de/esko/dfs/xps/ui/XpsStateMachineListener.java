package de.esko.dfs.xps.ui;

import de.esko.dfs.statemachine.Event;
import de.esko.dfs.xps.statemachine.State;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

@RequiredArgsConstructor
public class XpsStateMachineListener extends StateMachineListenerAdapter<State, Event> {

    private final XpsUi xpsUi;

    @Override
    public void stateEntered(org.springframework.statemachine.state.State<State, Event> state) {
        var st = state.getId();
        switch (st) {
            case BUSY -> xpsUi.busyState();
            case MAIN_ON -> xpsUi.mainOnState();
            case ERROR -> xpsUi.errorState();
        }
    }

}
