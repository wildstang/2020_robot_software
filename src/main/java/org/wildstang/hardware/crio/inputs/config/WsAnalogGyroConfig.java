package org.wildstang.hardware.crio.inputs.config;

import org.wildstang.framework.hardware.InputConfig;

public class WsAnalogGyroConfig implements InputConfig {
    private int m_channel = 0;
    private boolean m_compensate = true;

    public WsAnalogGyroConfig(int channel, boolean driftCompensate) {
        m_channel = channel;
        m_compensate = driftCompensate;
    }

    public int getChannel() {
        return m_channel;
    }

    public boolean getCompensate() {
        return m_compensate;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("{\"channel\": ");
        buf.append(m_channel);
        buf.append(",\"driftCompensation\": ");
        buf.append(m_compensate);
        buf.append("}");

        return buf.toString();
    }
}
