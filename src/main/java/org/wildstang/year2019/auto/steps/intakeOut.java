package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSSubsystems;
import org.wildstang.year2019.subsystems.drive.Drive;
import org.wildstang.year2019.subsystems.lift.Hatch;
import org.wildstang.year2019.subsystems.ballpath.Ballpath;


//this example is for Drive, you can also modify it to use ballpath, climbwedge, hatch, lift, strafeaxis

public class intakeOut extends AutoStep{

    private Ballpath bp;

    public void update(){
        //call what you want the subsystem to do during this step
        // control the drive with drive. whatever you want

        bp.setIntake();
            setFinished(true);
        
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Deploy Intake";
    }
    public void initialize(){

        bp = (Ballpath) Core.getSubsystemManager().getSubsystem(WSSubsystems.BALLPATH.getName());

    }


}