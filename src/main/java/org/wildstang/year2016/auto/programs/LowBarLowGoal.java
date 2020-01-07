package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.intake.StepIntake;
import org.wildstang.year2016.auto.steps.intake.StepResetIntakeToggle;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;

public class LowBarLowGoal extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        addStep(new StepSetShooterPosition(false));
        addStep(new StepSetIntakeState(true));
        addStep(new StepStartDriveUsingMotionProfile(-180, 0.0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        addStep(new StepResetShooterPositionToggle());
        addStep(new StepResetIntakeToggle());
        addStep(new StepSetIntakeState(false));
        addStep(new StepQuickTurn(-120));
        addStep(new StepStartDriveUsingMotionProfile(100, 0.0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        addStep(new StepIntake(-1));
        addStep(new AutoStepDelay(2000));

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Low Bar Low Goal";
    }

}
