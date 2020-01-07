package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepDriveDistanceAtSpeed;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.intake.StepIntake;
import org.wildstang.year2016.auto.steps.intake.StepResetIntakeToggle;
import org.wildstang.year2016.auto.steps.intake.StepSetIntakeState;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;

public class OneBallMoatRampartRightLow extends AutoProgram {
    private int defensePosition;
    // protected final double dist2 = 91.78;
    protected final double dist3 = 104;
    protected final double dist4 = 52;
    protected final double dist5 = 0;

    @Override
    protected void defineSteps() {
        // TODO Auto-generated method stub
        AutoParallelStepGroup crossDefense = new AutoParallelStepGroup();
        AutoSerialStepGroup crossSeries = new AutoSerialStepGroup();
        // Drive to defense and cross
        crossSeries.addStep(new StepStartDriveUsingMotionProfile(52.5, 0));
        crossSeries.addStep(new StepWaitForDriveMotionProfile());
        crossSeries.addStep(new StepStopDriveUsingMotionProfile());
        crossSeries.addStep(new StepStartDriveUsingMotionProfile(80.5, 0));
        crossSeries.addStep(new StepWaitForDriveMotionProfile());
        crossSeries.addStep(new StepStopDriveUsingMotionProfile());
        crossDefense.addStep(crossSeries);
        // Wait 1 second before deploying intake
        crossDefense.addStep(new AutoStepDelay(1000));
        crossDefense.addStep(new StepSetIntakeState(true));
        addStep(crossDefense);
        addStep(new StepResetIntakeToggle());

        AutoParallelStepGroup findGoal = new AutoParallelStepGroup();
        AutoSerialStepGroup gotoGoal = new AutoSerialStepGroup();
        // add in 4 different cases for driving waypoints based on defense
        // position
        // findGoal.addStep(new StepRunFlywheel(speed));
        findGoal.addStep(new StepSetShooterPosition(true));
        findGoal.addStep(new StepSetIntakeState(false));

        switch (defensePosition) {
        // set different drive instructions based on the distance of the
        // defense from the center goal, to drive to perpendicular, rotate to
        // face, and shoot

        // case (2):
        // {
        // gotoGoal.addStep(new StepQuickTurn(90 * (dist2 / Math.abs(dist2))));
        // gotoGoal.addStep(new StepDriveDistanceAtSpeed(Math.abs(dist2), 1, true));
        // gotoGoal.addStep(new StepQuickTurn(-90 * (dist2 / Math.abs(dist2))));
        // }
        case (3): {
            gotoGoal.addStep(new StepQuickTurn(90 * (dist3 / Math.abs(dist3))));
            gotoGoal.addStep(new StepStartDriveUsingMotionProfile(Math.abs(dist3), 0));
            gotoGoal.addStep(new StepWaitForDriveMotionProfile());
            gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
            gotoGoal.addStep(new StepQuickTurn(-90 * (dist3 / Math.abs(dist3))));
        }
        case (4): {
            gotoGoal.addStep(new StepQuickTurn(90 * (dist4 / Math.abs(dist4))));
            gotoGoal.addStep(new StepStartDriveUsingMotionProfile(Math.abs(dist4), 0));
            gotoGoal.addStep(new StepWaitForDriveMotionProfile());
            gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
            gotoGoal.addStep(new StepQuickTurn(-90 * (dist4 / Math.abs(dist4))));
        }
        case (5): {
            // gotoGoal.addStep(new StepQuickTurn(90 * (dist5 / Math.abs(dist5))));
            // gotoGoal.addStep(new StepDriveDistanceAtSpeed(Math.abs(dist5), 1, true));
            // gotoGoal.addStep(new StepQuickTurn(-90 * (dist5 / Math.abs(dist5))));
        }
            // gotoGoal.addStep(new StepResetFlywheelToggles());
            gotoGoal.addStep(new StepResetShooterPositionToggle());
            gotoGoal.addStep(new StepResetIntakeToggle());
        }

        findGoal.addStep(gotoGoal);
        addStep(findGoal);

        // Should be positioned in front of the right-most defense now.
        // Drive forward to in line with right goal
        addStep(new StepDriveDistanceAtSpeed(160, 1, true));
        addStep(new StepStartDriveUsingMotionProfile(160, 0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());

        // rotate to face low goal
        addStep(new StepQuickTurn(120));

        // Ram into goal wall
        // addStep(new StepDriveDistanceAtSpeed(-40, .5, true));
        addStep(new StepStartDriveUsingMotionProfile(-40, 0));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());

        // Outtake into low goal, because unsure if enough space to shoot properly
        addStep(new StepIntake(-1));

        // addStep(new StepShoot());
        // addStep(new StepResetShotToggle());

        // addStep(new StepRunFlywheel(0));
        // addStep(new StepResetFlywheelToggles());

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "One Ball D Right Low";
    }

}
