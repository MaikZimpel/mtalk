package de.esko.dfs.ph.statemachine;

import de.esko.dfs.statemachine.Event;

public class PhEvent extends Event {

    private PhEvent(int value, String name) {
        super(value, name);
    }

    public static Event PH_RDY = new PhEvent(401, "PH_RDY");
    // PH
    public static Event PH_LOAD_CDI = new PhEvent(402, "PH_LOAD_CDI");
    public static Event PH_UNLOAD_CDI = new PhEvent(403, "PH_UNLOAD_CDI");
    public static Event PH_LOAD_UV  = new PhEvent(404, "PH_LOAD_UV");
    public static Event PH_RESET  = new PhEvent(405, "PH_RESET");
    public static Event PH_ABORT  = new PhEvent(406, "PH_ABORT");
    public static Event PH_STATE = new PhEvent(407, "PH_STATE");
    public static Event PH_GLOBAL = new PhEvent(408, "PH_GLOBAL");
    public static Event PH_GLOBAL_TYPE = new PhEvent(409, "PH_GLOBAL_TYPE");
    public static Event PH_LOAD_TO_POST_PROC = new PhEvent(410, "PH_LOAD_TO_POST_PROC");
    public static Event PH_PLATE_SIZE = new PhEvent(411, "PH_PLATE_SIZE");
    public static Event PH_CALIBRATE = new PhEvent(412, "PH_CALIBRATE");
    public static Event PH_CALIBRATE_DONE = new PhEvent(413, "PH_CALIBRATE_DONE");
    public static Event PH_COMMAND = new PhEvent(414, "PH_COMMAND");
    public static Event PH_AXIS_X = new PhEvent(415, "PH_AXIS_X");
    public static Event PH_AXIS_Y = new PhEvent(416, "PH_AXIS_Y");
    public static Event PH_AXIS_Z = new PhEvent(417, "PH_AXIS_Z");
    public static Event PH_CMD_STS = new PhEvent(418, "PH_CMD_STS");
    public static Event PH_STS_AXIS_X = new PhEvent(419, "PH_STS_AXIS_X");
    public static Event PH_STS_AXIS_Y = new PhEvent(420, "PH_STS_AXIS_Y");
    public static Event PH_STS_AXIS_Z = new PhEvent(421, "PH_STS_AXIS_Z");


}
