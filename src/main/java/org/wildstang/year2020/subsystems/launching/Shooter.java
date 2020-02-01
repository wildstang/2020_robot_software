package org.wildstang.year2020.subsystems.launching;

import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSSubsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
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
    private AnalogInput aimModeTrigger;
    private AnalogInput fireTrigger;

    // Outputs
    private TalonSRX shooterMasterMotor;
    private TalonSRX shooterFollowerMotor;

    private TalonSRX hoodMotor;

    private Limelight limelightSubsystem;

    // Constants
    public static final double MOTOR_OUTPUT_TOLERANCE = 0.02;
    public static final double MOTOR_POSITION_TOLERANCE = 1.0;

    public static final double UPPER_GOAL_DISTANCE_LIMIT = 0.0;

    public static final double REVS_PER_INCH = 1.0 / 2.0;
    public static final double TICKS_PER_REV = 4096.0;
    public static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;

    // Motor velocities in ticks per decisecond (ticks per 0.1 seconds)
    public static final double SAFE_SHOOTER_SPEED = (5000 * TICKS_PER_REV) / 600.0;//34133
    //public static final double SAFE_SHOOTER_SPEED = 8000;
    public static final double AIM_MODE_SHOOTER_SPEED = (6750 * TICKS_PER_REV) / 600.0;//51200

    // PID constants go in order of F, P, I, D
    public static final PIDConstants HOOD_PID_CONSTANTS = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    public static final PIDConstants SHOOTER_PID_CONSTANTS = new PIDConstants(0.016, 0.003, 0.0, 0.0);
    
    public static final double HOOD_TRAVEL_DISTANCE = 4.0;

    // Logic
    private boolean aimModeEnabled;
    private boolean shooterMotorSpeedSetForAimMode;
    private boolean hoodAimed;
    private List<Double> trailingHorizontalAngleOffsets;
    private long lastValueAddedTimestamp;

    @Override
    // Initializes the subsystem (inputs, outputs and logical variables)
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }

    // Initializes inputs
    private void initInputs() {
        aimModeTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.TURRET_AIM_MODE_TRIGGER);
        aimModeTrigger.addInputListener(this);
        fireTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.TURRET_FIRE_TRIGGER);
        fireTrigger.addInputListener(this);
    }

    // Initializes outputs
    private void initOutputs() {
        shooterMasterMotor = new TalonSRX(1);
        shooterMasterMotor.config_kF(0, SHOOTER_PID_CONSTANTS.f);
        shooterMasterMotor.config_kP(0, SHOOTER_PID_CONSTANTS.p);
        shooterMasterMotor.config_kI(0, SHOOTER_PID_CONSTANTS.i);
        shooterMasterMotor.config_kD(0, SHOOTER_PID_CONSTANTS.d);

        //shooterFollowerMotor = new VictorSPX(2);
        shooterFollowerMotor = new TalonSRX(2);
        shooterFollowerMotor.follow(shooterMasterMotor);

        shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
        //shooterFollowerMotor.set(ControlMode.Follower, 0);
        shooterFollowerMotor.follow(shooterMasterMotor);
        shooterFollowerMotor.setInverted(true);

        hoodMotor = new TalonSRX(0);
        hoodMotor.config_kF(0, HOOD_PID_CONSTANTS.f);
        hoodMotor.config_kP(0, HOOD_PID_CONSTANTS.p);
        hoodMotor.config_kI(0, HOOD_PID_CONSTANTS.i);
        hoodMotor.config_kD(0, HOOD_PID_CONSTANTS.d);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT);
    }

    @Override
    // Updates the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        double currentShooterMotorSpeed = shooterMasterMotor.getMotorOutputPercent();
        SmartDashboard.putNumber("Encoder position", shooterMasterMotor.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("Encoder value", shooterMasterMotor.getSensorCollection().getQuadratureVelocity());
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

        double horizontalAngleOffset = limelightSubsystem.getTXValue();
        if (System.currentTimeMillis() > lastValueAddedTimestamp + 25L) {
            if (trailingHorizontalAngleOffsets.size() == 20) {
                trailingHorizontalAngleOffsets.remove(0);
            }

            trailingHorizontalAngleOffsets.add(horizontalAngleOffset);
            lastValueAddedTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    // Responds to updates from inputs
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (aimModeTrigger.getValue() > 0.75) { // Entering aim mode
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

    @Override
    // Resets all variables to the default state
    public void resetState() {
        aimModeEnabled = false;
        shooterMotorSpeedSetForAimMode = false;
        hoodAimed = false;

        shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
        hoodMotor.set(ControlMode.Position, 0.0);

        trailingHorizontalAngleOffsets = new ArrayList<Double>();
        lastValueAddedTimestamp = 0L;
    }

    @Override
    // Returns the subsystem's name
    public String getName() {
        return "Shooter";
    }

    @Override
    // Tests the subsystem (unimplemented right now)
    public void selfTest() {}

    // Returns whether the shooter motor's speed is right for being in aim mode
    public boolean isShooterMotorSpeedSetForAimMode() {
        return shooterMotorSpeedSetForAimMode;
    }

    // Returns whether the hood is considered done aiming
    public boolean isHoodAimed() {
        return hoodAimed;
    }

    // Decides whether is the inner goal is within range (+/- 15 degrees horizontally)
    private boolean willAimToInnerGoal() {
        double horizontalAngleOffsetSum = 0.0;

        for (int i = 0; i < trailingHorizontalAngleOffsets.size(); i++) {
            horizontalAngleOffsetSum += trailingHorizontalAngleOffsets.get(i);
        }

        double netHorizontalAngleOffset = horizontalAngleOffsetSum / (double) trailingHorizontalAngleOffsets.size();

        if (netHorizontalAngleOffset <= 15.0 && netHorizontalAngleOffset >= -15.0) {
            return true;
        } else {
            return false;
        }
    }

    // Aims to either the inner or outer goal based on horizontal angle offset
    private void aimToGoal() {
        if (willAimToInnerGoal()) {
            hoodMotor.set(ControlMode.Position, HOOD_TRAVEL_DISTANCE * TICKS_PER_INCH);
        } else {
            hoodMotor.set(ControlMode.Position, 0);
        }
    }

}