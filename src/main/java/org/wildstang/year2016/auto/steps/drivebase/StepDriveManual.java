/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;

/**
 *
 * @author coder65535
 */
public class StepDriveManual extends AutoStep {

    private double throttle, heading;
    public static final double KEEP_PREVIOUS_STATE = 2.0;

    public StepDriveManual(double throttle, double heading) {
        this.throttle = throttle;
        this.heading = heading;
    }

    @Override
    public void initialize() {
        // ((DriveBase)
        // Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName())).overrideThrottleValue(throttle);
        // ((DriveBase)
        // Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName())).overrideHeadingValue(heading);
        setFinished(true);
    }

    @Override
    public void update() {
    }

    @Override
    public String toString() {
        return "Set throttle to " + throttle + " and heading to " + heading;
    }
}
