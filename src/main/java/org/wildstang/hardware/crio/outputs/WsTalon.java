package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.AnalogOutput;

import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Nathan
 */
public class WsTalon extends AnalogOutput {

    Talon talon;

    // By giving the victor1 number in the constructor we can make this generic
    // for all digital victor1s
    public WsTalon(String name, int channel, double p_default) {
        super(name, p_default);
        this.talon = new Talon(channel);

    }

    @Override
    public void sendDataToOutput() {
        talon.set(getValue());
    }

    public void notifyConfigChange() {
        // Nothing to update here, since the config value only affect the
        // start state.
    }
}
