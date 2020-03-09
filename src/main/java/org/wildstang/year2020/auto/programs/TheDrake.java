package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2020.auto.steps.FeedOffStep;
import org.wildstang.year2020.auto.steps.FeedOnStep;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.steps.SetTurretStep;
import org.wildstang.year2020.auto.steps.AutoAimStep;
import org.wildstang.year2020.auto.steps.DelayStep;
import org.wildstang.year2020.auto.steps.IntakeOnStep;
import org.wildstang.year2020.auto.programs.PathNameConstants;

public class TheDrake extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new IntakeOnStep(true));
        addStep(new SetTurretStep(-27000));
        addStep(new DelayStep(3));
        addStep(new AutoAimStep(true));
        addStep(new DelayStep(2));
        addStep(new FeedOnStep());
        addStep(new DelayStep(2.5));
        addStep(new FeedOffStep());
        //addStep(new AutoAimStep(false));
        addStep(new PathFollowerStep(PathNameConstants.TEST120, true, true));
        addStep(new PathFollowerStep(PathNameConstants.TEST120, true, false));
        addStep(new FeedOnStep());

    }

    @Override
    public String toString() {
        //give it a name
        return "Shoot 3 and back up";
    }

}