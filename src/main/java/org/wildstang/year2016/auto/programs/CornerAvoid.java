package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class CornerAvoid extends AutoProgram {

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        AutoParallelStepGroup beginAuto = new AutoParallelStepGroup("Start shooter and go to goal");
        AutoSerialStepGroup gotoGoal = new AutoSerialStepGroup("Go to goal");
        // Start shooter to get to speed
        // Medium Flywheel speed.
        beginAuto.addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_MEDIUM));
        beginAuto.addStep(new StepSetShooterPosition(true));

        // Drive to point in line with goal
        gotoGoal.addStep(new StepStartDriveUsingMotionProfile(-40, 0));
        gotoGoal.addStep(new StepWaitForDriveMotionProfile());
        gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
        // reset functions for tasks in the parallel group go after first serial step to
        // ensure that the commands
        // do not overlap. As long as they are reset before the next call to the
        // function it should be fine.
        gotoGoal.addStep(new StepResetShooterPositionToggle());
        // Turn to face goal
        gotoGoal.addStep(new StepQuickTurn(-63));
        beginAuto.addStep(gotoGoal);
        addStep(beginAuto);
        addStep(new StepResetShooterPositionToggle());
        // Shoot
        addStep(new StepShoot());
        addStep(new AutoStepDelay(1000));
        addStep(new StepResetShotToggle());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Shot w/starting angle";
    }

}
