package org.wildstang.year2020.robot;

import org.wildstang.year2020.subsystems.drive.Drive;
import org.wildstang.year2020.subsystems.Limelight;
//import org.wildstang.year2020.subsystems.drive.FalconDrive;
//import org.wildstang.year2020.subsystems.TestSubsystem;
import org.wildstang.framework.core.Subsystems;

/**
 * Enumerate all subsystems on the robot. This enum is used in Robot.java to
 * initialize all subsystems.
 **/
public enum WSSubsystems implements Subsystems {
    DRIVEBASE("Drive Base", Drive.class),
    //FALCONDRIVE("Falcon Drive",FalconDrive.class),
    //TEST("Test", TestSubsystem.class);
    LIMELIGHT("Limelight", Limelight.class);

    private String name;

    private Class<?> subsystemClass;

    WSSubsystems(String name, Class<?> subsystemClass) {
        this.name = name;
        this.subsystemClass = subsystemClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getSubsystemClass() {
        return subsystemClass;
    }
}
