package org.wildstang.year2016.robot;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.pid.input.IPidInput;
import org.wildstang.year2016.subsystems.DriveBase;

/**
 *
 * @author Nathan
 */
public class WsDriveBaseSpeedPidInput implements IPidInput {

    public WsDriveBaseSpeedPidInput() {
        // Nothing to do here
    }

    @Override
    public double pidRead() {
        // left_encoder_value = ((WsDriveBase)
        // WsSubsystemContainer.getInstance().getSubsystem(WsSubsystemContainer.WS_DRIVE_BASE)).getLeftDistance();
        double currentVelocity = ((DriveBase) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName())).getVelocity();
        return currentVelocity;
    }
}
