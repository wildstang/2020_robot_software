package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class CornerShot extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        // wait for flywheel to get to speed, then shoot
        // Medium flywheel speed.
        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_LOW));
        addStep(new StepSetShooterPosition(true));
        addStep(new StepResetShooterPositionToggle());
        addStep(new AutoStepDelay(2000));
        addStep(new StepShoot());
        addStep(new AutoStepDelay(1000));
        addStep(new StepResetShotToggle());
        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Corner Shot";
    }

}
