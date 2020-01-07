package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.hardware.crio.inputs.WsAnalogGyro;
import org.wildstang.year2017.robot.WSInputs;
import org.wildstang.year2017.robot.WSSubsystems;
import org.wildstang.year2017.subsystems.Drive;
import org.wildstang.year2017.subsystems.drive.DriveConstants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnByNDegreesStepMagic extends AutoStep {
    private WsAnalogGyro m_gyro;
    private Drive m_drive;
    private int m_deltaHeading;
    private double f_gain;
    private int m_target;
    double m_rightTarget;
    double m_leftTarget;

    // boolean fakeFinished = false;

    private int m_currentHeading;

    private long m_cycleCount = 0;

    private static final int TOLERANCE = 1;
    private static final int TICKS_PER_DEGREE_LEFT = 103;
    private static final int TICKS_PER_DEGREE_RIGHT = 105;

    public TurnByNDegreesStepMagic(int p_deltaHeading, double f_gain) {
        m_deltaHeading = p_deltaHeading;
        this.f_gain = f_gain;
    }

    public TurnByNDegreesStepMagic(int p_deltaHeading) {
        // TODO Auto-generated constructor stub
        this(p_deltaHeading, DriveConstants.MM_DRIVE_F_GAIN);
    }

    @Override
    public void initialize() {
        m_gyro = (WsAnalogGyro) Core.getInputManager().getInput(WSInputs.GYRO.getName());
        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName());

        m_drive.setMotionMagicMode(true, f_gain);

        // The gyro drift compensation means we should be able to set the target in
        // initialize() rather than on
        // first time through update()
        m_currentHeading = (int) m_gyro.getValue();
        m_target = getCompassHeading((m_currentHeading + m_deltaHeading));
        if (m_deltaHeading < 0) { // here we add a little overshoot to make up for increased
                                  // friction on carpet
            m_deltaHeading -= 0;
        } else if (m_deltaHeading > 0) {
            m_deltaHeading += 0;
        }
        double rotationsLeft = getRotationsForDeltaAngle((int) modAngle(m_deltaHeading), true);
        double rotationsRight = -getRotationsForDeltaAngle((int) modAngle(m_deltaHeading), false);
        // Turning left means right is a positive count
        m_rightTarget = rotationsRight;
        m_leftTarget = rotationsLeft;

        SmartDashboard.putNumber("Initial heading", m_currentHeading);
        SmartDashboard.putNumber("Target heading", m_target);
        SmartDashboard.putNumber("Target Encoder Ticks Right", rotationsRight * 4096);
        SmartDashboard.putNumber("Target Encoder Ticks Right", rotationsLeft * 4096);
    }

    // 35,399 and 40,626
    @Override
    public void update() {
        m_drive.setHighGear(false);

        m_currentHeading = getCompassHeading((int) m_gyro.getValue());

        // Every 5 cycles (about 100ms) recalculate to adjust for slipping
        // This will also execute on the first run, setting the initial targets
        // if (m_cycleCount++ % 5 == 0)
        // {
        // These values are rotations, for Motion Magic
        // double rotations = getRotationsForDeltaAngle(m_currentHeading - m_target);
        //
        // // Turning left means right is a positive count
        // m_rightTarget = rotations;
        // m_leftTarget = -rotations;
        // if (!fakeFinished)
        // {
        m_drive.setMotionMagicTargetAbsolute(m_leftTarget, m_rightTarget);
        // }
        // }

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
    }

    private double getRotationsForDeltaAngle(int p_delta, boolean left) {
        if (left) {
            double ticksLeft = p_delta * TICKS_PER_DEGREE_LEFT;
            SmartDashboard.putNumber("p_delta", p_delta);
            return ticksLeft / 4096;
        } else {
            double ticksRight = p_delta * TICKS_PER_DEGREE_RIGHT;
            SmartDashboard.putNumber("p_delta", p_delta);
            return ticksRight / 4096;
        }
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
