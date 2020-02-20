package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.programs.PathNameConstants;

public class Prog144 extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new PathFollowerStep(PathNameConstants.TEST144, true, true));

    }

    @Override
    public String toString() {
        //give it a name
        return "Test 144";
    }

}