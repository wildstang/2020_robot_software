package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2020.robot.WSSubsystems;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;

public class FeedOnStep extends AutoStep{

    private Ballpath Feeder;

    public void update() {
        Feeder.turnOnFeed();
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Feed On";
    }
    public void initialize(){
        Feeder = (Ballpath) Core.getSubsystemManager().getSubsystem(WSSubsystems.BALLPATH.getName());
    }
}