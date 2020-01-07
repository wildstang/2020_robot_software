package org.wildstang.framework.logger;

import java.util.Date;
import java.util.HashMap;

public class StateGroup {

    private Date m_timestamp;
    private HashMap<String, StateInfo> m_stateList = new HashMap<>();

    public StateGroup(Date p_timestamp) {
        m_timestamp = p_timestamp;
    }

    public void addState(String p_name, String p_parent, Object p_value) {
        StateInfo state = new StateInfo(p_name, p_parent, p_value);
        m_stateList.put(p_name, state);
    }

    public Date getTimestamp() {
        return m_timestamp;
    }

    public HashMap<String, StateInfo> getStateList() {
        return m_stateList;
    }

}
