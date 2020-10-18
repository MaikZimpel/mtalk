package de.esko.dfs.automation.ui;

import de.esko.dfs.actor.BusConnector;
import de.esko.dfs.automation.statemachine.State;
import de.esko.dfs.message.Command;
import de.esko.dfs.statemachine.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

@RequiredArgsConstructor
@Slf4j
public class AutomationStatemachineListener extends StateMachineListenerAdapter<State, Event> {

    private final AutomationMonitorUi ui;
    private final BusConnector messageBus;

    @Override
    public void extendedStateChanged(Object key, Object value) {
        switch (key.toString()) {
            case "PH_RDY" -> ui.addToDisplay("PH ready = " + value);
            case "XPS_RDY" -> ui.addToDisplay("XPS ready = " + value);
            case "CDI_LOADING" -> loadingCdiState(Boolean.parseBoolean(value.toString()));
            case "CDI_LOADED" -> cdiLoadedStateChanged(Boolean.parseBoolean(value.toString()));
            case "S1_WIP" -> s1WipStateChanged(Boolean.parseBoolean(value.toString()));
            case "S1_DONE" -> s1DoneStateChanged(Boolean.parseBoolean(value.toString()));
            case "CDI_UNLOADING" -> cdiUnloadingStateChanged(Boolean.parseBoolean(value.toString()));
            case "CDI_UNLOADED" -> cdiUnloadedStateChanged(Boolean.parseBoolean(value.toString()));
            case "S2_WIP" -> s2WipStateChanged(Boolean.parseBoolean(value.toString()));
            case "S2_DONE" -> s2DoneStateChanged(Boolean.parseBoolean(value.toString()));
        }

    }

    private void s2DoneStateChanged(boolean isS2Done) {
        if (isS2Done) {
            ui.addToDisplay("S2 finished. Process Completed. Sending AUTO_RESET Command.");
            messageBus.send(new Command(Event.AUTO_RESET.value(), "AUTO RESET", ui.getName(), Event.AUTO_RESET.name()));
        }
    }

    private void s2WipStateChanged(boolean s2Wip) {
        if (s2Wip) {
            ui.addToDisplay("Waiting for XPS to finish S2");
        }
    }

    private void cdiUnloadingStateChanged(boolean isUnloading) {
        if (isUnloading) {
            ui.addToDisplay("Waiting for PH to unload CDI");
        }
    }

    private void cdiUnloadedStateChanged(boolean isUnloaded) {
        if (isUnloaded) {
            ui.addToDisplay("CDI Unloaded. Sending S2 command.");
            messageBus.send(new Command(Event.AUTO_S2.value(), "Start S2", ui.getName(), Event.AUTO_S2.name()));
        }
    }

    private void s1DoneStateChanged(boolean isDone) {
        if (isDone) {
            ui.addToDisplay("S1 finished.");
            messageBus.send(new Command(Event.AUTO_UNLOAD_CDI.value(), "UNLOAD CDI", ui.getName(), Event.AUTO_UNLOAD_CDI.name()));
        }
    }

    private void loadingCdiState(boolean isLoading) {
        if (isLoading) {
            ui.addToDisplay("Waiting for CDI to load.");
        }
    }

    private void s1WipStateChanged(boolean isWip) {
        if (isWip) {
            ui.addToDisplay("S1 in Progress. Waiting for XPS to finish.");
        }
    }

    private void cdiLoadedStateChanged(boolean isLoaded) {
        if (isLoaded) {
            ui.addToDisplay("CDI Loaded.");
            messageBus.send(new Command(Event.AUTO_S1.value(), "Start S1", ui.getName(), Event.AUTO_S1.name()));
        }
    }

    @Override
    public void stateChanged(org.springframework.statemachine.state.State<State, Event> from, org.springframework.statemachine.state.State<State, Event> to) {
        ui.addToDisplay("State changed from " + from.getId() + " to " + to.getId());
    }

    @Override
    public void stateEntered(org.springframework.statemachine.state.State<State, Event> state) {
        switch (state.getId()) {
            case WAITING -> ui.setAutoSetupBtnEnabled(false);
            case SETUP -> ui.setStartBtnEnabled(true);
            case BUSY -> ui.busyState();
            case RDY -> ui.readyState();
        }
    }

}
