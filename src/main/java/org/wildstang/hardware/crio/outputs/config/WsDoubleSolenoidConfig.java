package org.wildstang.hardware.crio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoidState;

public class WsDoubleSolenoidConfig implements OutputConfig {
    private int m_module;
    private int m_channel1;
    private int m_channel2;
    private WsDoubleSolenoidState m_default;

    public WsDoubleSolenoidConfig(int module, int channel1, int channel2,
            WsDoubleSolenoidState p_default) {
        m_module = module;
        m_channel1 = channel1;
        m_channel2 = channel2;
        m_default = p_default;
    }

    public int getChannel1() {
        return m_channel1;
    }

    public int getChannel2() {
        return m_channel2;
    }

    public WsDoubleSolenoidState getDefault() {
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
        buf.append(", \"channel1\": ");
        buf.append(m_channel1);
        buf.append(",\"channel2\": ");
        buf.append(m_channel2);
        buf.append("}");

        return buf.toString();
    }

}
