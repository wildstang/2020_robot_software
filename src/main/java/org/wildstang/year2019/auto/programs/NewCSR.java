package org.wildstang.year2019.auto.programs;

import org.wildstang.year2019.auto.steps.PathFollowerStep;
import org.wildstang.year2019.auto.steps.CollectHatch;
import org.wildstang.year2019.auto.steps.DeployHatch;
import org.wildstang.year2019.auto.steps.DelayStep;
import org.wildstang.year2019.auto.steps.BasicStraight;
import org.wildstang.year2019.auto.steps.MotionMagicStraightLine;
import org.wildstang.year2019.auto.programs.PathNameConstants;
import org.wildstang.year2019.auto.steps.SandstormStep;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.framework.auto.AutoProgram;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class NewCSR extends AutoProgram {

    @Override
    protected void defineSteps() {


        
        addStep(new PathFollowerStep(PathNameConstants.LCS1I,false,true));

        addStep(new DeployHatch());

        addStep(new PathFollowerStep(PathNameConstants.LCS2I,false,false));

        addStep(new PathFollowerStep(PathNameConstants.LCS3I,false, true));
        
        addStep(new CollectHatch());

        addStep(new PathFollowerStep(PathNameConstants.LCS4I,false, false));

        addStep(new PathFollowerStep(PathNameConstants.LCS5I,false, true));

        addStep(new DeployHatch());

        addStep(new SandstormStep());

    }

    @Override
    public String toString() {
        //give it a name
        return "Right CS";
    }

}