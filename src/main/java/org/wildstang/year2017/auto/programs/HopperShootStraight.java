package org.wildstang.year2017.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2017.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2017.auto.steps.PathFollowerStep;
import org.wildstang.year2017.auto.steps.SetBrakeModeStep;
import org.wildstang.year2017.auto.steps.SetHighGearStep;
import org.wildstang.year2017.auto.steps.ShootStep;
import org.wildstang.year2017.auto.steps.ShooterOnAndReady;
import org.wildstang.year2017.auto.steps.StopShooting;
import org.wildstang.year2017.auto.steps.TurnByNDegreesStepMagic;
import org.wildstang.year2017.subsystems.drive.DriveConstants;

public class HopperShootStraight extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new SetHighGearStep(true));
        addStep(new SetBrakeModeStep(true));

        // Drive out to hopper
        addStep(new MotionMagicStraightLine(96)); // Need to tune distances
        addStep(new AutoStepDelay(200));

        // Turn towards hopper
        addStep(new TurnByNDegreesStepMagic(-90));
        addStep(new AutoStepDelay(200));

        // Drive into hopper and wait
        addStep(new MotionMagicStraightLine(24));
        addStep(new AutoStepDelay(3000));

        // Backup to turn
        addStep(new MotionMagicStraightLine(-12));
        addStep(new AutoStepDelay(200));

        // Turn towards boiler
        addStep(new TurnByNDegreesStepMagic(-90));
        addStep(new AutoStepDelay(200));

        // Drive towards boiler wall
        addStep(new MotionMagicStraightLine(78));
        addStep(new AutoStepDelay(200));

        // Turn towards boiler and drive forward
        addStep(new TurnByNDegreesStepMagic(45));

        addStep(new ShooterOnAndReady());
        // This delay should b unnecessary
        // addStep(new AutoStepDelay(500));
        addStep(new MotionMagicStraightLine(12));

        // Shoot
        addStep(new ShootStep());
        addStep(new AutoStepDelay(15000));
        addStep(new StopShooting());
    }

    @Override
    public String toString() {
        return "Hopper Shoot Straight Paths";
    }

}
