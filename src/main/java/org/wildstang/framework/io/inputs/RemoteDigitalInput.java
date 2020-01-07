package org.wildstang.framework.io.inputs;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class RemoteDigitalInput extends DigitalInput {

    NetworkTable remoteIOTable;

    public RemoteDigitalInput(String p_name, String p_networkTbl) {
        super(p_name);
        remoteIOTable = NetworkTableInstance.getDefault().getTable(p_networkTbl);

    }

    @Override
    public boolean readRawValue() {
        return remoteIOTable.getEntry(getName()).getBoolean(false);
    }
}