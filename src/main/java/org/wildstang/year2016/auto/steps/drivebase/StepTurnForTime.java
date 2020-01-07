package org.wildstang.year2016.auto.steps.drivebase;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.DriveBase;

/**
 *
 * @author Joey
 */
public class StepTurnForTime extends AutoStep {

    private double speed, time;
    private long startTime;
    private boolean shouldFinish = false;

    public StepTurnForTime(double speed, double time) {
        this.speed = speed;
        this.time = time;
    }

    @Override
    public void initialize() {
        startTime = System.currentTimeMillis();
        ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                .setThrottleValue(0);
        // ((DriveBase)
        // Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName())).setLeftDrive(value
        // < 0 ? 0.6
        // : -0.6);
        ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                .setLeftDrive(speed < 0 ? 0.4 : -0.4);
        // TODO
        // ((AnalogInput)Core.getInputManager().getInput(WSInputs.DRV_THROTTLE.getName())).setValue(0.0);
        // ((AnalogInput)Core.getInputManager().getInput(WSInputs.DRV_HEADING.getName())).setValue(value
        // < 0 ? 0.6 : -0.6);
        ((AnalogInput) Core.getInputManager().getInput(WSInputs.DRV_THROTTLE.getName()))
                .setValue(0.0);
        ((AnalogInput) Core.getInputManager().getInput(WSInputs.DRV_HEADING.getName()))
                .setValue(speed < 0 ? 0.4 : -0.4);

    }

    @Override
    public void update() {
        if (shouldFinish) {
            setFinished(true);
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .setLeftDrive(0.0);
            return;
        }

        ((AnalogInput) Core.getInputManager().getInput(WSInputs.DRV_THROTTLE.getName()))
                .setValue(0.0);
        // ((AnalogInput)Core.getInputManager().getInput(WSInputs.DRV_HEADING.getName())).setValue(value
        // < 0 ? 0.6 : -0.6);
        ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                .setLeftDrive(speed);
        if (System.currentTimeMillis() > (startTime + time)) {
            shouldFinish = true;
        }
    }

    @Override
    public String toString() {
        return "Turning using time";
    }
}
