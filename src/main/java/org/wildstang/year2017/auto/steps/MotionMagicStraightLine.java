package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Drive;
import org.wildstang.year2017.subsystems.drive.DriveConstants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MotionMagicStraightLine extends AutoStep {

    private double m_rotations;
    private Drive m_drive;
    private boolean m_started = false;

    private static final double ONE_ROTATION_INCHES = 4 * Math.PI;

    // Tolerance - in rotations. The numerator is in inches
    private static final double TOLERANCE = 1 / ONE_ROTATION_INCHES;

    public MotionMagicStraightLine(double p_inches) {
        m_rotations = p_inches / ONE_ROTATION_INCHES;
    }

    @Override
    public void initialize() {
        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName());

        m_drive.setMotionMagicMode(true, DriveConstants.MM_DRIVE_F_GAIN);
        m_drive.resetEncoders();
        m_drive.setHighGear(true);
        m_drive.setBrakeMode(true);
    }

    @Override
    public void update() {

        if (!m_started) {
            m_drive.setMotionMagicTargetAbsolute(m_rotations, m_rotations);
            m_started = true;
        } else {
            SmartDashboard.putNumber("Target rotations", m_rotations);
            SmartDashboard.putNumber("Left sensor", m_drive.getLeftSensorValue());

            SmartDashboard.putNumber("Rotations", Math.abs(m_drive.getLeftSensorValue() / 4096));
            SmartDashboard.putNumber("Difference", Math.abs(
                    (Math.abs(m_rotations) - (Math.abs(m_drive.getLeftSensorValue() / 4096)))));
            SmartDashboard.putNumber("Tolerance", TOLERANCE);
            // Check if we've gone far enough
            // if (Math.abs((m_drive.getRightSensorValue() / 4096)) >= m_rotations)
            if (Math.abs((Math.abs(m_rotations)
                    - (Math.abs(m_drive.getRightSensorValue() / 4096)))) <= TOLERANCE) {
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
