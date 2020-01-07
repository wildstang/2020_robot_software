package org.wildstang.hardware.crio.inputs.driverstation;

import org.wildstang.framework.io.inputs.AnalogInput;

/**
 *
 */
public class WsDSAnalogInput extends AnalogInput {

    int channel;

    // By giving the input number in the constructor we can make this generic
    // for all analog inputs
    public WsDSAnalogInput(String name, int channel) {
        super(name);
        this.channel = channel;
    }

    @Override
    protected double readRawValue() {
        // NOTE: getAnalogIn() no longer available in Driverstation API
        // analogValue.setValue(DriverStation.getInstance().getAnalogIn(channel));

        return 0;
    }
}
