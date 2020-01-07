package org.wildstang.year2016.robot;

import org.wildstang.framework.core.Subsystems;
import org.wildstang.year2016.subsystems.Climber;
import org.wildstang.year2016.subsystems.DriveBase;
import org.wildstang.year2016.subsystems.Intake;
import org.wildstang.year2016.subsystems.Shooter;
import org.wildstang.year2016.subsystems.Vision;

public enum WSSubsystems implements Subsystems {

    // MONITOR("Monitor", Monitor.class) {},
    INTAKE("Intake", Intake.class) {
    },
    SHOOTER("Shooter", Shooter.class) {
    },
    CLIMBER("Climber", Climber.class) {
    },
    DRIVE_BASE("Drive Base", DriveBase.class) {
    },
    VISION("Vision", Vision.class) {
    };
    // ENCODER_TEST("Encoder Test", EncoderTest.class) {};

    private String m_name;
    private Class m_class;

    WSSubsystems(String p_name, Class p_class) {
        m_name = p_name;
        m_class = p_class;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public Class getSubsystemClass() {
        return m_class;
    }

}
