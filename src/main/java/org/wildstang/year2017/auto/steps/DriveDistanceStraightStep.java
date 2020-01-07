package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveDistanceStraightStep extends AutoStep {
    Drive m_drive;
    double m_speed;
    int m_distance2Go;

    /**
     * 
     * @param speed
     *            speed to travel
     * @param inchesToTravel
     *            distance to travel in inches - if negative, goes backwards
     */
    public DriveDistanceStraightStep(double speed, int inchesToTravel) {
        m_speed = speed;
        // Convert distance to positive
        m_distance2Go = Math.abs(inchesToTravel);

        // If distance is negative, invert speed to go backwards
        if (inchesToTravel < 0) {
            m_speed *= -1;
        }
    }

    public void initialize() {
        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName());
        m_drive.setOpenLoopDrive();
        m_drive.resetEncoders();
    }

    @Override
    public void update() {
        SmartDashboard.putString("Drive distance",
                m_drive.getEncoderDistanceInches() + " : " + m_distance2Go);
        if (m_drive.getEncoderDistanceInches() < m_distance2Go) {
            m_drive.setThrottle(m_speed);
        } else {
            m_drive.setThrottle(0);
            setFinished(true);
        }
    }

    @Override
    public String toString() {
        return "Drive Straight Distance";
    }

}
