package org.wildstang.year2020.subsystems.launching;

import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSSubsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.pid.PIDConstants;
import org.wildstang.framework.subsystems.Subsystem;

/**
 * Class:       TestSubsystem.java
 * Inputs:      1 joystick
 * Outputs:     1 talon
 * Description: This is a testing subsystem that controls a motor with a joystick
 */
public class Shooter implements Subsystem {

    // Inputs
    private DigitalInput aimModeTrigger;
    private DigitalInput fireTrigger;

    // Outputs
    private TalonSRX shooterMasterMotor;
    private VictorSPX shooterFollowerMotor;

    private VictorSPX hoodMotor;

    private Limelight limelightSubsystem;

    // Constants
    public static final double MOTOR_OUTPUT_TOLERANCE = 0.02;
    public static final double MOTOR_POSITION_TOLERANCE = 1.0;

    public static final double UPPER_GOAL_DISTANCE_LIMIT = 0.0;

    public static final double REVS_PER_INCH = 1.0 / 2.0;
    public static final double TICKS_PER_REV = 4096.0;
    public static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;

    // Motor velocities are measured in ticks per decisecond (ticks per 0.1 seconds)
    public static final double SAFE_SHOOTER_SPEED = (5000 * TICKS_PER_REV) / 600.0;
    public static final double AIM_MODE_SHOOTER_SPEED = (7500 * TICKS_PER_REV) / 600.0;

    // PID constants go in order of F, P, I, D
    public static final PIDConstants HOOD_PID_CONSTANTS = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    public static final PIDConstants SHOOTER_PID_CONSTANTS = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    
    public static final double HOOD_TRAVEL_DISTANCE = 4.0;

    // Logic
    private boolean aimModeEnabled;
    private boolean shooterMotorSpeedSetForAimMode;
    private boolean hoodAimed;

    // initializes the subsystem
    public void init() {
        aimModeEnabled = false;

        aimModeTrigger = (DigitalInput) Core.getInputManager().getInput(WSInputs.TURRET_AIM_MODE_TRIGGER);
        aimModeTrigger.addInputListener(this);
        fireTrigger = (DigitalInput) Core.getInputManager().getInput(WSInputs.TURRET_FIRE_TRIGGER);
        fireTrigger.addInputListener(this);

        shooterMasterMotor = new TalonSRX(0);
        shooterMasterMotor.config_kF(0, SHOOTER_PID_CONSTANTS.f);
        shooterMasterMotor.config_kP(0, SHOOTER_PID_CONSTANTS.p);
        shooterMasterMotor.config_kI(0, SHOOTER_PID_CONSTANTS.i);
        shooterMasterMotor.config_kD(0, SHOOTER_PID_CONSTANTS.d);

        shooterFollowerMotor = new VictorSPX(0);
        shooterFollowerMotor.follow(shooterMasterMotor);

        shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
        shooterFollowerMotor.set(ControlMode.Follower, 0);

        hoodMotor = new VictorSPX(0);
        hoodMotor.config_kF(0, HOOD_PID_CONSTANTS.f);
        hoodMotor.config_kP(0, HOOD_PID_CONSTANTS.p);
        hoodMotor.config_kI(0, HOOD_PID_CONSTANTS.i);
        hoodMotor.config_kD(0, HOOD_PID_CONSTANTS.d);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT);
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        double currentShooterMotorSpeed = shooterMasterMotor.getMotorOutputPercent();
        if (currentShooterMotorSpeed < (AIM_MODE_SHOOTER_SPEED + MOTOR_OUTPUT_TOLERANCE) && currentShooterMotorSpeed > (AIM_MODE_SHOOTER_SPEED - MOTOR_OUTPUT_TOLERANCE)) {
            shooterMotorSpeedSetForAimMode = true;
        } else {
            shooterMotorSpeedSetForAimMode = false;
        }

        double currentHoodMotorPosition = hoodMotor.getSelectedSensorPosition();
        if (currentHoodMotorPosition < (HOOD_TRAVEL_DISTANCE + MOTOR_POSITION_TOLERANCE) && currentHoodMotorPosition > (HOOD_TRAVEL_DISTANCE - MOTOR_POSITION_TOLERANCE)) {
            hoodAimed = true;
        } else {
            hoodAimed = false;
        }
    }

    // respond to input updates
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (aimModeTrigger.getValue() == true) { // Entering aim mode
                aimModeEnabled = true;
                shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
                aimToGoal();
            } else { // Exiting aim mode
                aimModeEnabled = false;
                shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
                hoodMotor.set(ControlMode.Position, 0.0);
            }
        }
    }

    // used for testing
    public void selfTest() {

    }

    // resets all variables to the default state
    public void resetState() {
        aimModeEnabled = false;
        shooterMotorSpeedSetForAimMode = false;
        hoodAimed = false;

        shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
        hoodMotor.set(ControlMode.Position, 0.0);
    }

    // returns the unique name of the example
    public String getName() {
        return "Shooter";
    }

    public boolean isShooterMotorSpeedSetForAimMode() {
        return shooterMotorSpeedSetForAimMode;
    }

    public boolean isHoodAimed() {
        return hoodAimed;
    }

    private boolean willAimToUpperGoal() {
        double distanceToTarget = limelightSubsystem.getDistanceToTarget();

        if (distanceToTarget > UPPER_GOAL_DISTANCE_LIMIT) {
            return true;
        } else {
            return false;
        }
    }

    private void aimToGoal() {
        if (willAimToUpperGoal()) {
            hoodMotor.set(ControlMode.Position, HOOD_TRAVEL_DISTANCE * TICKS_PER_INCH);
        } else {
            hoodMotor.set(ControlMode.Position, 0);
        }
    }

}