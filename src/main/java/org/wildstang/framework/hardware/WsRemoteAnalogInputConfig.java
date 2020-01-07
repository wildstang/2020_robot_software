package org.wildstang.framework.hardware;

public class WsRemoteAnalogInputConfig implements InputConfig {
    private String m_networktbl = "remoteIO";

    public WsRemoteAnalogInputConfig(String networktbl) {
        m_networktbl = networktbl;
    }

    public String getTableName() {
        return m_networktbl;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("{\"networktbl\": ");
        buf.append(m_networktbl);
        buf.append("}");

        return buf.toString();
    }
}