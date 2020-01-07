package org.wildstang.year2019.auto.programs.Left2056steps;

import org.wildstang.year2019.auto.steps.PathFollowerStep;
import org.wildstang.year2019.auto.steps.CollectHatch;
import org.wildstang.year2019.auto.steps.DeployHatch;
import org.wildstang.year2019.auto.steps.DelayStep;
import org.wildstang.year2019.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2019.auto.programs.PathNameConstants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.framework.auto.AutoProgram;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class step2 extends AutoProgram {

    @Override
    protected void defineSteps() {
        //addStep(new PathFollowerStep(PathNameConstants.the2056B,false));
        addStep(new PathFollowerStep(PathNameConstants.L20562I,true,false));
    }

    @Override
    public String toString() {
        //give it a name
        return "step2";
    }

}