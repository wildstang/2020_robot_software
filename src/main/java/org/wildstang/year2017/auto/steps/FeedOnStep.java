package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Shooter;

//This is an autonomous step which turns on the belt/feed 

public class FeedOnStep extends AutoStep {
    private Shooter shooter;

    public void initialize() {
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName());
    }

    @Override
    public void update() {
        shooter.turnFeedOn();
        setFinished(true);
    }

    @Override
    public String toString() {
        return "Feed On Step";
    }

}
