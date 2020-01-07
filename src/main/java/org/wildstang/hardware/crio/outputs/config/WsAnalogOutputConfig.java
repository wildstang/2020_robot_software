package org.wildstang.hardware.crio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;

public class WsAnalogOutputConfig implements OutputConfig {
    private int m_channel = 0;
    private double m_default;

    public WsAnalogOutputConfig(int channel, double p_default) {
        m_channel = channel;
        m_default = p_default;
    }

    public int getChannel() {
        return m_channel;
    }

    public double getDefault() {
        return m_default;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("{\"channel\": ");
        buf.append(m_channel);
        buf.append("}");

        return buf.toString();
    }

}
