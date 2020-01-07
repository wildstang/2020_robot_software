package org.wildstang.devbase1.robot;

import org.wildstang.devbase1.subsystems.Drive;
import org.wildstang.framework.core.Subsystems;

/**
 * Enumerate all subsystems on the robot. This enum is used in Robot.java to
 * initialize all subsystems.
 **/
public enum WSSubsystems implements Subsystems {
    DRIVEBASE("Drive Base", Drive.class);

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
