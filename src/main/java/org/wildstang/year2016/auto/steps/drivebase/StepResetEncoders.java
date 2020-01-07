package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.DriveBase;

public class StepResetEncoders extends AutoStep {
    DriveBase driveBase = ((DriveBase) Core.getSubsystemManager()
            .getSubsystem(WSSubsystems.DRIVE_BASE.getName()));

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        driveBase.resetLeftEncoder();
        driveBase.resetRightEncoder();
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        setFinished(true);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

}
