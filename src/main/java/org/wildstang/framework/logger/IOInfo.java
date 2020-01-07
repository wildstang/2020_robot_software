package org.wildstang.framework.logger;

public class IOInfo {
    private String m_name;
    private String m_type;
    private String m_direction;
    private Object m_port;

    public IOInfo(String p_name, String p_type, String p_direction, Object p_port) {
        m_name = p_name;
        m_type = p_type;
        m_direction = p_direction;
        m_port = p_port;
    }

    public String getName() {
        return m_name;
    }

    public String getType() {
        return m_type;
    }

    public Object getPort() {
        return m_port;
    }

    public String getDirection() {
        return m_direction;
    }

}
