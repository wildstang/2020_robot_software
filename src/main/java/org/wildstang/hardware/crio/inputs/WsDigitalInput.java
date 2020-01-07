package org.wildstang.hardware.crio.inputs;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 *
 */
public class WsDigitalInput extends org.wildstang.framework.io.inputs.DigitalInput {

    DigitalInput input;
    private boolean m_pullup = false;

    // By giving the input number in the constructor we can make this generic
    // for all digital inputs
    public WsDigitalInput(String p_name, int channel, boolean p_pullup) {
        super(p_name);
        m_pullup = p_pullup;

        this.input = new DigitalInput(channel);
    }

    @Override
    public boolean readRawValue() {
        if (m_pullup) {
            return !input.get();
        } else {
            return input.get();
        }
    }
}
