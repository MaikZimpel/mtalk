package de.esko.dfs.ph.statemachine;

public enum State {

    MAINON,
    HOME,
    IDLE,
    PLATEDETECT,
    LOAD2CDI,
    LOADFROMCDI,
    LOADTOUV,
    LOADTOPOSTPROC,
    LOADFROM2NDLOADAREA,
    MOVEPOS,
    CALIBRATE,
    STOPALL,
    RESET,
    ERROR,
    DEBUG,
    AWAITING_REPLY
}
