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
    //  +  +---------+              [TOP]              +---------+  +       
    //  |  |    6    |                                 |    7    |  |       
    //  |  +---------+                                 +---------+  |       
    //  |      			                                           |   
    //  |  +---------+                                 +---------+  |       
    //  |  |    4    |                                 |    5    |  |
    //  +  +---------+                                 +---------+  +
    //    +-------------------------------------------------------+
    //  
    //    +-------------------------------------------------------+
    //   /    +--+                 [FRONT]                         \
    //  +     |YU|                                         (3)      +       
    //  |  +--+  +--+        +----+       +----+                    | 
    //  |  |XL    XR|        |  8 |  (X)  |  9 |       (0)     (2)  |       
    //  |  +--+  +--+        +----+       +----+                    | 
    //  |     |YD|                                         (1)      |       
    //  |     +--+     +--+          (X)          +--+              |
    //  |             /    \                     /    \             |
    //  |            |  10  |                   |  11  |            |
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
    DRIVE_THROTTLE("Throttle", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_Y), true), // Driver
                                                                                      // Subsystem
    DRIVE_HEADING("Heading", WSInputType.JS_JOYSTICK,
            new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_X), true), // Driver
                                                                                       // Subsystem

    // ---------------------------------
    // Driver Buttons
    // ---------------------------------
    ANTITURBO("Antiturbo", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 4), false), // Drive
                                                                                           // Subsystem
    SHIFT("Driver Shift", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 2), false), // Driver
                                                                                          // Subsystem
    QUICK_TURN("Quick Turn", WSInputType.JS_JOYSTICK, new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_TRIGGER), false), // Driver
                                                                                             // Subsystem
    BASE_LOCK("Base lock", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 3), false), // Driver
                                                                                           // Subsystem
    BUMPER("BUMPER", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 1), false),

    AUTO_E_STOP("Auto E-Stop", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(0, 0), false), // Drive/Auto
                                                                                               // Subsystem

    // ---------------------------------
    // Manipulator Joysticks
    // ---------------------------------

    LIMELIGHT_AIM("Limelight Aiming Toggle", WSInputType.JS_JOYSTICK,  //Launcher Subsystem (LT)
        new WsJSJoystickInputConfig(1, 3), true), 

    HOPPER("Hopper", WSInputType.JS_JOYSTICK,  //Ballpath (RT)
        new WsJSJoystickInputConfig(1, 2), true), 

    HOOD_MANUAL("Manual Hood Aiming", WSInputType.JS_JOYSTICK, 
        new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_Y), true),  //Launcher Subsystem

    TURRET_MANUAL("Manual Turret Aiming", WSInputType.JS_JOYSTICK,
        new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_X), true), //Launcher Subsystem
    
    // ---------------------------------
    // Manipulator DPAD Buttons
    // ---------------------------------
    CP_RETURN("Control Panel Return", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_Y_DOWN), false), // CP
                                                                              
    CP_FORWARD("Control Panel Forward", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_X_LEFT), false), // CP
                                                                                 
    CP_BACKWARD("Control Panel Backward", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_X_RIGHT), false), // CP
                                                                                  
    CP_DEPLOY("Control Panel Deploy", WSInputType.JS_DPAD_BUTTON,
            new WsJSButtonInputConfig(1, JoystickConstants.DPAD_Y_UP), false), // CP
                                                                                 

    // ---------------------------------
    // Manipulator Buttons
    // ---------------------------------
    TURRET_BACK("Turret back Preset", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 2), false), // Launcher (X)
                                                                                    
    INTAKE("Intake", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 0), false), // CP (A)
                                                                                         
    TURRET_FRONT("Turret front preset", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 1), false), // Launcher (B)
                                                                                                        
    BALLPATH_REVERSE("Ballpath Reverse", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 3), false), // Ballpath (Y)
                                                                                                   
    CP_ROTATIONS("Control Panel Rotations   ", WSInputType.JS_BUTTON, new WsJSButtonInputConfig(1, 8), false), //CP (LC)

    // ********************************
    // Digital IOs
    // ********************************
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

    // -------------------------------
    // Networked sensors
    // -------------------------------
    /** Message from the RasPi telling us where the line is */
    // FIXME TABLE NAME
    LINE_POSITION("Line position", WSInputType.REMOTE_ANALOG, new WsRemoteAnalogInputConfig("line"), false),

    // ********************************
    // Others ...
    // ********************************
    GYRO("Gyro", WSInputType.ANALOG_GYRO, new WsAnalogGyroConfig(0, true), false),
    // IMU("IMU", WSInputType.COMPASS, new WsI2CInputConfig(I2C.Port.kMXP, 0x20),
    // true);

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
