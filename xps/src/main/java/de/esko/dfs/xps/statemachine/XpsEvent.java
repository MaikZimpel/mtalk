package de.esko.dfs.xps.statemachine;

import de.esko.dfs.statemachine.Event;

public class XpsEvent extends Event {

    private XpsEvent(int value, String name) {
        super(value, name);
    }

    public static final Event XPS_S1 = new XpsEvent(501, "XPS_S1");
    public static final Event XPS_S2 = new XpsEvent(502, "XPS_S2");
    public static final Event XPS_RESET = new XpsEvent(504, "XPS_RESET");
    public static final Event XPS_ABORT = new XpsEvent(505, "XPS_ABORT");
}
