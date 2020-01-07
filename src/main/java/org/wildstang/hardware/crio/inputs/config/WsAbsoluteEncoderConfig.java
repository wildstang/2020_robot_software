package org.wildstang.hardware.crio.inputs.config;

import org.wildstang.framework.hardware.InputConfig;

public class WsAbsoluteEncoderConfig implements InputConfig {
    private int m_channel = 0;
    private int m_maxVoltage = 0;

    public WsAbsoluteEncoderConfig(int channel, int p_maxVoltage) {
        m_channel = channel;
        m_maxVoltage = p_maxVoltage;
    }

    public int getChannel() {
        return m_channel;
    }

    public int getMaxVoltage() {
        return m_maxVoltage;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("{\"channel\": ");
        buf.append(m_channel);
        buf.append(",\"maxVoltage\": ");
        buf.append(m_maxVoltage);
        buf.append("}");

        return buf.toString();
    }

}
