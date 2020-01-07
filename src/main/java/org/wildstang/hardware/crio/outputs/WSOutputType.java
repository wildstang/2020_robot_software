package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.OutputType;

public enum WSOutputType implements OutputType {
    DIGITAL_OUTPUT("Digital"),
    SERVO("Servo"),
    SOLENOID_SINGLE("Solenoid"),
    SOLENOID_DOUBLE("Double solenoid"),
    VICTOR("Victor"),
    TALON("Talon"),
    RELAY("Relay"),
    I2C("I2C"),
    REMOTE_ANALOG("Remote Analog"),
    REMOTE_DIGITAL("Remote Digital"),
    NULL("Null");

    private String m_typeStr;

    WSOutputType(String p_typeStr) {
        m_typeStr = p_typeStr;
    }

    @Override
    public String toString() {
        return m_typeStr;
    }

}
