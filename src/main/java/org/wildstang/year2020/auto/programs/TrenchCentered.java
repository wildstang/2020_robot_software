package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.steps.DelayStep;
import org.wildstang.year2020.auto.programs.PathNameConstants;

public class TrenchCentered extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new PathFollowerStep(PathNameConstants.TRENCH_CENTERED,true,true));
        addStep(new DelayStep(2));
        addStep(new PathFollowerStep(PathNameConstants.TRENCH_CENTERED,true,false));

    }

    @Override
    public String toString() {
        //give it a name
        return "TrenchCentered";
    }

}