package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.intake.StepIntake;
import org.wildstang.year2016.auto.steps.intake.StepResetIntakeToggle;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.shooter.StepResetFlywheelToggles;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class TwoBall extends AutoProgram {
    private int speed;

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        AutoParallelStepGroup beginAuto = new AutoParallelStepGroup("Start shooter and go to goal");
        AutoSerialStepGroup gotoGoal = new AutoSerialStepGroup("Go to goal");
        // Start shooter to get to speed
        beginAuto.addStep(new StepRunFlywheel(speed));
        // beginAuto.addStep(new StepResetFlywheelToggles());
        beginAuto.addStep(new StepSetShooterPosition(true));
        // beginAuto.addStep(new StepResetShooterPositionToggle());
        // Drive to point in line with goal
        // gotoGoal.addStep(new StepDriveDistanceAtSpeed(-71.25, 1, true));
        gotoGoal.addStep(new StepStartDriveUsingMotionProfile(-71.25, 0));
        gotoGoal.addStep(new StepWaitForDriveMotionProfile());
        gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
        // reset functions for tasks in the parallel group go after first serial step to
        // ensure that the commands
        // do not overlap. As long as they are reset before the next call to the
        // function it should be fine.
        gotoGoal.addStep(new StepSetShooterPosition(true));
        gotoGoal.addStep(new StepResetShooterPositionToggle());
        // Turn to face goal
        gotoGoal.addStep(new StepQuickTurn(90));
        beginAuto.addStep(gotoGoal);
        addStep(beginAuto);

        // Shoot
        addStep(new StepShoot());
        addStep(new StepResetShotToggle());

        AutoParallelStepGroup leaveCourtyard = new AutoParallelStepGroup(
                "Stop flywheel and go to low bar");
        AutoSerialStepGroup gotoLowBar = new AutoSerialStepGroup("Go to low bar");
        // Stop flywheel
        leaveCourtyard.addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));
        // leaveCourtyard.addStep(new StepResetFlywheelToggles());
        leaveCourtyard.addStep(new StepSetShooterPosition(false));
        // leaveCourtyard.addStep(new StepResetShooterPositionToggle());
        // Drive to point in line with low bar
        // gotoLowBar.addStep(new StepDriveDistanceAtSpeed(-19, 1, true));
        gotoLowBar.addStep(new StepStartDriveUsingMotionProfile(-19, 0));
        gotoLowBar.addStep(new StepWaitForDriveMotionProfile());
        gotoLowBar.addStep(new StepStopDriveUsingMotionProfile());
        gotoLowBar.addStep(new StepResetFlywheelToggles());
        gotoLowBar.addStep(new StepResetShooterPositionToggle());
        // Turn to face low bar
        gotoLowBar.addStep(new StepQuickTurn(-60));
        // lower intake
        gotoLowBar.addStep(new StepSetIntakeState(true));
        gotoLowBar.addStep(new StepResetIntakeToggle());
        // Drive to low bar
        // gotoLowBar.addStep(new StepDriveDistanceAtSpeed(-108, 1, false));
        gotoLowBar.addStep(new StepStartDriveUsingMotionProfile(-108, 0));
        gotoLowBar.addStep(new StepWaitForDriveMotionProfile());
        gotoLowBar.addStep(new StepStopDriveUsingMotionProfile());
        leaveCourtyard.addStep(gotoLowBar);
        addStep(leaveCourtyard);

        // Cross low bar
        // addStep(new StepDriveDistanceAtSpeed(-80.5, 1, true));
        addStep(new StepStartDriveUsingMotionProfile(-80.5, 0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        // Turn to face ball
        addStep(new StepQuickTurn(-21));

        AutoParallelStepGroup grabBall = new AutoParallelStepGroup(
                "Run intake and go to/return from ball");
        AutoSerialStepGroup gotoBall = new AutoSerialStepGroup("Go to ball");
        // Intake ball
        grabBall.addStep(new StepIntake(1));
        // Drive to ball
        // gotoBall.addStep(new StepDriveDistanceAtSpeed(-56, 1, true));
        gotoBall.addStep(new StepStartDriveUsingMotionProfile(-56, 0));
        gotoBall.addStep(new StepWaitForDriveMotionProfile());
        gotoBall.addStep(new StepStopDriveUsingMotionProfile());
        // Wait while ball is collected
        gotoBall.addStep(new AutoStepDelay(1000));
        grabBall.addStep(gotoBall);
        addStep(grabBall);

        // Stop intake
        addStep(new StepIntake(0));
        // Drive to low bar
        // addStep(new StepDriveDistanceAtSpeed(56, 1, true));
        addStep(new StepStartDriveUsingMotionProfile(56, 0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        // Turn to face low bar
        addStep(new StepQuickTurn(21));

        AutoParallelStepGroup shootTwo = new AutoParallelStepGroup(
                "Return to shooting position and shoot");
        AutoSerialStepGroup gotoGoalTwo = new AutoSerialStepGroup("Go to goal again");
        // Start shooter to get to speed
        shootTwo.addStep(new StepRunFlywheel(speed));
        // shootTwo.addStep(new StepResetFlywheelToggles());
        // Cross low bar
        // gotoGoalTwo.addStep(new StepDriveDistanceAtSpeed(80.5, 1, true));
        gotoGoalTwo.addStep(new StepStartDriveUsingMotionProfile(80.5, 0));
        gotoGoalTwo.addStep(new StepWaitForDriveMotionProfile());
        gotoGoalTwo.addStep(new StepStopDriveUsingMotionProfile());
        gotoGoalTwo.addStep(new StepResetFlywheelToggles());
        // Retract intake
        gotoGoalTwo.addStep(new StepSetIntakeState(false));
        gotoGoalTwo.addStep(new StepResetIntakeToggle());
        // Extend shooter
        gotoGoalTwo.addStep(new StepSetShooterPosition(true));
        gotoGoalTwo.addStep(new StepResetShooterPositionToggle());
        // Drive to point in line with goal
        // gotoGoalTwo.addStep(new StepDriveDistanceAtSpeed(108, 1, true));
        gotoGoalTwo.addStep(new StepStartDriveUsingMotionProfile(108, 0));
        gotoGoalTwo.addStep(new StepWaitForDriveMotionProfile());
        gotoGoalTwo.addStep(new StepStopDriveUsingMotionProfile());
        // Turn to face goal
        gotoGoalTwo.addStep(new StepQuickTurn(60));
        shootTwo.addStep(gotoGoalTwo);
        addStep(shootTwo);

        // Shoot
        addStep(new StepShoot());
        addStep(new StepResetShotToggle());

        addStep(new StepRunFlywheel(0));
        addStep(new StepResetFlywheelToggles());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Two Ball Auto";
    }

}
