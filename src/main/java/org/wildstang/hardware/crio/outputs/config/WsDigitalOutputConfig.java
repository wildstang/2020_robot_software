package org.wildstang.hardware.crio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;

public class WsDigitalOutputConfig implements OutputConfig {
    private int m_channel = 0;
    private boolean m_default;

    public WsDigitalOutputConfig(int channel, boolean p_default) {
        m_channel = channel;
        m_default = p_default;
    }

    public int getChannel() {
        return m_channel;
    }

    public boolean getDefault() {
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
