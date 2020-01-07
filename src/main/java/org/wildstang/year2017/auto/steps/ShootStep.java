package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Shooter;

public class ShootStep extends AutoStep {

    private Shooter shooter;

    @Override
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
        return "Shoot Step";
    }

}
