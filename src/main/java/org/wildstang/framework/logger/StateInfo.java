package org.wildstang.framework.logger;

public class StateInfo {
    private String m_name;
    private String m_parent;
    private Object m_value;

    public StateInfo(String p_name, String p_parent, Object p_value) {
        m_name = p_name;
        m_parent = p_parent;
        m_value = p_value;
    }

    public String getName() {
        return m_name;
    }

    public String getParent() {
        return m_parent;
    }

    public Object getValue() {
        return m_value;
    }

}
