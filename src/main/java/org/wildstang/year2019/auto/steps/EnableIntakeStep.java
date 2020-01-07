package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSSubsystems;

import org.wildstang.year2019.subsystems.ballpath.Ballpath;


//this example is for Drive, you can also modify it to use ballpath, climbwedge, hatch, lift, strafeaxis

public class EnableIntakeStep extends AutoStep{

    private Ballpath ballpath;
    private boolean enable;

    public EnableIntakeStep(boolean enable) {
        this.enable = enable;
    }

    public void update() {
        ballpath.enableIntake(enable);
        setFinished(true);
    }

    public String toString(){
        return "EnableIntakeStep";
    }

    public void initialize(){

        ballpath = (Ballpath) Core.getSubsystemManager().getSubsystem(WSSubsystems.BALLPATH.getName());

    }
}