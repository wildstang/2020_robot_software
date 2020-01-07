package org.wildstang.hardware.crio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.hardware.crio.outputs.WsRelayState;

public class WsRelayConfig implements OutputConfig {
    private int m_channel = 0;
    private WsRelayState m_default;

    public WsRelayConfig(int channel, WsRelayState p_default) {
        m_channel = channel;
        m_default = p_default;
    }

    public int getChannel() {
        return m_channel;
    }

    public WsRelayState getDefault() {
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
