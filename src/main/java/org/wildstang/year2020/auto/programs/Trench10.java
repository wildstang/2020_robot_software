package org.wildstang.year2020.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.year2020.auto.steps.FeedOffStep;
import org.wildstang.year2020.auto.steps.FeedOnStep;
import org.wildstang.year2020.auto.steps.PathFollowerStep;
import org.wildstang.year2020.auto.steps.SetHoodStep;
import org.wildstang.year2020.auto.steps.SetTurretStep;
import org.wildstang.year2020.auto.steps.AutoAimStep;
import org.wildstang.year2020.auto.steps.DelayStep;
import org.wildstang.year2020.auto.steps.IntakeOnStep;
import org.wildstang.year2020.auto.programs.PathNameConstants;

public class Trench10 extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new IntakeOnStep());
        AutoParallelStepGroup initial = new AutoParallelStepGroup();
        initial.addStep(new IntakeOnStep());
        //initial.addStep(new PathFollowerStep(PathNameConstants.TRENCH10A,true,true));
        initial.addStep(new SetTurretStep(-29400));
        addStep(initial);

        //make this step aim when it's nearly done
        AutoParallelStepGroup second = new AutoParallelStepGroup();
        //second.addStep(new PathFollowerStep(PathNameConstants.TRENCH10AA,true,false));
        AutoSerialStepGroup secondA = new AutoSerialStepGroup();
        secondA.addStep(new DelayStep(1.0));
        secondA.addStep(new AutoAimStep(true));
        second.addStep(secondA);
        addStep(second);

        addStep(new FeedOnStep());
        addStep(new DelayStep(1.5));
        addStep(new FeedOffStep());

        AutoParallelStepGroup third = new AutoParallelStepGroup();
        third.addStep(new AutoAimStep(false));
        //third.addStep(new PathFollowerStep(PathNameConstants.TRENCH10B, true, true));
        addStep(third);

        AutoParallelStepGroup fourth = new AutoParallelStepGroup();
        //fourth.addStep(new PathFollowerStep(PathNameConstants.TRENCH10B,true,false));
        AutoSerialStepGroup fourthA = new AutoSerialStepGroup();
        fourthA.addStep(new DelayStep(2.0));
        fourthA.addStep(new AutoAimStep(true));
        fourth.addStep(fourthA);
        addStep(fourth);
        
        addStep(new FeedOnStep());
    }

    @Override
    public String toString() {
        //give it a name
        return "Trench10";
    }

}