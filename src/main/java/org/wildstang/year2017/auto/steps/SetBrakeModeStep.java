package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Drive;

public class SetBrakeModeStep extends AutoStep {
    private Drive m_drive;
    private boolean m_brake;

    public SetBrakeModeStep(boolean p_brake) {
        m_brake = p_brake;
    }

    @Override
    public void initialize() {
        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName());
    }

    @Override
    public void update() {
        m_drive.setBrakeMode(m_brake);
        setFinished(true);
    }

    @Override
    public String toString() {
        return "Set brake mode step";
    }

}
