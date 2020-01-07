package org.wildstang.year2017.auto.testprograms;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2017.auto.programs.PathNameConstants;
import org.wildstang.year2017.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2017.auto.steps.PathFollowerStep;

public class TEST10FtStraightLineSlowPath extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new MotionMagicStraightLine(7));
    }

    @Override
    public String toString() {
        return "7ft straight motion magic";
    }

}
