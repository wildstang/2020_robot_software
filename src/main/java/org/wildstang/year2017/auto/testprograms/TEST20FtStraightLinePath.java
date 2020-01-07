package org.wildstang.year2017.auto.testprograms;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2017.auto.programs.PathNameConstants;
import org.wildstang.year2017.auto.steps.PathFollowerStep;

public class TEST20FtStraightLinePath extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new PathFollowerStep(PathNameConstants.STRAIGHT_LINE_20_FT_TEST));
    }

    @Override
    public String toString() {
        return "TEST - 20ft straight line";
    }

}
