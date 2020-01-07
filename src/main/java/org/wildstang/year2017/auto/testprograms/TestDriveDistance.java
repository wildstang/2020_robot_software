package org.wildstang.year2017.auto.testprograms;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2017.auto.steps.DriveDistanceStraightStep;

public class TestDriveDistance extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new DriveDistanceStraightStep(0.5, 24));
        addStep(new AutoStepDelay(2000));
        addStep(new DriveDistanceStraightStep(0.5, 36));
    }

    @Override
    public String toString() {
        return "Test Drive Distance";
    }

}
