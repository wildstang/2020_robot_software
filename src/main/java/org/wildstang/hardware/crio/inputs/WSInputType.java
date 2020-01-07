package org.wildstang.hardware.crio.inputs;

import org.wildstang.framework.io.inputs.InputType;

public enum WSInputType implements InputType {
    SWITCH("Digital"),
    HALL_EFFECT("Hall Effect"),
    POT("Analog"),
    JS_BUTTON("Joystick Button"),
    JS_JOYSTICK("Joystick"),
    JS_DPAD("Joystick DPad"),
    JS_DPAD_BUTTON("Joystick DPad Button"),
    LIDAR("LIDAR"),
    I2C("I2C"),
    COMPASS("Compass"),
    ANALOG_GYRO("Analog Gyro"),
    ABSOLUTE_ENCODER("Absolute Encoder"),
    MOTION_PROFILE_CONTROL("Motion Profile Control"),
    CAMERA("Camera"),
    REMOTE_ANALOG("RemoteAnalogInput"),
    REMOTE_DIGITAL("RemoteDigitalInput"),
    NULL("Null");

    private String m_typeStr;

    WSInputType(String p_typeStr) {
        m_typeStr = p_typeStr;
    }

    @Override
    public String toString() {
        return m_typeStr;
    }

}
