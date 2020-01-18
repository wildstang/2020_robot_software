package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2020.auto.programs.PathNameConstants;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.steps.DelayStep;


public class Ball10 extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new PathFollowerStep(PathNameConstants.BALL10A, true, true));
        addStep(new PathFollowerStep(PathNameConstants.BALL10B, true, true));
        addStep(new DelayStep(2));
        addStep(new PathFollowerStep(PathNameConstants.BALL10C, true, true));
    }

    @Override
    public String toString() {
        //give it a name
        return "Ball10";
    }

}