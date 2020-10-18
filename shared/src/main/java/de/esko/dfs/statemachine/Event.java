package de.esko.dfs.statemachine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Event {

    private final int value;
    private final String name;
    private static final Map<String, Event> ENUM_MAP = new HashMap<>();

    protected Event(int value, String name) {
        this.value = value;
        this.name = name;
        ENUM_MAP.put(name, this);
    }

    public int value() {
        return value;
    }

    public String name() {
        return name;
    }

    public static Event valueOf(String name) {
        return ENUM_MAP.get(name);
    }

    public static Set<Event> enumSet() {
        return Collections.unmodifiableSet(Set.copyOf(ENUM_MAP.values()));
    }

    // Automation
    public static final Event GLOBAL_ACK = new Event(0, "GLOBAL_ACK");
    public static final Event GLOBAL_RDY = new Event(9901, "GLOBAL_RDY");
    public static final Event AUTO_SETUP = new Event(9900, "AUTO_SETUP");
    public static final Event AUTO_START = new Event(9908, "AUTO_START");
    public static final Event AUTO_STOP = new Event(9910, "AUTO_STOP");
    public static final Event AUTO_PH_RDY = new Event(9902, "PH_RDY");
    public static final Event AUTO_XPS_RDY = new Event(9903, "XPS_RDY");
    public static final Event AUTO_RESET = new Event(9904, "RESET");
    public static final Event PROGRAM_START = new Event(9905, "PROGRAM_START");
    public static final Event AUTO_LOAD_CDI = new Event(9906, "AUTO_LOAD_CDI");
    public static final Event AUTO_UNLOAD_CDI = new Event(9907, "AUTO_UNLOAD_CDI");
    public static final Event AUTO_CDI_LOADED = new Event(9908, "AUTO_CDI_LOADED");
    public static final Event AUTO_S1 = new Event(9909, "AUTO_S1");
    public static final Event AUTO_S2 = new Event(9910, "AUTO_S2");
    public static final Event AUTO_S1_FINISHED = new Event(9911, "AUTO_S1_FINISHED");
    public static final Event AUTO_S2_FINISHED = new Event(9912, "AUTO_S2_FINISHED");
    public static final Event AUTO_UNLOAD_CDI_FINISHED = new Event(9913, "AUTO_UNLOAD_CDI_FINISHED");
}
