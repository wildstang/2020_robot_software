package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Gear;

public class CloseGearHolderStep extends AutoStep {
    private Gear m_gearSubsystem;

    @Override
    public void initialize() {
        m_gearSubsystem = (Gear) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.GEAR.getName());
    }

    @Override
    public void update() {
        m_gearSubsystem.closeDoor();
        setFinished(true);
    }

    @Override
    public String toString() {
        return "Close gear holder step";
    }

}
