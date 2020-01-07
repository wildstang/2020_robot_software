package org.wildstang.hardware.crio.inputs;

import org.wildstang.framework.io.inputs.DigitalInput;

import edu.wpi.first.wpilibj.Joystick;

public class WsJoystickButton extends DigitalInput {

    Joystick m_joystick;
    int m_buttonIndex;

    public WsJoystickButton(String p_name, int p_port, int p_buttonIndex) {
        super(p_name);
        m_joystick = new Joystick(p_port);
        m_buttonIndex = p_buttonIndex + 1;
    }

    @Override
    protected boolean readRawValue() {
        return m_joystick.getRawButton(m_buttonIndex);
    }

}
