package de.esko.dfs.ph.ui;

import de.esko.dfs.message.Command;
import de.esko.dfs.ph.statemachine.State;
import de.esko.dfs.statemachine.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

@RequiredArgsConstructor
@Slf4j
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
            case REMOTE_CONTROL -> platehandlerUi.rcState();

        }
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        var changedStateKey = key.toString();
        switch (changedStateKey) {
            case "IS_AUTO" -> operationModeChangedToAuto(Boolean.parseBoolean(value.toString()));
            case "REMOTE_LOADING_CDI" -> remoteLoadingCdiStattusChanged(Boolean.parseBoolean(value.toString()));
            case "REMOTE_UNLOADING_CDI" -> remoteUnloadingCdiStatusChanged(Boolean.parseBoolean(value.toString()));
        }
    }

    private void remoteUnloadingCdiStatusChanged(boolean isUnloading) {
        platehandlerUi.addToDisplay("Unloading CDI status set to " + isUnloading);
        if (isUnloading) {
            platehandlerUi.sendCommandDelayed(new Command(Event.AUTO_UNLOAD_CDI_FINISHED.value(), "CDI Unloaded", platehandlerUi.getName(), Event.AUTO_UNLOAD_CDI_FINISHED.name()), 11);
        }
    }

    private void remoteLoadingCdiStattusChanged(boolean loadingCdiStatus) {
        platehandlerUi.addToDisplay("Loading CDI Status set to " + loadingCdiStatus);
        if (loadingCdiStatus) {
            platehandlerUi.sendCommandDelayed(new Command(Event.AUTO_CDI_LOADED.value(), "CDI Loaded", platehandlerUi.getName(),  Event.AUTO_CDI_LOADED.name()), 17);
        }
    }

    private void operationModeChangedToAuto(boolean isAuto) {
        platehandlerUi.addToDisplay("Remote control is set to = " + isAuto);
        if (isAuto) {
            platehandlerUi.addToDisplay("This seems to be a friendly take over ....");
            platehandlerUi.sendCommandDelayed(new Command(Event.AUTO_PH_RDY.value(), "PH Ready", platehandlerUi.getName(),  Event.AUTO_PH_RDY.name()), 13);
        }
    }


}
