package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.DigitalOutput;

/**
 *
 */
public class WsDigitalOutput extends DigitalOutput {

    edu.wpi.first.wpilibj.DigitalOutput output;

    // By giving the output number in the constructor we can make this generic
    // for all digital outputs

    public WsDigitalOutput(String name, int channel, boolean p_default) {
        super(name, p_default);
        output = new edu.wpi.first.wpilibj.DigitalOutput(channel);
    }

    public void notifyConfigChange() {
        // Nothing to update here, since the config value only affect the
        // start state.
    }

    @Override
    protected void sendDataToOutput() {
        this.output.set(getValue());
    }

}
