package org.wildstang.hardware.crio.inputs;

import org.wildstang.framework.io.inputs.AnalogInput;

import edu.wpi.first.wpilibj.Joystick;

public class WsJoystickAxis extends AnalogInput {

    Joystick m_joystick;
    int m_axisIndex;

    public WsJoystickAxis(String p_name, int p_port, int p_axisIndex) {
        super(p_name);
        m_joystick = new Joystick(p_port);
        m_axisIndex = p_axisIndex;
    }

    @Override
    protected double readRawValue() {
        double value;

        // Invert the vertical axes so that full up is 1
        if (m_axisIndex % 2 == 0) {
            value = m_joystick.getRawAxis(m_axisIndex);
        } else {
            value = m_joystick.getRawAxis(m_axisIndex) * -1;
        }

        return value;
    }

}
