package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.hardware.crio.inputs.WsAnalogGyro;
import org.wildstang.year2017.robot.WSInputs;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnByNDegreesStep extends AutoStep {
    private WsAnalogGyro m_gyro;
    private Drive m_drive;
    private int m_deltaHeading;
    private int m_target;
    double m_rightTarget;
    double m_leftTarget;

    private int m_currentHeading;
    private double m_minRotation;

    private static final int TOLERANCE = 0;

    private static final double TURN_EXPONENT = 3.3; // Adjust for turning speed curve. Higher =
                                                     // faster theoretical turn
    private static final double MIN_ROTATION_OUTPUT = 0.4;

    public TurnByNDegreesStep(int p_deltaHeading, double p_minRotation) {
        m_deltaHeading = p_deltaHeading;
        m_minRotation = p_minRotation;
    }

    @Override
    public void initialize() {
        m_gyro = (WsAnalogGyro) Core.getInputManager().getInput(WSInputs.GYRO.getName());
        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName());

        // The gyro drift compensation means we should be able to set the target in
        // initialize() rather than on
        // first time through update()
        m_currentHeading = getCompassHeading((int) m_gyro.getValue());
        m_target = getCompassHeading((m_currentHeading + m_deltaHeading));

        m_drive.setOpenLoopDrive();
        m_drive.setHighGear(false);
        m_drive.setQuickTurn(true);

        SmartDashboard.putNumber("Initial heading", m_currentHeading);
        SmartDashboard.putNumber("Target heading", m_target);
    }

    @Override
    public void update() {
        m_currentHeading = getCompassHeading((int) m_gyro.getValue());

        // Every 5 cycles (about 100ms) recalculate to adjust for slipping
        // This will also execute on the first run, setting the initial targets
        double throttle = calculateRotationSpeed(m_currentHeading, m_target, TOLERANCE);
        m_drive.setHeading(throttle);

        if (Math.abs(m_target - m_currentHeading) <= TOLERANCE) {
            SmartDashboard.putBoolean("Gyro turn on target", true);
            m_drive.setOpenLoopDrive();
            m_drive.setThrottle(0);
            // fakeFinished = true;
            setFinished(true);
        } else {
            SmartDashboard.putBoolean("Gyro turn on target", false);
        }

        SmartDashboard.putNumber("Current heading", m_currentHeading);
        SmartDashboard.putNumber("Turn rotation speed", throttle);
    }

    private double calculateRotationSpeed(int p_current, int p_target, int p_tolerance) {
        double rotationSpeed = 0.0;

        // Usually the angle changes will be small. For large changes (> 180 difference)
        // follow the shortest path to the new position
        // Smooth the output so that it slows near the target position
        // Limit the minimum output to some percentage (20%?) to prevent stalling

        int diff = p_target - p_current;
        int distanceToTarget = Math.abs(diff);
        int dir = 1;

        if (p_current > p_target) {
            if (distanceToTarget >= 180) {
                dir = 1;
            } else {
                dir = -1;
            }
        } else if (p_current < p_target) {
            if (distanceToTarget >= 180) {
                dir = -1;
            } else {
                dir = 1;
            }
        } else {
            // Prev and target are equal - nowhere to go
        }

        // If we are going past half a rotation, go the shortest route
        // Direction has already been taken care of above
        if (distanceToTarget >= 180) {
            distanceToTarget = 360 - distanceToTarget;
        }

        // Determine the speed of the motor
        // Scale based on proportion of distance to travel of 180 degrees
        // - 180 degrees away results in full speed
        // - closer is slower
        rotationSpeed = Math.pow(
                (((double) distanceToTarget * (1 - Math.pow(m_minRotation, TURN_EXPONENT))) / 180)
                        + Math.pow(m_minRotation, TURN_EXPONENT),
                1 / TURN_EXPONENT);
        // rotationSpeed = (((double)distanceToTarget * (1-m_minRotation))/ 180) +
        // m_minRotation;

        // If we are within tolerance of the target angle, stop turning
        if (distanceToTarget <= p_tolerance) {
            rotationSpeed = 0.0;
        }

        // Set the correct direction
        rotationSpeed *= dir;

        return rotationSpeed;
    }

    private int getCompassHeading(int p_relative) {
        return (p_relative + 360) % 360;
    }

    public double modAngle(double initAngle) {
        double modAngle = initAngle;

        while (modAngle > 180) { // should account for all angles
            modAngle -= 360;
        }
        while (modAngle < -180) {
            modAngle += 360;
        }

        return modAngle;
    }

    @Override
    public String toString() {
        return "Turn to heading step";
    }

}
