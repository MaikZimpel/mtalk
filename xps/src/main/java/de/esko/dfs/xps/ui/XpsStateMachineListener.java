package de.esko.dfs.xps.ui;

import de.esko.dfs.message.Command;
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
            case REMOTE_CONTROL -> xpsUi.rcState();
        }
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        var changedStateKey = key.toString();
        switch (changedStateKey) {
            case "IS_AUTO" -> remoteControlStateChanged(Boolean.parseBoolean(value.toString()));
            case "S1_WIP" -> s1WipStatusChanged(Boolean.parseBoolean(value.toString()));
            case "S2_WIP" -> s2WipStatusChanged(Boolean.parseBoolean(value.toString()));
        }

    }

    private void remoteControlStateChanged(boolean isAuto) {
        xpsUi.addToDisplay("Extended State IS_AUTO=" + isAuto);
        if (isAuto) {
            xpsUi.sendCommandDelayed(new Command(Event.AUTO_XPS_RDY.value(), "XPS Ready", xpsUi.getName(),
                    Event.AUTO_XPS_RDY.name()), 10);
        }
    }

    private void s1WipStatusChanged(Boolean isWip) {
        xpsUi.addToDisplay("Extended State S1_WIP=" + isWip);
        if (isWip) {
            xpsUi.sendCommandDelayed(new Command(Event.AUTO_S1_FINISHED.value(), "AUTO S1 FINISHED",
                    xpsUi.getName(), Event.AUTO_S1_FINISHED.name()), 7);
        }
    }

    private void s2WipStatusChanged(Boolean isWip) {
        xpsUi.addToDisplay("Extended State S2_WIP=" + isWip);
        if (isWip) {
            xpsUi.sendCommandDelayed(new Command(Event.AUTO_S2_FINISHED.value(), "AUTO S2 FINISHED",
                    xpsUi.getName(), Event.AUTO_S2_FINISHED.name()), 3);
        }
    }

}
