package de.esko.dfs.statemachine;

public enum Event {

    // Automation
    GLOBAL_ACK,
    GLOBAL_RDY,
    PH_RDY,
    XPS_RDY,

    // PH
    PH_LOAD_CDI,
    PH_UNLOAD_CDI,
    PH_LOAD_UV,
    PH_RESET,
    PH_ABORT,
    PH_STATE,
    PH_GLOBAL,
    PH_GLOBAL_TYPE,
    PH_LOAD_TO_POST_PROC,
    PH_PLATE_SIZE,
    PH_CALIBRATE,
    PH_CALIBRATE_DONE,
    PH_COMMAND,
    PH_AXIS_X,
    PH_AXIS_Y,
    PH_AXIS_Z,
    PH_CMD_STS,
    PH_STS_AXIS_X,
    PH_STS_AXIS_Y,
    PH_STS_AXIS_Z,

    // XPS
    XPS_S1,
    XPS_S2,
    XPS_RESET,
    XPS_ABORT
}
