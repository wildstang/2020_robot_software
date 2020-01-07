package org.wildstang.framework.pid.controller;

/**
 *
 * @author Nathan
 */
public enum PidStateType {

    PID_DISABLED_STATE("PID_DISABLED_STATE"),
    PID_INITIALIZE_STATE("PID_INITIALIZE_STATE"),
    PID_BELOW_TARGET_STATE("PID_BELOW_TARGET_STATE"),
    PID_ON_TARGET_STATE("PID_ON_TARGET_STATE"),
    PID_STABILIZED_STATE("PID_STABILIZED_STATE"),
    PID_ABOVE_TARGET_STATE("PID_ABOVE_TARGET_STATE");

    private String title;

    private PidStateType(String name) {
        this.title = name;
    }

    @Override
    public String toString() {
        return title;
    }
}
