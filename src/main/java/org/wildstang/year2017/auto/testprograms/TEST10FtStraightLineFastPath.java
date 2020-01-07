package org.wildstang.year2017.auto.testprograms;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2017.auto.programs.PathNameConstants;
import org.wildstang.year2017.auto.steps.PathFollowerStep;

public class TEST10FtStraightLineFastPath extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new PathFollowerStep(PathNameConstants.STRAIGHT_LINE_10_FT_FAST_TEST));
    }

    @Override
    public String toString() {
        return "10ft straight line Fast";
    }

}
