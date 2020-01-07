/**
 * Taken from https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages/blob/master/Java/VelocityClosedLoop/src/org/usfirst/frc/team217/robot/Robot.java
 * Example demonstrating the velocity closed-loop servo.
 * Tested with Logitech F350 USB Gamepad inserted into Driver Station]
 *
 * Be sure to select the correct feedback sensor using configSelectedFeedbackSensor() below.
 *
 * After deploying/debugging this to your RIO, first use the left Y-stick
 * to throttle the Talon manually.  This will confirm your hardware setup.
 * Be sure to confirm that when the Talon is driving forward (green) the
 * position sensor is moving in a positive direction.  If this is not the cause
 * flip the boolean input to the setSensorPhase() call below.
 *
 * Once you've ensured your feedback device is in-phase with the motor,
 * use the button shortcuts to servo to target velocity.
 *
 * Tweak the PID gains accordingly.
 */
package org.wildstang.cantest;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends TimedRobot {
    TalonSRX _talon = new TalonSRX(8);
    VictorSPX _victor1 = new VictorSPX(8);
    VictorSPX _victor2 = new VictorSPX(3);
    Joystick _joy = new Joystick(0);
    StringBuilder _sb = new StringBuilder();
    int _loops = 0;

    @Override
    public void robotInit() {
        /* first choose the sensor */
        _talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
                Constants.kTimeoutMs);
        _talon.setSensorPhase(true);

        _victor1.follow(_talon);
        _victor2.follow(_talon);

        /* set the peak, nominal outputs */
        _talon.configNominalOutputForward(0, Constants.kTimeoutMs);
        _talon.configNominalOutputReverse(0, Constants.kTimeoutMs);
        _talon.configPeakOutputForward(1, Constants.kTimeoutMs);
        _talon.configPeakOutputReverse(-1, Constants.kTimeoutMs);

        /* set closed loop gains in slot0 */
        _talon.config_kF(Constants.kPIDLoopIdx, 0.1097, Constants.kTimeoutMs);
        _talon.config_kP(Constants.kPIDLoopIdx, 0.113333, Constants.kTimeoutMs);
        _talon.config_kI(Constants.kPIDLoopIdx, 0, Constants.kTimeoutMs);
        _talon.config_kD(Constants.kPIDLoopIdx, 0, Constants.kTimeoutMs);
    }

    /** This function is called periodically during operator control */
    @Override
    public void teleopPeriodic() {
        /* get gamepad axis */
        double leftYstick = _joy.getY();
        double motorOutput = _talon.getMotorOutputPercent();
        /* prepare line to print */
        _sb.append("\tout:");
        _sb.append(motorOutput);
        _sb.append("\tspd:");
        _sb.append(_talon.getSelectedSensorVelocity(Constants.kPIDLoopIdx));

        if (!_joy.getRawButton(1)) {
            /* Speed mode */
            /* Convert 500 RPM to units / 100ms.
             * 4096 Units/Rev * 500 RPM / 600 100ms/min in either direction:
             * velocity setpoint is in units/100ms
             */
            double targetVelocity_UnitsPer100ms = leftYstick * 500.0 * 4096 / 600;
            /* 500 RPM in either direction */
            _talon.set(ControlMode.Velocity, targetVelocity_UnitsPer100ms);

            /* append more signals to print when in speed mode. */
            _sb.append("\terr:");
            _sb.append(_talon.getClosedLoopError(Constants.kPIDLoopIdx));
            _sb.append("\ttrg:");
            _sb.append(targetVelocity_UnitsPer100ms);
        } else {
            /* Percent voltage mode */
            _talon.set(ControlMode.PercentOutput, leftYstick);
        }

        if (++_loops >= 10) {
            _loops = 0;
            System.out.println(_sb.toString());
        }
        _sb.setLength(0);
    }
}