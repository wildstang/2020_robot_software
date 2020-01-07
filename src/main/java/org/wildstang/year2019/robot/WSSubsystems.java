package org.wildstang.year2019.robot;

import org.wildstang.year2019.subsystems.drive.Drive;
import org.wildstang.year2019.subsystems.ballpath.Ballpath;
import org.wildstang.year2019.subsystems.climbwedge.ClimbWedge;
import org.wildstang.year2019.subsystems.lift.Lift;
import org.wildstang.year2019.subsystems.strafeaxis.StrafeAxis;
import org.wildstang.year2019.subsystems.Claw_Example;
import org.wildstang.year2019.subsystems.lift.Hatch;
import org.wildstang.year2019.subsystems.lift.superlift;
import org.wildstang.framework.core.Subsystems;

/**
 * Enumerate all subsystems on the robot. This enum is used in Robot.java to
 * initialize all subsystems.
 **/
public enum WSSubsystems implements Subsystems {
    DRIVEBASE("Drive Base", Drive.class),
    // CLAW_EXAMPLE("CLAW_EXAMPLE",Claw_Example.class), this shouldn't be uncommented
    BALLPATH("Ballpath", Ballpath.class),
    CLIMB_WEDGE("Climb Wedge", ClimbWedge.class),
    //LIFT("Lift", Lift.class), old lift DO NOT USE
    STRAFE_AXIS("Strafe Axis", StrafeAxis.class),
    HATCH("Hatch",Hatch.class),
    LIFT("Lift",superlift.class),
    ;

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
