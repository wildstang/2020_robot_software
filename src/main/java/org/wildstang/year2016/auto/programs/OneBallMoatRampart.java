package org.wildstang.year2016.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepTurnForTime;
import org.wildstang.year2016.auto.steps.drivebase.StepVisionAdjustment;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;
import org.wildstang.year2016.auto.steps.shooter.StepResetFlywheelToggles;
import org.wildstang.year2016.auto.steps.shooter.StepResetShooterPositionToggle;
import org.wildstang.year2016.auto.steps.shooter.StepResetShotToggle;
import org.wildstang.year2016.auto.steps.shooter.StepRunFlywheel;
import org.wildstang.year2016.auto.steps.shooter.StepSetShooterPosition;
import org.wildstang.year2016.auto.steps.shooter.StepShoot;
import org.wildstang.year2016.subsystems.Shooter;

public class OneBallMoatRampart extends AutoProgram {
    private int speed = Shooter.FLYWHEEL_SPEED_MEDIUM;
    private int defensePosition = 2;
    protected final double dist2 = 91.78;
    protected final double dist3 = 38.59;
    // protected final double dist4 = -15.12;
    protected final double dist4 = 0;
    protected final double dist5 = -67.27;

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
        // crossDefense.addStep(new StepSetIntakeState(true));
        addStep(crossDefense);
        // addStep(new StepResetIntakeToggle());

        AutoParallelStepGroup findGoal = new AutoParallelStepGroup();
        AutoSerialStepGroup gotoGoal = new AutoSerialStepGroup();
        // add in 4 different cases for driving waypoints based on defense
        // position
        findGoal.addStep(new StepRunFlywheel(speed));
        findGoal.addStep(new StepSetShooterPosition(true));
        // findGoal.addStep(new StepSetIntakeState(false));

        switch (defensePosition) {
        // set different drive instructions based on the distance of the
        // defense from the center goal, to drive to perpendicular, rotate to
        // face, and shoot

        case (2): {
            // gotoGoal.addStep(new StepQuickTurn(90 * (dist2 / Math.abs(dist2))));
            // gotoGoal.addStep(new StepStartDriveUsingMotionProfile(Math.abs(dist2), 0));
            // gotoGoal.addStep(new StepWaitForDriveMotionProfile());
            // gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
            // gotoGoal.addStep(new StepQuickTurn(-90 * (dist2 / Math.abs(dist2))));
            gotoGoal.addStep(new StepTurnForTime(.5, .5));
        }
        case (3): {
            gotoGoal.addStep(new StepQuickTurn(90 * (dist3 / Math.abs(dist3))));
            gotoGoal.addStep(new StepStartDriveUsingMotionProfile(Math.abs(dist3), 0));
            gotoGoal.addStep(new StepWaitForDriveMotionProfile());
            gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
            gotoGoal.addStep(new StepQuickTurn(-90 * (dist3 / Math.abs(dist3))));
        }
        case (4): {
            if (dist4 == 0) {

            } else {
                gotoGoal.addStep(new StepQuickTurn(90 * (dist4 / Math.abs(dist4))));
                gotoGoal.addStep(new StepStartDriveUsingMotionProfile(Math.abs(dist4), 0));
                gotoGoal.addStep(new StepWaitForDriveMotionProfile());
                gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
                gotoGoal.addStep(new StepQuickTurn(-90 * (dist4 / Math.abs(dist4))));
            }
        }
        case (5): {
            gotoGoal.addStep(new StepQuickTurn(90 * (dist5 / Math.abs(dist5))));
            gotoGoal.addStep(new StepStartDriveUsingMotionProfile(Math.abs(dist5), 0));
            gotoGoal.addStep(new StepWaitForDriveMotionProfile());
            gotoGoal.addStep(new StepStopDriveUsingMotionProfile());
            gotoGoal.addStep(new StepQuickTurn(-90 * (dist5 / Math.abs(dist5))));
        }
            gotoGoal.addStep(new StepResetFlywheelToggles());
            gotoGoal.addStep(new StepResetShooterPositionToggle());
            // gotoGoal.addStep(new StepResetIntakeToggle());
        }
        findGoal.addStep(gotoGoal);
        addStep(findGoal);

        addStep(new AutoStepDelay(500));
        addStep(new StepVisionAdjustment());
        addStep(new AutoStepDelay(500));

        addStep(new StepShoot());
        addStep(new AutoStepDelay(2000));
        addStep(new StepResetShotToggle());

        addStep(new StepRunFlywheel(Shooter.FLYWHEEL_SPEED_ZERO));
        addStep(new StepResetFlywheelToggles());

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "One Ball Moat or Rampart";
    }

}
