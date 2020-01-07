package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.DiscreteOutput;

import edu.wpi.first.wpilibj.Relay;

/**
 *
 */
public class WsRelay extends DiscreteOutput {
    private Relay relay;

    public WsRelay(String p_name, int channel)// , Direction direction)
    {
        // TODO: Need to handle direction in the output factory
        super(p_name);
        relay = new Relay(channel, Relay.Direction.kBoth);// , direction);
    }

    @Override
    public void sendDataToOutput() {
        Relay.Value value = Relay.Value.kOff;

        if (getValue() == WsRelayState.RELAY_ON.ordinal()) {
            value = Relay.Value.kOn;
        } else if (getValue() == WsRelayState.RELAY_FORWARD.ordinal()) {
            value = Relay.Value.kForward;
        } else if (getValue() == WsRelayState.RELAY_REVERSE.ordinal()) {
            value = Relay.Value.kReverse;
        } else if (getValue() == WsRelayState.RELAY_OFF.ordinal()) {
            value = Relay.Value.kOff;
        }

        relay.set(value);
    }

}
