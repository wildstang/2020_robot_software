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
public class MoveForwardStepDrive extends AutoStep {
    // variables to be used
    private Drive drive;

    private double driveSpeed;
    private boolean driveStarted;
    private double distanceToDrive;

    public MoveForwardStepDrive(double distance, double speed)
    {
        // TODO: Do we need to use rotations?
        // TODO: See class JavaDoc above
        distanceToDrive = Math.abs(distance);
        driveSpeed = speed;
    }

    public void initialize()
    {
        // initialize variables and stuff
        drive = (Drive) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVEBASE.getName());
        drive.resetEncoders();
    }

    public void update()
    {
        // TODO: Do we need call anything to SmartDashboard?
        // if so.. add it here
        if (!driveStarted)
        {
            //drive.setAutonStraightDrive();
            driveStarted = true;
        }
        else
        {
            // setFinished(true); do we need this?
        }
    }

    public String toString()
    {
        return "MoveForwardStepDrive";
    }
}
