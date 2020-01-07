package org.wildstang.hardware.crio.inputs;

import org.wildstang.framework.io.inputs.DigitalInput;

import edu.wpi.first.wpilibj.Joystick;

/** A joystick button whose output TOGGLES between true and false with each button press
 * 
 */
public class WsJoystickToggleButton extends DigitalInput {

    private Joystick m_joystick;
    private int m_buttonIndex;
    private boolean m_lastPressed;
    private boolean m_initialState;
    private boolean m_state;

    /** Constructor
     * @param p_name Name of input
     * @param p_port Joystick port number
     * @param p_buttonIndex Which button on controller
     * @param p_initialState Start in true state or false state on initialization
     */
    public WsJoystickToggleButton(String p_name, int p_port,
                                  int p_buttonIndex, boolean p_initialState) {
        super(p_name);
        m_joystick = new Joystick(p_port);
        m_buttonIndex = p_buttonIndex + 1;
        m_lastPressed = false;
        m_state = m_initialState = p_initialState;
    }

    @Override
    protected boolean readRawValue() {
        boolean pressed = m_joystick.getRawButton(m_buttonIndex);
        if (pressed && !m_lastPressed) {
            m_state = !m_state;
        }
        m_lastPressed = pressed;

        return m_state;
    }

    /** Set the toggle state back to the start state */
    public void resetState() {
        m_state = m_initialState;
    }
}
