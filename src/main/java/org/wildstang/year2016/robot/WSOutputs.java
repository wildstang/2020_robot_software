package org.wildstang.year2016.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.io.outputs.OutputType;
import org.wildstang.hardware.crio.outputs.WSOutputType;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoidState;
import org.wildstang.hardware.crio.outputs.WsRelayState;
import org.wildstang.hardware.crio.outputs.config.WsDoubleSolenoidConfig;
import org.wildstang.hardware.crio.outputs.config.WsI2COutputConfig;
import org.wildstang.hardware.crio.outputs.config.WsRelayConfig;
import org.wildstang.hardware.crio.outputs.config.WsSolenoidConfig;
import org.wildstang.hardware.crio.outputs.config.WsVictorConfig;

import edu.wpi.first.wpilibj.I2C;

public enum WSOutputs implements Outputs {
    LEFT_1("Left motor 1", WSOutputType.VICTOR, new WsVictorConfig(0, 0.0), getLogging()),
    LEFT_2("Left motor 2", WSOutputType.VICTOR, new WsVictorConfig(1, 0.0), getLogging()),
    RIGHT_1("Right motor 1", WSOutputType.VICTOR, new WsVictorConfig(2, 0.0), getLogging()),
    RIGHT_2("Right motor 2", WSOutputType.VICTOR, new WsVictorConfig(3, 0.0), getLogging()),

    SHOOTER("Shooter flywheel", WSOutputType.VICTOR, new WsVictorConfig(6, 0.0), getLogging()),
    WINCH_LEFT("Left Winch", WSOutputType.VICTOR, new WsVictorConfig(4, 0.0), getLogging()),
    WINCH_RIGHT("Right Winch", WSOutputType.VICTOR, new WsVictorConfig(5, 0.0), getLogging()),
    FRONT_ROLLER("Front intake roller", WSOutputType.VICTOR, new WsVictorConfig(7, 0.0),
            getLogging()),
    FRONT_ROLLER_2("Front intake roller2", WSOutputType.VICTOR, new WsVictorConfig(8, 0.0),
            getLogging()),

    LED("LEDs", WSOutputType.I2C, new WsI2COutputConfig(I2C.Port.kMXP, 0x10), true),
    RING_LIGHT("Light", WSOutputType.RELAY, new WsRelayConfig(0, WsRelayState.RELAY_ON),
            getLogging()),
    // Solenoids

    SHIFTER("Shifter double solenoid", WSOutputType.SOLENOID_DOUBLE,
            new WsDoubleSolenoidConfig(1, 0, 1, WsDoubleSolenoidState.FORWARD), getLogging()),
    INTAKE_DEPLOY("Intake deploy", WSOutputType.SOLENOID_DOUBLE,
            new WsDoubleSolenoidConfig(1, 2, 7, WsDoubleSolenoidState.REVERSE), getLogging()),
    INTAKE_FRONT_LOWER("Intake front lower", WSOutputType.SOLENOID_SINGLE,
            new WsSolenoidConfig(1, 3, false), getLogging()),
    // LOWER_ARM("Lower Lift Arm", WSOutputType.SOLENOID_SINGLE, new
    // WsSolenoidConfig(1, 4, false), getLogging()),
    // UPPER_ARM("Upper Lift Arm", WSOutputType.SOLENOID_SINGLE, new
    // WsSolenoidConfig(1, 5, false), getLogging()),
    LEFT_BRAKE("Left Winch Brake", WSOutputType.SOLENOID_SINGLE, new WsSolenoidConfig(1, 4, false),
            getLogging()),
    RIGHT_BRAKE("Right Winch Brake", WSOutputType.SOLENOID_SINGLE,
            new WsSolenoidConfig(1, 5, false), getLogging()),
    HOOK_EXTENSION("Hook Extenstion", WSOutputType.SOLENOID_SINGLE,
            new WsSolenoidConfig(1, 6, false), getLogging()),
    SHOOTER_HOOD("Shooter Hood", WSOutputType.SOLENOID_DOUBLE,
            new WsDoubleSolenoidConfig(2, 0, 1, WsDoubleSolenoidState.REVERSE), getLogging()),
    // WINCH_BRAKE("Stop the winches", WSOutputType.SOLENOID_SINGLE, new
    // WsSolenoidConfig(2, 3, false), getLogging());
    ARMS("Both Arms", WSOutputType.SOLENOID_SINGLE, new WsSolenoidConfig(2, 3, false),
            getLogging());

    private String m_name;
    private OutputType m_type;
    private OutputConfig m_config;
    private boolean m_trackingState;

    private static boolean isLogging = false;

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

    @Override
    public OutputConfig getConfig() {
        return m_config;
    }

    @Override
    public boolean isTrackingState() {
        return m_trackingState;
    }

    public static boolean getLogging() {
        return isLogging;
    }

}
