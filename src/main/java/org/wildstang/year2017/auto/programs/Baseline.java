package org.wildstang.year2017.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2017.auto.steps.MotionMagicStraightLine;

public class Baseline extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new MotionMagicStraightLine(83));
    }

    @Override
    public String toString() {
        return "Baseline";
    }

}
