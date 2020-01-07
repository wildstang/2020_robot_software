package org.wildstang.framework.io.inputs;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 *
 */
public class RemoteAnalogInput extends AnalogInput {

    NetworkTable remoteIOTable;

    public RemoteAnalogInput(String p_name, String p_networkTbl) {
        super(p_name);
        remoteIOTable = NetworkTableInstance.getDefault().getTable(p_networkTbl);
    }

    @Override
    public double readRawValue() {
        return remoteIOTable.getEntry(getName()).getDouble(0);
    }

}