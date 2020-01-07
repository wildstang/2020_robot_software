package org.wildstang.year2017.auto.testprograms;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2017.auto.programs.PathNameConstants;
import org.wildstang.year2017.auto.steps.PathFollowerStep;

public class TESTWallToGearCenterPath extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new PathFollowerStep(PathNameConstants.WALL_TO_GEAR_CENTER));
    }

    @Override
    public String toString() {
        return "TEST - Wall to gear center path";
    }

}
