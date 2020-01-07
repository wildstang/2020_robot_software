package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.year2016.auto.steps.drivebase.StepDriveDistanceAtSpeed;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.intake.StepIntake;
import org.wildstang.year2016.auto.steps.intake.StepResetIntakeToggle;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.shooter.StepResetFlywheelToggles;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class HarpoonAuto extends AutoProgram {
    private int speed;

    @Override
    protected void defineSteps() {
        // Shove balls out of the way on our crusade to the low bar
        AutoParallelStepGroup driveToEnd = new AutoParallelStepGroup();
        AutoSerialStepGroup deployAndDrive = new AutoSerialStepGroup();
        deployAndDrive.addStep(new StepSetIntakeState(true));
        deployAndDrive.addStep(new StepResetIntakeToggle());
        deployAndDrive.addStep(new StepStartDriveUsingMotionProfile(276, 0));
        deployAndDrive.addStep(new StepWaitForDriveMotionProfile());
        deployAndDrive.addStep(new StepStopDriveUsingMotionProfile());
        driveToEnd.addStep(new StepIntake(-1));
        driveToEnd.addStep(deployAndDrive);
        addStep(driveToEnd);

        // Retract intake to prevent crossing midline during turn, then drive
        // forward and extend intake for low bar crossing
        AutoSerialStepGroup crossTheRoad = new AutoSerialStepGroup();
        crossTheRoad.addStep(new StepIntake(0));
        crossTheRoad.addStep(new StepSetIntakeState(false));
        crossTheRoad.addStep(new StepResetIntakeToggle());
        crossTheRoad.addStep(new StepQuickTurn(90));
        crossTheRoad.addStep(new StepDriveDistanceAtSpeed(32, 1, false));
        crossTheRoad.addStep(new StepSetIntakeState(true));
        crossTheRoad.addStep(new StepResetIntakeToggle());
        crossTheRoad.addStep(new StepDriveDistanceAtSpeed(80.5, 1, true));
        addStep(crossTheRoad);

        // Start running flywheels while going to side goal shooting position
        AutoParallelStepGroup Score = new AutoParallelStepGroup();
        AutoSerialStepGroup gotoGoal = new AutoSerialStepGroup();
        Score.addStep(new StepRunFlywheel(speed));
        // Score.addStep(new StepResetFlywheelToggles());
        gotoGoal.addStep(new StepDriveDistanceAtSpeed(108, 1, true));
        gotoGoal.addStep(new StepResetFlywheelToggles());
        gotoGoal.addStep(new StepQuickTurn(60));
        Score.addStep(gotoGoal);
        addStep(Score);

        addStep(new StepShoot());
        addStep(new StepResetShotToggle());

        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));
        addStep(new StepResetFlywheelToggles());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Neutralizing 2 balls";
    }

}
