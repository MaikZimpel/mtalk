package de.esko.dfs.automation.ui;

import de.esko.dfs.automation.statemachine.State;
import de.esko.dfs.statemachine.Event;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

public class AutomationStatemachineListener extends StateMachineListenerAdapter<State, Event> {
    @Override
    public void extendedStateChanged(Object key, Object value) {
        switch (key.toString()) {
            case "PH_STATE" -> {}
        }
    }
}
