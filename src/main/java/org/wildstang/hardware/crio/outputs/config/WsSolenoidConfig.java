package org.wildstang.hardware.crio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;

public class WsSolenoidConfig implements OutputConfig {
    private int m_module;
    private int m_channel;
    private boolean m_default;

    public WsSolenoidConfig(int module, int channel, boolean p_default) {
        m_module = module;
        m_channel = channel;
        m_default = p_default;
    }

    public int getChannel() {
        return m_channel;
    }

    public boolean getDefault() {
        return m_default;
    }

    public int getModule() {
        return m_module;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("{\"module\": ");
        buf.append(m_module);
        buf.append(", \"channel\": ");
        buf.append(m_channel);
        buf.append("}");

        return buf.toString();
    }

}
