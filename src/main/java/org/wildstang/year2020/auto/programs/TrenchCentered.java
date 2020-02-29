package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.steps.SetTurretStep;
import org.wildstang.year2020.auto.steps.AutoAimStep;
import org.wildstang.year2020.auto.steps.DelayStep;
import org.wildstang.year2020.auto.steps.FeedOffStep;
import org.wildstang.year2020.auto.steps.FeedOnStep;
import org.wildstang.year2020.auto.steps.IntakeOnStep;
import org.wildstang.year2020.auto.programs.PathNameConstants;

public class TrenchCentered extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new IntakeOnStep());
        //addStep(new SetTurretStep(-9800));
        addStep(new DelayStep(1));
        //addStep(new AutoAimStep(true));
        addStep(new DelayStep(3));
        //addStep(new FeedOnStep());
        addStep(new DelayStep(2));
        //addStep(new FeedOffStep());
        //addStep(new AutoAimStep(false));
        addStep(new PathFollowerStep(PathNameConstants.TRENCH_CENTERED, true, true));
        addStep(new PathFollowerStep(PathNameConstants.TRENCH10C, true, false));
        //addStep(new AutoAimStep(true));
        //addStep(new FeedOnStep());

    }

    @Override
    public String toString() {
        //give it a name
        return "TrenchCentered";
    }

}