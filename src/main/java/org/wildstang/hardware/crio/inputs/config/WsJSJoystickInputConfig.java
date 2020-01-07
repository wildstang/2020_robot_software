package org.wildstang.hardware.crio.inputs.config;

import org.wildstang.framework.hardware.InputConfig;

public class WsJSJoystickInputConfig implements InputConfig {
    private int m_port = 0;
    private int m_axis = 0;

    public WsJSJoystickInputConfig(int p_port, int p_axis) {
        m_port = p_port;
        m_axis = p_axis;
    }

    public int getPort() {
        return m_port;
    }

    public int getAxis() {
        return m_axis;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("{\"port\": ");
        buf.append(m_port);
        buf.append(",\"axis\": ");
        buf.append(m_axis);
        buf.append("}");

        return buf.toString();
    }

}
