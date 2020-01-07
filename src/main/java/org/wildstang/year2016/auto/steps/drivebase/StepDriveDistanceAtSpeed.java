package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.DriveBase;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class StepDriveDistanceAtSpeed extends AutoStep {

    private final double DRIFTING_COMPENSATION_FACTOR;
    private final boolean USE_DRIFTING_COMPENSATION_FACTOR_CONFIG;

    private static final long MILLIS_TO_REVERSE = 200;

    private double distance;
    private double speed;
    private boolean hasReachedTarget = false;
    private long timeWhenTargetReached;
    private boolean shouldHardStop;

    private DriveBase driveBase;

    public StepDriveDistanceAtSpeed(double distanceInInches, double speed, boolean shouldHardStop) {
        this.distance = distanceInInches;
        this.speed = Math.abs(speed);
        this.shouldHardStop = shouldHardStop;
        DRIFTING_COMPENSATION_FACTOR = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + ".drifting_compensation_factor", 0.05);
        USE_DRIFTING_COMPENSATION_FACTOR_CONFIG = Core.getConfigManager().getConfig()
                .getBoolean(this.getClass().getName() + ".use_comp", false);
        SmartDashboard.putBoolean("Using comp factor", USE_DRIFTING_COMPENSATION_FACTOR_CONFIG);
    }

    @Override
    public void initialize() {
        driveBase = ((DriveBase) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName()));
        driveBase.resetLeftEncoder();
        driveBase.resetRightEncoder();
        if (distance < 0) {
            speed = -speed;
        }

        ((AnalogInput) Core.getInputManager().getInput(WSInputs.DRV_THROTTLE.getName()))
                .setValue(speed);
        hasReachedTarget = false;
    }

    @Override
    public void update() {
        double leftDistance = driveBase.getLeftDistance();
        double rightDistance = driveBase.getRightDistance();
        if (!hasReachedTarget) {
            if (Math.abs(leftDistance) > Math.abs(distance)
                    || Math.abs(rightDistance) > Math.abs(distance)) {
                hasReachedTarget = true;
                timeWhenTargetReached = System.currentTimeMillis();
            } else {
                if (USE_DRIFTING_COMPENSATION_FACTOR_CONFIG) {
                    SmartDashboard.putBoolean("Using comp factor NOW",
                            USE_DRIFTING_COMPENSATION_FACTOR_CONFIG);
                    // Still need to reach target. Try to compensate for drifting by
                    // applying a heading.
                    double distanceDifference = rightDistance - leftDistance;
                    if (distance > 0) {
                        // We're driving forward
                        // driveBase.overrideHeadingValue(distanceDifference *
                        // DRIFTING_COMPENSATION_FACTOR);
                        ((AnalogInput) Core.getInputManager()
                                .getInput(WSInputs.DRV_HEADING.getName())).setValue(
                                        distanceDifference * DRIFTING_COMPENSATION_FACTOR);
                    } else {
                        // We're driving backwards. Heading compensation is reversed.
                        // driveBase.overrideHeadingValue(distanceDifference *
                        // DRIFTING_COMPENSATION_FACTOR * -1);
                        ((AnalogInput) Core.getInputManager()
                                .getInput(WSInputs.DRV_HEADING.getName())).setValue(
                                        -1 * distanceDifference * DRIFTING_COMPENSATION_FACTOR);
                    }
                }
            }
            // }

            if (hasReachedTarget && shouldHardStop) {
                if (System.currentTimeMillis() < timeWhenTargetReached + MILLIS_TO_REVERSE) {
                    // driveBase.overrideThrottleValue(-speed);
                    ((AnalogInput) Core.getInputManager().getInput(WSInputs.DRV_THROTTLE.getName()))
                            .setValue(-speed);
                } else {
                    // driveBase.disableDriveOverride();
                    setFinished(true);
                }
            } else if (hasReachedTarget) {
                // driveBase.disableDriveOverride();
                setFinished(true);
            }
        }
        // to create a function so robot drives straight
        SmartDashboard.putNumber("Actual Left Speed", driveBase.getLeftEncoderValue());
        SmartDashboard.putNumber("Actual Right Speed", driveBase.getRightEncoderValue());
        SmartDashboard.putNumber("supposed", speed);
    }

    @Override
    public String toString() {
        return "Drive distance at speed";
    }

}
