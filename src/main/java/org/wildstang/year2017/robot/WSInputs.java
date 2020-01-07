package org.wildstang.year2017.robot;

import org.wildstang.framework.core.Inputs;
import org.wildstang.framework.hardware.InputConfig;
import org.wildstang.framework.hardware.WsRemoteAnalogInputConfig;
import org.wildstang.framework.io.inputs.InputType;
import org.wildstang.hardware.JoystickConstants;
import org.wildstang.hardware.crio.inputs.WSInputType;
import org.wildstang.hardware.crio.inputs.config.WsAnalogGyroConfig;
import org.wildstang.hardware.crio.inputs.config.WsDigitalInputConfig;
import org.wildstang.hardware.crio.inputs.config.WsI2CInputConfig;
import org.wildstang.hardware.crio.inputs.config.WsJSButtonInputConfig;
import org.wildstang.hardware.crio.inputs.config.WsJSJoystickInputConfig;
import org.wildstang.hardware.crio.inputs.config.WsMotionProfileConfig;

import edu.wpi.first.wpilibj.I2C;

public enum WSInputs implements Inputs {
    // im.addSensorInput(LIDAR, new WsLIDAR());
    //
    // ***************************************************************
    // Driver and Manipulator Controller Button Locations
    // ***************************************************************
    //
    // +-------------------------------------------------------+
    // + +---------+ [TOP] +---------+ +
    // | | 6 | | 7 | |
    // | +---------+ +---------+ |
    // | |
    // | +---------+ +---------+ |
    // | | 4 | | 5 | |
    // + +---------+ +---------+ +
    // +-------------------------------------------------------+
    //
    // +-------------------------------------------------------+
    // / +--+ [FRONT] \
    // + |YU| (3) +
    // | +--+ +--+ +----+ +----+ |
    // | |XL XR| | 8 | (X) | 9 | (0) (2) |
    // | +--+ +--+ +----+ +----+ |
    // | |YD| (1) |
    // | +--+ +--+ (X) +--+ |
    // | / \ / \ |
    // | | 10 | | 11 | |
    // | \ / \ / |
    // + +--+ +--+ +
    // \ /
    // \ +-----------------------------+ /
    // \ / \ /
    // \ / \ /
    // \ / \ /
    // +----+ +----+
    //
    //
    // ********************************
    // Driver Enums
    // ********************************
    //
    // ---------------------------------
    // Driver Joysticks
    // ---------------------------------
    DRV_THROTTLE("Throttle", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_Y), true), // Driver
                                                                                      // Subsystem
    DRV_HEADING("Heading", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_X), true), // Driver
                                                                                       // Subsystem

    // ---------------------------------
    // Driver Buttons
    // ---------------------------------
    AUTO_GEAR_DROP("Auto gear drop", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 1), false), // Intake
                                                                                                     // Subsystem
    ANTITURBO("Antiturbo", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 4), false), // Drive
                                                                                           // Subsystem
    SHIFT("Driver Shift", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 5), false), // Driver
                                                                                          // Subsystem
    QUICK_TURN("Quick Turn", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 7), false), // Driver
                                                                                             // Subsystem
    BASE_LOCK("Base lock", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 3), false), // Driver
                                                                                           // Subsystem

    // ---------------------------------
    // Manipulator Joysticks
    // ---------------------------------
    FEEDER_LEFT("Feeder Left Up/Down", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_JOYSTICK_Y), true), // Shooter
                                                                                      // Subsystem
    FEEDER_RIGHT("Feeder Right Up/Down", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_Y), true), // Shooter
                                                                                       // Subsystem

    // ---------------------------------
    // Manipulator DPAD Buttons
    // ---------------------------------
    CLIMBER_UP("Climber Up", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_Y_UP), false), // Climber Subsystem
    CLIMBER_HALF_SPEED("Climber Down", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_Y_DOWN), false), // Climber
                                                                                 // Subsystem

    // ---------------------------------
    // Manipulator Buttons
    // ---------------------------------
    GEAR_TILT_BUTTON("Gear Wall", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 0), false), // Gear
                                                                                                  // Subsystem
    GEAR_HOLD_BUTTON("Gear Hold", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 1), false), // Gear
                                                                                                  // Subsystem
    GEAR_RECEIVE("Gear Receive", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 2), false), // Gear
                                                                                                 // Subsystem
    GEAR_DELIVER("Gear Deliver", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 3), false), // Gear
                                                                                                 // Subsystem
    INTAKE_ON("Intake Turn on/off", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 4), false), // Intake
                                                                                                    // Subsystem
    FLYWHEEL("Flywheel On/Off", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 5), true),

    OVERRIDE("Override", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 9), false),

    // ********************************
    // Digital IOs
    // ********************************
    // GEAR_IN_POSITION("Gear In Position Sensor", WSInputType.SWITCH, new
    // WsDigitalInputConfig(0, true), false), // Gear Subsystem
    // BALLS_WAITING_LEFT("Balls Waiting Left", WSInputType.SWITCH, new
    // WsDigitalInputConfig(1, true), false), // Shooter Subsystem
    // BALLS_WAITING_RIGHT("Digital Waiting Right", WSInputType.SWITCH, new
    // WsDigitalInputConfig(2, true), false), // Shooter Subsystem

    // ********************************
    // Others ...
    // ********************************
    GYRO("Gyro", WSInputType.ANALOG_GYRO, new WsAnalogGyroConfig(0, true), false);
    // IMU("IMU", WSInputType.COMPASS, new WsI2CInputConfig(I2C.Port.kMXP, 0x20),
    // true);

    private final String m_name;
    private final InputType m_type;

    private InputConfig m_config = null;

    private boolean m_trackingState;

    private static boolean isLogging = true;

    WSInputs(String p_name, InputType p_type, InputConfig p_config, boolean p_trackingState) {
        m_name = p_name;
        m_type = p_type;
        m_config = p_config;
        m_trackingState = p_trackingState;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public InputType getType() {
        return m_type;
    }

    public InputConfig getConfig() {
        return m_config;
    }

    public boolean isTrackingState() {
        return m_trackingState;
    }

    public static boolean getLogging() {
        return isLogging;
    }

}
