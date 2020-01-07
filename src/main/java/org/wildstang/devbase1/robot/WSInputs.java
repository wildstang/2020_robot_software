package org.wildstang.devbase1.robot;

import org.wildstang.framework.core.Inputs;
import org.wildstang.framework.hardware.InputConfig;
import org.wildstang.framework.io.inputs.InputType;
import org.wildstang.hardware.JoystickConstants;
import org.wildstang.hardware.crio.inputs.WSInputType;
import org.wildstang.hardware.crio.inputs.config.WsJSJoystickInputConfig;

/**
 * Enumerate all inputs to the robot. Robot.java uses this to set up all inputs.
 */
public enum WSInputs implements Inputs {
    // ***************************************************************
    // Driver and Manipulator Controllers
    // ***************************************************************
    /** Throttle control for drive subsystem */
    DRIVE_THROTTLE("Throttle", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_Y), true),
    /** Steering control for drive subsystem */
    DRIVE_HEADING("Heading", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_X), true);

    private final String name;
    private final InputType type;
    private InputConfig config;
    private boolean trackingState;

    WSInputs(String name, InputType type, InputConfig config, boolean trackingState) {
        this.name = name;
        this.type = type;
        this.config = config;
        this.trackingState = trackingState;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputType getType() {
        return type;
    }

    @Override
    public InputConfig getConfig() {
        return config;
    }

    @Override
    public boolean isTrackingState() {
        return trackingState;
    }
}
