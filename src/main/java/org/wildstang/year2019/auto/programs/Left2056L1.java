package org.wildstang.year2019.auto.programs;

import org.wildstang.year2019.auto.steps.PathFollowerStep;
import org.wildstang.year2019.auto.steps.CollectHatch;
import org.wildstang.year2019.auto.steps.DeployHatch;
import org.wildstang.year2019.auto.steps.DelayStep;
import org.wildstang.year2019.auto.steps.BasicStraight;
import org.wildstang.year2019.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2019.auto.programs.PathNameConstants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.framework.auto.AutoProgram;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Left2056L1 extends AutoProgram {

    @Override
    protected void defineSteps() {


        //addStep(new MotionMagicStraightLine(132));//goes directly to the hatch from level1
        addStep(new BasicStraight(126));

        addStep(new DeployHatch());

        addStep(new PathFollowerStep(PathNameConstants.the2056B,true,false));

        addStep(new PathFollowerStep(PathNameConstants.the2056C,true, true));
        
        addStep(new CollectHatch());

        addStep(new PathFollowerStep(PathNameConstants.the2056D,true, false));

        addStep(new PathFollowerStep(PathNameConstants.the2056E,true, true));

        addStep(new DeployHatch());
        
        //addStep(new PathFollowerStep(PathNameConstants.the2056F, false));

    }

    @Override
    public String toString() {
        //give it a name
        return "2056 level 1 left";
    }

}