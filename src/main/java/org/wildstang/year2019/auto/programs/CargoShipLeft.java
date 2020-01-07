package org.wildstang.year2019.auto.programs;

import org.wildstang.year2019.auto.steps.PathFollowerStep;
import org.wildstang.year2019.auto.steps.CollectHatch;
import org.wildstang.year2019.auto.steps.DeployHatch;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2019.auto.steps.MotionMagicStraightLine;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CargoShipLeft extends AutoProgram {

    @Override
    protected void defineSteps() {

        addStep(new MotionMagicStraightLine(30));//path starts at x=270 from back of hab
            //adjust to go from level 2 or level 1, constants to be added when found 

        addStep(new PathFollowerStep(PathNameConstants.HAB_CARGO_FAR_LEFT,true, true));

        //addStep(new DeployHatch());

        addStep(new PathFollowerStep(PathNameConstants.CARGO_FAR_LEFT_INTERIM_HP,true, false));

        addStep(new PathFollowerStep(PathNameConstants.INTERIM_CARGO_FAR_LEFT_HP,true, true));
        
        //addStep(new CollectHatch());

        addStep(new PathFollowerStep(PathNameConstants.HP_INTERIM_CARGO_CLOSE_LEFT,true, false));

        addStep(new PathFollowerStep(PathNameConstants.INTERIM_CARGO_CLOSE_LEFT,true, true));

        //addStep(new DeployHatch());
    }

    @Override
    public String toString() {
        //give it a name
        return "CargoShipLeft";
    }

}