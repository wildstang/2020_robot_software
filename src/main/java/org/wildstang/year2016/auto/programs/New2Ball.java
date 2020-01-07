package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.intake.StepIntake;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class New2Ball extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        AutoParallelStepGroup setup = new AutoParallelStepGroup();
        setup.addStep(new StepSetShooterPosition(true));
        setup.addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_MEDIUM));
        addStep(setup);
        addStep(new AutoStepDelay(250));
        addStep(new StepSetIntakeState(true));
        addStep(new StepShoot());
        addStep(new AutoStepDelay(350));
        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));
        AutoParallelStepGroup cross = new AutoParallelStepGroup();
        AutoSerialStepGroup manageIntake = new AutoSerialStepGroup();
        new AutoSerialStepGroup();
        manageIntake.addStep(new StepResetShotToggle());
        manageIntake.addStep(new StepIntake(1));
        manageIntake.addStep(new AutoStepDelay(1000));
        manageIntake.addStep(new StepIntake(0));
        // crossLow.addStep(new Step);
        cross.addStep(manageIntake);

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "2 Ball";
    }

}
