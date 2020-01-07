package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Drive;

public class TrackVisionToGearStep extends AutoStep {
    double distance;
    double xCorrection;
    private Drive m_drive;

    @Override
    public void initialize() {
        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName());
        m_drive.setAutoGearMode();
        m_drive.setHighGear(true);
    }

    @Override
    public void update() {
        if (m_drive.isGearDropFinished()) {
            setFinished(true);
            m_drive.exitAutoGearMode();
        }
    }

    @Override
    public String toString() {
        return "Track Vision Target To Gear Step";
    }

}
