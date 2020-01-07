package org.wildstang.year2019.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2019.auto.steps.ExampleAutoStep;
import org.wildstang.year2019.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2019.auto.steps.StraightDrive;
import org.wildstang.year2019.auto.steps.CollectHatch;
import org.wildstang.year2019.auto.steps.DeployHatch;
import org.wildstang.year2019.auto.steps.BasicStraight;
public class ExampleAutoProgram extends AutoProgram {

    @Override
    protected void defineSteps() {
        //addStep(new ExampleAutoStep());
        //addStep(new MotionMagicStraightLine(50));
        //addStep(new StraightDrive(50));
        addStep(new BasicStraight(122));
    }

    @Override
    public String toString() {
        //give it a name
        return "ExampleAutoProgram";
    }

}