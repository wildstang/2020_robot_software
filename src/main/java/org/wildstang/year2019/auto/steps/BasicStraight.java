package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSSubsystems;
import org.wildstang.year2019.subsystems.drive.Drive;
import org.wildstang.year2019.subsystems.drive.DriveConstants;

import org.wildstang.year2019.subsystems.drive.DrivePID;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BasicStraight extends AutoStep {

    private double m_rotations;
    private Drive m_drive;
    private boolean m_started = false;

    // FIXME use subsystems.drive.DriveConstants.TICKS_PER_INCH, or, even better, conceal the ticks-per-inch inside the drive class
    private static final double ONE_ROTATION_INCHES = 6 * Math.PI;
    
    // Tolerance - in rotations. The numerator is in inches
    private static final double TOLERANCE = 1 / ONE_ROTATION_INCHES;

    public BasicStraight(double p_inches) {
        m_rotations = p_inches / ONE_ROTATION_INCHES;
    }

    @Override
    public void initialize() {
        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVEBASE.getName());

        //m_drive.setMotionMagicMode(false, DrivePID.MM_DRIVE.k.f);
        m_drive.resetEncoders();
       
        m_drive.setBrakeMode(true);
        
    }

    @Override
    public void update() {

        if (!m_started) {
            m_drive.setForward(true);
            //m_drive.setMotionMagicTargetAbsolute(m_rotations, m_rotations);
            m_started = true;
        } else {
            SmartDashboard.putNumber("Target rotations", m_rotations);
            SmartDashboard.putNumber("Left sensor", m_drive.getRightSensorValue());

            SmartDashboard.putNumber("Rotations", Math.abs(m_drive.getRightSensorValue() / 4096));
            SmartDashboard.putNumber("Difference", Math.abs(
                    (Math.abs(m_rotations) - (Math.abs(m_drive.getRightSensorValue() / 4096)))));
            SmartDashboard.putNumber("Tolerance", TOLERANCE);
            // Check if we've gone far enough
            // if (Math.abs((m_drive.getRightSensorValue() / 4096)) >= m_rotations)
            // if (Math.abs((Math.abs(m_rotations)
            //         - (Math.abs(m_drive.getRightSensorValue() / 4096)))) <= TOLERANCE) {
            if (Math.abs(m_drive.getRightSensorValue()/4096) > m_rotations){
                m_drive.setForward(false);
                m_drive.setOpenLoopDrive();
                m_drive.setBrakeMode(true);
                setFinished(true);
            }
        }
    }

    @Override
    public String toString() {
        return "Motion Magic Straight Drive";
    }

}
