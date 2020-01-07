package org.wildstang.year2017.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.io.outputs.OutputType;
import org.wildstang.hardware.crio.outputs.WSOutputType;
import org.wildstang.hardware.crio.outputs.config.WsI2COutputConfig;
import org.wildstang.hardware.crio.outputs.config.WsSolenoidConfig;
import org.wildstang.hardware.crio.outputs.config.WsVictorConfig;
import org.wildstang.hardware.crio.outputs.config.WsDigitalOutputConfig;

import edu.wpi.first.wpilibj.I2C;

public enum WSOutputs implements Outputs {
    // ********************************
    // PWM Outputs
    // ********************************
    // ---------------------------------
    // Motors
    // ---------------------------------
    FEEDER_LEFT("Left Feeder Motor", WSOutputType.VICTOR, new WsVictorConfig(0, 0.0), false), // Shooter
                                                                                              // Subsystem
    FEEDER_RIGHT("Right Feeder Motor", WSOutputType.VICTOR, new WsVictorConfig(1, 0.0), false), // Shooter
                                                                                                // Subsystem
    INTAKE("Intake Motor", WSOutputType.VICTOR, new WsVictorConfig(2, 0.0), false), // Intake
                                                                                    // Subsystem
    WINCH("Winch motor", WSOutputType.VICTOR, new WsVictorConfig(3, 0.0), false), // Winch Subsystem
    BLENDER("Blender motor", WSOutputType.VICTOR, new WsVictorConfig(4, 0.0), false), // Blender
                                                                                      // Subsystem

    // ---------------------------------
    // Servos
    // ---------------------------------
    // SERVO_0("Test Servo 0", WSOutputType.SERVO, new WsServoConfig(0, 0.0),
    // false), // PWM 0, Initial Rotation Angle 0.0

    // ********************************
    // DIO Outputs
    // ********************************
    // DIO_O_0("Test Digital Output 0", WSOutputType.DIGITAL_OUTPUT, new
    // WsDigitalOutputConfig(0, true), false), // Channel 0, Initially Low

    // ********************************
    // Solenoids
    // ********************************
    SHIFTER("Shifter single solenoid", WSOutputType.SOLENOID_SINGLE,
            new WsSolenoidConfig(0, 7, false), false), // Ctrl 1, Pins 0 Driver Subsystem
    // GATE("Gate", WSOutputType.SOLENOID_SINGLE, new WsSolenoidConfig(0, 4, false),
    // false), // Ctrl 1, Pin 1 Shooter Subsystem
    GEAR_HOLD("Gear Doors", WSOutputType.SOLENOID_SINGLE, new WsSolenoidConfig(0, 6, false), false), // Ctrl
                                                                                                     // 1,
                                                                                                     // Pin
                                                                                                     // 2
                                                                                                     // Gear
                                                                                                     // Subsystem
    GEAR_TILT("Gear Tilt", WSOutputType.SOLENOID_SINGLE, new WsSolenoidConfig(0, 5, false), false), // Ctrl
                                                                                                    // 1,
                                                                                                    // Pin
                                                                                                    // 3
                                                                                                    // Gear
                                                                                                    // Subsystem

    // ********************************
    // Relays
    // ********************************
    // RELAY_0("Relay 0", WSOutputType.RELAY, new WsRelayConfig(0,
    // WsRelayState.RELAY_OFF), false), // Relay 0, Both Off
    // RELAY_1("Relay 1", WSOutputType.RELAY, new WsRelayConfig(1,
    // WsRelayState.RELAY_OFF), false), // Relay 1, Both Off
    // RELAY_2("Relay 2", WSOutputType.RELAY, new WsRelayConfig(2,
    // WsRelayState.RELAY_OFF), false), // Relay 2, Both Off
    // RELAY_3("Relay 3", WSOutputType.RELAY, new WsRelayConfig(3,
    // WsRelayState.RELAY_OFF), false), // Relay 3, Both Off

    // ********************************
    // Others ...
    // ********************************
    LED("LEDs", WSOutputType.I2C, new WsI2COutputConfig(I2C.Port.kMXP, 0x10), false);

    private String m_name;
    private OutputType m_type;
    private OutputConfig m_config;
    private boolean m_trackingState;

    private static boolean isLogging = true;

    WSOutputs(String p_name, OutputType p_type, OutputConfig p_config, boolean p_trackingState) {
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
    public OutputType getType() {
        return m_type;
    }

    public OutputConfig getConfig() {
        return m_config;
    }

    public boolean isTrackingState() {
        return m_trackingState;
    }

    public static boolean getLogging() {
        return isLogging;
    }

}
