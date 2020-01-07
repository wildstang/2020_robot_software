package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.AnalogOutput;

import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author Nathan
 */
public class WsVictor extends AnalogOutput {

    Victor victor;

    // By giving the victor1 number in the constructor we can make this generic
    // for all digital victor1s
    public WsVictor(String name, int channel, double p_default) {
        super(name, p_default);
        this.victor = new Victor(channel);
    }

    @Override
    protected void sendDataToOutput() {
        victor.set(getValue());
    }

    public void notifyConfigChange() {
        // Nothing to update here, since the config value only affect the
        // start state.
    }
}
