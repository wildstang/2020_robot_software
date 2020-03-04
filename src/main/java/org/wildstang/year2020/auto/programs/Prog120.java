package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.programs.PathNameConstants;
import org.wildstang.year2020.auto.steps.IntakeOnStep;

public class Prog120 extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new PathFollowerStep(PathNameConstants.TEST120,true,true));
        System.out.println("Made it here");
        //addStep(new IntakeOnStep());
        //System.out.println("made it here as well");
    }

    @Override
    public String toString() {
        //give it a name
        return "Feb26 test";
    }

}