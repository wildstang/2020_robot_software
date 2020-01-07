package org.wildstang.year2017.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2017.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2017.auto.steps.PathFollowerStep;
import org.wildstang.year2017.auto.steps.ShootStep;
import org.wildstang.year2017.auto.steps.ShooterOnAndReady;
import org.wildstang.year2017.auto.steps.SideGearStepGroup;
import org.wildstang.year2017.auto.steps.StopShooting;
import org.wildstang.year2017.auto.steps.TrackVisionToGearStep;
import org.wildstang.year2017.auto.steps.TurnByNDegreesStepMagic;

public class GearPlus10StraightRight extends AutoProgram {

    @Override
    protected void defineSteps() {
        // Drop off Gear
        addStep(new SideGearStepGroup(-60, 76));

        addStep(new MotionMagicStraightLine(-24));
        addStep(new AutoStepDelay(200));
        addStep(new TurnByNDegreesStepMagic(-120));
        addStep(new AutoStepDelay(200));
        addStep(new MotionMagicStraightLine(50));

        addStep(new TurnByNDegreesStepMagic(-45));
        addStep(new AutoStepDelay(200));
        addStep(new MotionMagicStraightLine(24));

        addStep(new ShooterOnAndReady());
        addStep(new ShootStep());
        addStep(new AutoStepDelay(15000));
        addStep(new StopShooting());
    }

    @Override
    public String toString() {
        return "Gear plus 10 Right";
    }

}
