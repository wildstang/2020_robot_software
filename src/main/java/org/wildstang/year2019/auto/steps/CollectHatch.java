package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSSubsystems;
import org.wildstang.year2019.subsystems.drive.Drive;
import org.wildstang.year2019.subsystems.lift.Hatch;

import org.wildstang.framework.timer.WsTimer;


//this example is for Drive, you can also modify it to use ballpath, climbwedge, hatch, lift, strafeaxis

public class CollectHatch extends AutoStep{

    private Hatch hatch;
    private boolean firstRun = true;
    private WsTimer timer = new WsTimer();

    
    public void update(){
        if (Drive.autoEStopActivated == true) {
            setFinished(true);
        } else {
            //call what you want the subsystem to do during this step
            // control the drive with drive. whatever you want
            if (firstRun) {
                timer.reset();
                timer.start();
                firstRun = false;
            }

            setFinished(hatch.collectAuto(timer.hasPeriodPassed(0.1)));
        }
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Deploy Hatch";
    }
    public void initialize(){

        hatch = (Hatch) Core.getSubsystemManager().getSubsystem(WSSubsystems.HATCH.getName());

    }


}