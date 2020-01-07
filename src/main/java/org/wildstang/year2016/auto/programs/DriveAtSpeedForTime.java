package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.auto.steps.drivebase.StepDriveDistanceAtSpeed;
import org.wildstang.year2016.auto.steps.drivebase.StepSetShifter;

public class DriveAtSpeedForTime extends AutoProgram {
    protected final int DISTANCE = Core.getConfigManager().getConfig()
            .getInt(this.getClass().getName() + ".Distance", 40);
    protected final double SPEED = Core.getConfigManager().getConfig()
            .getDouble(this.getClass().getName() + ".Speed", .5);

    @Override
    protected void defineSteps() {
        // Shift into high gear
        addStep(new StepSetShifter(true));
        addStep(new StepDriveDistanceAtSpeed(DISTANCE, SPEED, false));
    }

    @Override
    public String toString() {
        return "Drive Distance at Speed";
    }
}