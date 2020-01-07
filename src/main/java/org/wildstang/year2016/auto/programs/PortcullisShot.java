package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepDriveDistanceAtSpeed;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepVisionAdjustment;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.intake.StepSetNoseState;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class PortcullisShot extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        addStep(new StepSetIntakeState(true));
        addStep(new StepSetNoseState(true));

        AutoParallelStepGroup flywheelCross = new AutoParallelStepGroup();
        flywheelCross.addStep(new StepDriveDistanceAtSpeed(150, .5, true));
        flywheelCross.addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_MEDIUM));
        addStep(new AutoStepDelay(500));
        addStep(new StepQuickTurn(180));
        addStep(new StepSetShooterPosition(true));
        addStep(new StepSetIntakeState(false));
        addStep(new StepSetNoseState(false));
        addStep(new AutoStepDelay(500));
        addStep(new StepVisionAdjustment());
        addStep(new AutoStepDelay(1000));
        addStep(new StepShoot());
        addStep(new AutoStepDelay(2000));
        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Portcullis shot";
    }

}
