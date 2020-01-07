package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepResetEncoders;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.intake.StepResetIntakeToggle;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class SpyShotCross extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        // wait for flywheel to get to speed, then shoot
        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_MEDIUM));
        addStep(new StepSetShooterPosition(true));
        addStep(new StepResetShooterPositionToggle());
        addStep(new AutoStepDelay(2000));
        addStep(new StepShoot());
        addStep(new AutoStepDelay(1000));
        addStep(new StepResetShotToggle());
        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));

        // turn, then cross low bar both ways
        addStep(new StepQuickTurn(85));
        AutoParallelStepGroup lowBarGo = new AutoParallelStepGroup();
        AutoSerialStepGroup driveSteps = new AutoSerialStepGroup();
        lowBarGo.addStep(new StepSetShooterPosition(false));
        lowBarGo.addStep(new StepSetIntakeState(true));
        driveSteps.addStep(new StepStartDriveUsingMotionProfile(-220, 0));
        driveSteps.addStep(new StepWaitForDriveMotionProfile());
        driveSteps.addStep(new StepStopDriveUsingMotionProfile());
        lowBarGo.addStep(driveSteps);
        addStep(lowBarGo);
        addStep(new StepResetShooterPositionToggle());
        addStep(new StepResetIntakeToggle());
        addStep(new StepResetEncoders());
        addStep(new StepResetEncoders());
        addStep(new AutoStepDelay(1000));
        addStep(new StepStartDriveUsingMotionProfile(140, 0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Courtyard with cross";
    }

}
