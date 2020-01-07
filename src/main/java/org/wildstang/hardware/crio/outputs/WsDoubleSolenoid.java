package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.DiscreteOutput;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 *
 */

public class WsDoubleSolenoid extends DiscreteOutput {

    DoubleSolenoid solenoid;

    public WsDoubleSolenoid(String name, int module, int channel1, int channel2,
            WsDoubleSolenoidState p_default) {
        super(name, p_default.ordinal());

        solenoid = new DoubleSolenoid(module, channel1, channel2);
    }

    @Override
    protected void sendDataToOutput() {
        DoubleSolenoid.Value solValue = DoubleSolenoid.Value.kOff;

        if (getValue() == WsDoubleSolenoidState.FORWARD.ordinal()) {
            solValue = DoubleSolenoid.Value.kForward;
        } else if (getValue() == WsDoubleSolenoidState.REVERSE.ordinal()) {
            solValue = DoubleSolenoid.Value.kReverse;
        } else if (getValue() == WsDoubleSolenoidState.OFF.ordinal()) {
            solValue = DoubleSolenoid.Value.kOff;
        }

        solenoid.set(solValue);
    }

}
