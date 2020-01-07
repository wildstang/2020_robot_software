package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.DriveBase;

public class StepStartDriveUsingMotionProfile extends AutoStep {

    private double distance;
    private double speed;
    private DriveBase driveBase;

    public StepStartDriveUsingMotionProfile(double distanceInInches, double speed) {
        this.distance = distanceInInches;
        this.speed = Math.abs(speed);

    }

    @Override
    public void initialize() {
        driveBase = ((DriveBase) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName()));
        driveBase.resetLeftEncoder();
        driveBase.resetRightEncoder();
        // ((WsMotionProfileControl)Core.getInputManager().getInput(WSInputs.MOTION_PROFILE_CONTROL.getName())).setProfileEnabled(true);

        driveBase.startStraightMoveWithMotionProfile(this.distance, this.speed);
        setFinished(true);
    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return "Start Motion Profile Drive";
    }

}