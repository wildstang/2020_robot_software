package org.wildstang.hardware.crio.inputs.driverstation;

import org.wildstang.framework.io.inputs.DigitalInput;

/**
 *
 */
public class WsDSDigitalInput extends DigitalInput {

    int channel;

    // By giving the input number in the constructor we can make this generic
    // for all digital inputs
    public WsDSDigitalInput(String name, int channel) {
        super(name);

        this.channel = channel;
    }

    @Override
    protected boolean readRawValue() {
        // NOTE: getDigitalIn() is no longer available in DriverStation API
        // digitalValue.setValue(DriverStation.getInstance().getDigitalIn(channel));

        return false;
    }

}