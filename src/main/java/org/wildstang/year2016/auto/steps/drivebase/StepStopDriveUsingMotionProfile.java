package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.DriveBase;

public class StepStopDriveUsingMotionProfile extends AutoStep {

    private DriveBase driveBase;

    public StepStopDriveUsingMotionProfile() {

    }

    @Override
    public void initialize() {
        driveBase = ((DriveBase) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName()));

        driveBase.stopStraightMoveWithMotionProfile();
        setFinished(true);
    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return "Stop Motion Profile Drive";
    }

}