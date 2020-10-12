package de.esko.dfs.ph.ui;

import de.esko.dfs.ph.statemachine.State;
import de.esko.dfs.statemachine.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

@RequiredArgsConstructor
public class PlatehandlerStateMachineListener extends StateMachineListenerAdapter<State, Event> {

    private final PlatehandlerUi platehandlerUi;

    @Override
    public void stateEntered(org.springframework.statemachine.state.State<State, Event> state) {
        var st = state.getId();
        switch (st) {
            case LOAD2CDI -> platehandlerUi.loadToCdiState();
            case LOADFROMCDI -> platehandlerUi.loadFromCdiState();
            case ERROR -> platehandlerUi.errorState();
            case MAINON -> platehandlerUi.mainOnState();

        }
    }

}
