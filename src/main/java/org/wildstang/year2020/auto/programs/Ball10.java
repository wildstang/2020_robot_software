package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.year2017.auto.steps.FeedOffStep;
import org.wildstang.year2017.auto.steps.FeedOnStep;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.steps.DelayStep;
import org.wildstang.year2020.auto.steps.IntakeOnStep;
import org.wildstang.year2020.auto.programs.PathNameConstants;

public class Ball10 extends AutoProgram {

    @Override
    protected void defineSteps() {
        AutoParallelStepGroup initial = new AutoParallelStepGroup();
        initial.addStep(new IntakeOnStep());
        initial.addStep(new PathFollowerStep(PathNameConstants.BALL10A,true,true));
        //turret step
        //hood step
        addStep(initial);

        //make this step aim when it's nearly done
        addStep(new PathFollowerStep(PathNameConstants.BALL10B,true,false));

        addStep(new FeedOnStep());
        addStep(new DelayStep(2));
        addStep(new FeedOffStep());

        //make this step aim when it's nearly done
        addStep(new PathFollowerStep(PathNameConstants.BALL10C,true,true));

        addStep(new FeedOnStep());
    }

    @Override
    public String toString() {
        //give it a name
        return "Ball10";
    }

}