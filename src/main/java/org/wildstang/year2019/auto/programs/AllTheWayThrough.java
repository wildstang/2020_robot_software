package org.wildstang.year2019.auto.programs;

import org.wildstang.year2019.auto.steps.DelayStep;
import org.wildstang.year2019.auto.steps.EnableIntakeStep;
import org.wildstang.year2019.auto.steps.EnableWholePathStep;
import org.wildstang.framework.auto.AutoProgram;

public class AllTheWayThrough extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new EnableIntakeStep(true));
        addStep(new DelayStep(2.0));
        addStep(new EnableIntakeStep(false));
        addStep(new EnableWholePathStep(true));
        addStep(new DelayStep(7.0));
        addStep(new EnableWholePathStep(false));
    }

    @Override
    public String toString() {
        //give it a name
        return "AllTheWayThrough";
    }

}