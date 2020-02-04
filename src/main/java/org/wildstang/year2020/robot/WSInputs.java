package org.wildstang.year2020.robot;

import org.wildstang.framework.core.Inputs;
import org.wildstang.framework.io.inputs.RemoteDigitalInput;
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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public enum WSInputs implements Inputs {
    // im.addSensorInput(LIDAR, new WsLIDAR());
    //
    //***************************************************************
    //      Driver and Manipulator Controller Button Locations
    //***************************************************************
    //
    //    +-------------------------------------------------------+
    //  +  +---------+                                 +---------+  +       
    //  |  |  RIGHT  |           TRIGGERS              |  LEFT   |  |       
    //  |  +---------+                                 +---------+  |       
    //  |      			                                            |   
    //  |  +---------+                                 +---------+  |       
    //  |  |    4    |           SHOULDERS             |    5    |  |
    //  +  +---------+                                 +---------+  +
    //    +-------------------------------------------------------+
    //  
    //    +-------------------------------------------------------+
    //   /    +--+                 [FRONT]                         \
    //  +     |YU|                                         (3)      +       
    //  |  +--+  +--+        +----+       +----+                    | 
    //  |  |XL    XR|        |  6 |  (X)  |  7 |       (2)     (1)  |       
    //  |  +--+  +--+        +----+       +----+                    | 
    //  |     |YD|                                         (0)      |       
    //  |     +--+     +--+          (X)          +--+              |
    //  |             /    \                     /    \             |
    //  |            |   8  |                   |   9  |            |
    //  |             \    /                     \    /             |
    //  +              +--+                       +--+              +
    //   \                                                         /
    //    \            +-----------------------------+            /
    //     \          /                               \          /
    //      \        /                                 \        /
    //       \      /                                   \      /
    //        +----+                                     +----+
    //
    //
    // ********************************
    // Driver Enums
    // ********************************
    //
    // ---------------------------------
    // Driver Joysticks
    // ---------------------------------
    DRIVER_LEFT_JOYSTICK_Y("Throttle", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_Y), true), 
    DRIVER_LEFT_JOYSTICK_X("Open", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_X), true),
    DRIVER_RIGHT_JOYSTICK_Y("Open", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_Y), true),
    DRIVER_RIGHT_JOYSTICK_X("Heading", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_X), true), 
    
    // ---------------------------------
    // Driver DPAD Buttons
    // ---------------------------------
    DRIVER_DPAD_DOWN("Open", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(0, JoystickConstants.DPAD_Y_DOWN), false), 
    DRIVER_DPAD_LEFT("Open", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(0, JoystickConstants.DPAD_X_LEFT), false), 
    DRIVER_DPAD_RIGHT("Open", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(0, JoystickConstants.DPAD_X_RIGHT), false), 
    DRIVER_DPAD_UP("Open", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(0, JoystickConstants.DPAD_Y_UP), false), 

    // ---------------------------------
    // Driver Buttons
    // --------------------------------- 
    DRIVER_FACE_DOWN("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 0), false), 
    DRIVER_FACE_RIGHT("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 1), false), 
    DRIVER_FACE_LEFT("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 2), false),
    DRIVER_FACE_UP("Base Lock", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 3), false), 
    DRIVER_SHOULDER_LEFT("Antiturbo", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 4), false), 
    DRIVER_SHOULDER_RIGHT("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 5), false), 
    DRIVER_TRIGGER_LEFT("Open", WSInputType.JS_JOYSTICK, new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_TRIGGER), false), 
    DRIVER_TRIGGER_RIGHT("Quick Turn", WSInputType.JS_JOYSTICK, new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_TRIGGER), false), 
    DRIVER_SELECT("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 6), false), 
    DRIVER_START("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 7), false), 
    DRIVER_LEFT_JOYSTICK_BUTTON("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 8), false), 
    DRIVER_RIGHT_JOYSTICK_BUTTON("Open", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 9), false),

    // ---------------------------------
    // Manipulator Joysticks
    // ---------------------------------
    MANIPULATOR_LEFT_JOYSTICK_Y("Control Panel Position Y", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_JOYSTICK_Y), true), 
    MANIPULATOR_LEFT_JOYSTICK_X("Control Panel Position X", WSInputType.JS_JOYSTICK,
           new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_JOYSTICK_X), true), 
    MANIPULATOR_RIGHT_JOYSTICK_Y("Hood Manual Control", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_Y), true), 
    MANIPULATOR_RIGHT_JOYSTICK_X("Turret Manual Control", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_X), true), 

    // ---------------------------------
    // Manipulator DPAD Buttons
    // ---------------------------------
    MANIPULATOR_DPAD_DOWN("Control Panel Retract", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_Y_DOWN), false), 
    MANIPULATOR_DPAD_LEFT("Control Panel Reverse", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_X_LEFT), false), 
    MANIPULATOR_DPAD_RIGHT("Control Panel Forward", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_X_RIGHT), false), 
    MANIPULATOR_DPAD_UP("Control Panel Deploy", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_Y_UP), false), 

    // ---------------------------------
    // Manipulator Buttons
    // ---------------------------------
