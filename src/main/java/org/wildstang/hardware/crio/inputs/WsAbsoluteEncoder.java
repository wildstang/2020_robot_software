package org.wildstang.hardware.crio.inputs;

public class WsAbsoluteEncoder extends WsAnalogInput {
    private int m_maxVoltage;

    public WsAbsoluteEncoder(String p_name, int channel, int p_maxVoltage) {
        super(p_name, channel);
        m_maxVoltage = p_maxVoltage;
    }

    @Override
    public double readRawValue() {
        double rawValue = super.readRawValue();

        double position = (rawValue / m_maxVoltage) * 360;

        return position;
    }

}
