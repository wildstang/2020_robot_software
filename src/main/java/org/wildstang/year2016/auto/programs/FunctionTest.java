package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.intake.StepIntake;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;

public class FunctionTest extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        // addStep(new StepSetIntakeState(false));
        // addStep(new AutoStepDelay(1000));
        // addStep(new StepIntake(0));

        addStep(new StepIntake(1));
        addStep(new AutoStepDelay(1000));
        addStep(new StepIntake(0));

        addStep(new StepSetShooterPosition(true));
        addStep(new StepResetShooterPositionToggle());
        addStep(new AutoStepDelay(1000));
        addStep(new StepSetShooterPosition(false));
        addStep(new StepResetShooterPositionToggle());

        addStep(new StepStartDriveUsingMotionProfile(25, 0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "FunctionTest";
    }

}
