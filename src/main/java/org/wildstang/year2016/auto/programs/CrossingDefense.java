package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;

public class CrossingDefense extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        // addStep(new StepSetIntakeState(false));
        // addStep(new AutoStepDelay(1000));
        // addStep(new StepIntake(0));

        addStep(new StepStartDriveUsingMotionProfile(120, 0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Cross";
    }

}
