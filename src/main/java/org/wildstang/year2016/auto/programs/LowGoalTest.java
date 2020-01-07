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
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;

public class LowGoalTest extends AutoProgram {
    private int speed;

    @Override
    protected void defineSteps() {
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
        gotoGoalTwo.addStep(new StepQuickTurn(-120));
        shootTwo.addStep(gotoGoalTwo);
        addStep(shootTwo);

        gotoGoalTwo.addStep(new StepStartDriveUsingMotionProfile(-100, 0));
        gotoGoalTwo.addStep(new StepWaitForDriveMotionProfile());
        gotoGoalTwo.addStep(new StepStopDriveUsingMotionProfile());

        // Outtake
        addStep(new StepIntake(-1));
        addStep(new AutoStepDelay(2000));
        addStep(new StepIntake(0));
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Two Ball Auto";
    }

}
