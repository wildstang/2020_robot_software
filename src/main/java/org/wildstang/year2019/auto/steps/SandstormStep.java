package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSSubsystems;

import org.wildstang.year2019.subsystems.drive.Drive;

/**
 * TODO: Description of this class goes here
 *
 * XXX TODO: grab a mentor and go over                                   
 * https://github.com/wildstang/2019_robot_software/blob/master/design_docs/year2019/drive.md
 * before using or adding to this class.
 */
public class SandstormStep extends AutoStep {
    // variables to be used
    private Drive drive;

    public SandstormStep()
    {
        
    }

    public void initialize()
    {
        // initialize variables and stuff
        drive = (Drive) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVEBASE.getName());
        
        drive.resetEncoders();
        drive.purgePaths();
        drive.setOpenLoopDrive();
        drive.setBrakeMode(false);
    }

    public void update()
    {
        // TODO: Do we need call anything to SmartDashboard?
        // if so.. add it here
        
    }

    public String toString()
    {
        return "Sandstorm Step";
    }
}
