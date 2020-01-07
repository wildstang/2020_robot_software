package org.wildstang.year2017.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2017.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2017.auto.steps.ShootStep;
import org.wildstang.year2017.auto.steps.ShooterOnAndReady;
import org.wildstang.year2017.auto.steps.SideGearStepGroup;
import org.wildstang.year2017.auto.steps.StopShooting;
import org.wildstang.year2017.auto.steps.TurnByNDegreesStep;

public class GearPlus10StraightLeft extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new SideGearStepGroup(60, 73));

        addStep(new MotionMagicStraightLine(-24));

        addStep(new TurnByNDegreesStep(140, .6));
        addStep(new AutoStepDelay(200));

        AutoParallelStepGroup prepShootAndDrive = new AutoParallelStepGroup();
        prepShootAndDrive.addStep(new MotionMagicStraightLine(96));
        prepShootAndDrive.addStep(new ShooterOnAndReady());

        addStep(prepShootAndDrive);
        addStep(new ShootStep());
        addStep(new AutoStepDelay(10000));

        addStep(new StopShooting());
    }

    @Override
    public String toString() {
        return "Gear plus 10 straight";
    }

}
