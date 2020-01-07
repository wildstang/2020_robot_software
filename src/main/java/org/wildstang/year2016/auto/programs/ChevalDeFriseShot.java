package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepVisionAdjustment;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.intake.StepSetNoseState;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class ChevalDeFriseShot extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        addStep(new StepStartDriveUsingMotionProfile(80, 0.0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        addStep(new AutoStepDelay(500));
        addStep(new StepSetIntakeState(true));
        addStep(new StepSetNoseState(true));
        addStep(new AutoStepDelay(500));
        addStep(new StepStartDriveUsingMotionProfile(-5, 0.0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        addStep(new AutoStepDelay(500));

        AutoParallelStepGroup crossCheval = new AutoParallelStepGroup();
        AutoSerialStepGroup driveOver = new AutoSerialStepGroup();

        driveOver.addStep(new StepStartDriveUsingMotionProfile(80, 0.0));
        driveOver.addStep(new StepWaitForDriveMotionProfile());
        driveOver.addStep(new StepStopDriveUsingMotionProfile());
        // Medium flywheel speed.
        crossCheval.addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_MEDIUM));
        crossCheval.addStep(driveOver);
        addStep(crossCheval);
        addStep(new AutoStepDelay(500));
        addStep(new StepSetIntakeState(false));
        addStep(new StepSetNoseState(false));
        addStep(new AutoStepDelay(1000));

        addStep(new StepQuickTurn(180));
        addStep(new AutoStepDelay(1000));
        addStep(new StepVisionAdjustment());
        addStep(new AutoStepDelay(500));
        addStep(new StepSetShooterPosition(true));
        addStep(new StepResetShooterPositionToggle());
        addStep(new AutoStepDelay(2000));
        addStep(new StepShoot());
        addStep(new AutoStepDelay(1000));
        addStep(new StepResetShotToggle());
        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));

        // total delay time: 7.5 seconds
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Cheval De Frise";
    }

}
