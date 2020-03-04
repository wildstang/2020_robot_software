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

public class Straight8 extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new IntakeOnStep(true));
        addStep(new SetTurretStep(-31000));
        addStep(new DelayStep(0.5));
        addStep(new PathFollowerStep(PathNameConstants.STRAIGHT8A, true, true));
        addStep(new AutoAimStep(true));
        addStep(new DelayStep(3));
        addStep(new FeedOnStep());
        addStep(new DelayStep(2));
        addStep(new FeedOffStep());
        addStep(new AutoAimStep(false));
        addStep(new PathFollowerStep(PathNameConstants.STRAIGHT8B, true, true));
        addStep(new IntakeOnStep(false));
        addStep(new PathFollowerStep(PathNameConstants.STRAIGHT8C, true, false));
        addStep(new AutoAimStep(true));
        addStep(new IntakeOnStep(true));
        addStep(new DelayStep(0.5));
        addStep(new FeedOnStep());

    }

    @Override
    public String toString() {
        //give it a name
        return "Straight8";
    }

}