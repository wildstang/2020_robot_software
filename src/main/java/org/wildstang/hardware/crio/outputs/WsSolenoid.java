package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.DigitalOutput;

import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 */
public class WsSolenoid extends DigitalOutput {

    Solenoid solenoid;

    public WsSolenoid(String name, int module, int channel1, boolean p_default) {
        super(name, p_default);

        solenoid = new Solenoid(module, channel1);
        solenoid.set(p_default);

    }

    public WsSolenoid(String name, int channel1) {
        this(name, 0, channel1, false);
    }

    @Override
    public void sendDataToOutput() {
        solenoid.set(getValue());
    }

    public void notifyConfigChange() {
        // Nothing to update here, since the config value only affect the
        // start state.
    }
}