<<<<<<< HEAD
    RIGHT_TRIGGER("Ballpath Go", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 7), false),

    Y_BUTTON("Ballpath Reverse", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 3), false),

    A_BUTTON("Intake", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 1), false),

=======
    MANIPULATOR_FACE_DOWN("Intake", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 0), false), 
    MANIPULATOR_FACE_RIGHT("Turret Forwards", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 1), false), 
    MANIPULATOR_FACE_LEFT("Turret Backwards", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 2), false), 
    MANIPULATOR_FACE_UP("Reverse Ballpath", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 3), false), 
    MANIPULATOR_SHOULDER_LEFT("Shooter Speed Down", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 4), false), 
    MANIPULATOR_SHOULDER_RIGHT("Shooter Speed Up", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 5), false), 
    MANIPULATOR_TRIGGER_LEFT("Turret Auto-Aim", WSInputType.JS_JOYSTICK, new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_TRIGGER), false), 
    MANIPULATOR_TRIGGER_RIGHT("Fire", WSInputType.JS_JOYSTICK, new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_TRIGGER), false), 
    MANIPULATOR_SELECT("Climb Activation 1", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 6), false), 
    MANIPULATOR_START("Climb Activation 2", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 7), false), 
    MANIPULATOR_LEFT_JOYSTICK_BUTTON("Control Panel Movement", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 8), false), 
    MANIPULATOR_RIGHT_JOYSTICK_BUTTON("Turret Sensor Override", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 9), false), 
>>>>>>> master

    // ********************************
    // Digital IOs
    // ********************************
<<<<<<< HEAD
    // TODO IDs
    STRAFE_LEFT_LIMIT("Strafe left limit", WSInputType.SWITCH, new WsDigitalInputConfig(1, false), false),
    // TODO IDs
    STRAFE_RIGHT_LIMIT("Strafe right limit", WSInputType.SWITCH, new WsDigitalInputConfig(2, false), false),

    // TODO IDs
    LIFT_LOWER_LIMIT("Lift Lower Limit", WSInputType.SWITCH, new WsDigitalInputConfig(3, true), false),
    // TODO IDs
    LIFT_UPPER_LIMIT("Lift Upper Limit", WSInputType.SWITCH, new WsDigitalInputConfig(4, true), false),

    CARRIAGE_SENSOR_A("Carriage Sensor A",WSInputType.SWITCH, new WsDigitalInputConfig(5,true),false),

    CARRIAGE_SENSOR_B("Carriage Sensor B",WSInputType.SWITCH, new WsDigitalInputConfig(6,true),false),

=======
    
>>>>>>> master
    // -------------------------------
    // Networked sensors
    // -------------------------------
    
    // ********************************
    // Others ...
    // ********************************
    GYRO("Gyro", WSInputType.ANALOG_GYRO, new WsAnalogGyroConfig(0, true), false),
    VISION_FRAMES_PROCESSED("nFramesProcessed", WSInputType.REMOTE_ANALOG, new WsRemoteAnalogInputConfig("vision"), false);
    
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
