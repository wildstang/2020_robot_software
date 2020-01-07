package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepDriveDistanceAtSpeed;

public class DriveAtp2 extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new StepDriveDistanceAtSpeed(120, .2, true));
        addStep(new AutoStepDelay(1000));
        addStep(new StepDriveDistanceAtSpeed(120, -.2, true));
    }

    @Override
    public String toString() {
        return "Test Driving at .2 speed";
    }

}
