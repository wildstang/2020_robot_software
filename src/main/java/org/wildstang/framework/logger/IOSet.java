package org.wildstang.framework.logger;

import java.util.ArrayList;
import java.util.List;

public class IOSet {
    private ArrayList<IOInfo> m_infoList = new ArrayList<>();

    public IOSet() {
    }

    public void addIOInfo(String p_name, String p_type, String p_direction, Object p_port) {
        IOInfo info = new IOInfo(p_name, p_type, p_direction, p_port);
        m_infoList.add(info);
    }

    public List<IOInfo> getInfoList() {
        return new ArrayList<>(m_infoList);
    }

}
