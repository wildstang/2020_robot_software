package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepVisionAdjustment;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;

public class VisionTest extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new StepSetShooterPosition(true));
        addStep(new StepResetShooterPositionToggle());
        addStep(new AutoStepDelay(1000));

        addStep(new StepVisionAdjustment());

        addStep(new AutoStepDelay(1000));
        addStep(new StepSetShooterPosition(false));
        addStep(new StepResetShooterPositionToggle());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Test Vision Alignment";
    }

}
