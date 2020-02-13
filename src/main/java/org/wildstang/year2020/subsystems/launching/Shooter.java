package org.wildstang.year2020.subsystems.launching;

import org.wildstang.year2020.robot.CANConstants;
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
    private AnalogInput aimModeTrigger;
    private AnalogInput fireTrigger;
    private DigitalInput pointBlankShot; 

    // Outputs
    private TalonSRX shooterMasterMotor;
    private TalonSRX shooterFollowerMotor;

    private TalonSRX hoodMotor;

    private Limelight limelightSubsystem;

    // Constants
    public static final double MOTOR_OUTPUT_TOLERANCE = 1.04;
    public static final double MOTOR_POSITION_TOLERANCE = 1.0;

    public static final double UPPER_GOAL_DISTANCE_LIMIT = 0.0;

    public static final double REVS_PER_INCH = 1.0 / 2.0;
    public static final double TICKS_PER_REV = 4096.0;
    public static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;
    
    public static final double POINTBLANK_HOOD = 0; //tbd

    // Motor velocities in ticks per decisecond (ticks per 0.1 seconds)
    public static final double SAFE_SHOOTER_SPEED = (5000 * TICKS_PER_REV) / 600.0;//34133
    //public static final double SAFE_SHOOTER_SPEED = 8000;
    public static final double AIM_MODE_SHOOTER_SPEED = (6750 * TICKS_PER_REV) / 600.0;//51200

    // PID constants go in order of F, P, I, D
    public static final PIDConstants HOOD_PID_CONSTANTS = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    public static final PIDConstants SAFE_SHOOTER_PID_CONSTANTS = new PIDConstants(0.02, 0.024, 0.0, 0.0);//might push these P values way up
    public static final PIDConstants AIMING_SHOOTER_PID_CONSTANTS = new PIDConstants(0.02, 0.032, 0.0, 0.0);//same here
    
    // TODO: More regression coefficients may be needed based on what regression type we choose to use
    public static final double AIMING_INNER_REGRESSION_A = 0.0;
    public static final double AIMING_OUTER_REGRESSION_A = 0.0;
    

    // Logic
    private boolean aimModeEnabled;
    private boolean shooterMotorSpeedSetForAimMode;
    private boolean hoodAimed;
    private List<Double> trailingHorizontalAngleOffsets;
    private long lastValueAddedTimestamp;
    private double hoodTravelDistance;

    @Override
    // Initializes the subsystem (inputs, outputs and logical variables)
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }

    // Initializes inputs
    private void initInputs() {
        aimModeTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_LEFT);
        aimModeTrigger.addInputListener(this);
        fireTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_RIGHT);
        fireTrigger.addInputListener(this);
        pointBlankShot = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_LEFT);
        pointBlankShot.addInputListener(this);
    }

    // Initializes outputs
    private void initOutputs() {
        shooterMasterMotor = new TalonSRX(CANConstants.LAUNCHER_TALON);
        shooterMasterMotor.config_kF(0, SAFE_SHOOTER_PID_CONSTANTS.f);
        shooterMasterMotor.config_kP(0, SAFE_SHOOTER_PID_CONSTANTS.p);
        shooterMasterMotor.config_kI(0, SAFE_SHOOTER_PID_CONSTANTS.i);
        shooterMasterMotor.config_kD(0, SAFE_SHOOTER_PID_CONSTANTS.d);
        shooterMasterMotor.config_kF(1, AIMING_SHOOTER_PID_CONSTANTS.f);
        shooterMasterMotor.config_kP(1, AIMING_SHOOTER_PID_CONSTANTS.p);
        shooterMasterMotor.config_kI(1, AIMING_SHOOTER_PID_CONSTANTS.i);
        shooterMasterMotor.config_kD(1, AIMING_SHOOTER_PID_CONSTANTS.d);

        shooterMasterMotor.setInverted(true);

        //shooterFollowerMotor = new VictorSPX(2);
        shooterFollowerMotor = new TalonSRX(CANConstants.LAUNCHER_VICTOR);
        shooterFollowerMotor.follow(shooterMasterMotor);

        shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
        //shooterFollowerMotor.set(ControlMode.Follower, 0);
        shooterFollowerMotor.follow(shooterMasterMotor);
        shooterFollowerMotor.setInverted(true);

        hoodMotor = new TalonSRX(CANConstants.HOOD_MOTOR);
        hoodMotor.config_kF(0, HOOD_PID_CONSTANTS.f);
        hoodMotor.config_kP(0, HOOD_PID_CONSTANTS.p);
        hoodMotor.config_kI(0, HOOD_PID_CONSTANTS.i);
        hoodMotor.config_kD(0, HOOD_PID_CONSTANTS.d);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT);
    }

    @Override
    // Updates the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        double currentShooterMotorSpeed = shooterMasterMotor.getSensorCollection().getQuadratureVelocity();
        SmartDashboard.putNumber("Encoder Position", shooterMasterMotor.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("Encoder Velocity", shooterMasterMotor.getSensorCollection().getQuadratureVelocity());
        if (currentShooterMotorSpeed < (AIM_MODE_SHOOTER_SPEED * MOTOR_OUTPUT_TOLERANCE) && currentShooterMotorSpeed > (AIM_MODE_SHOOTER_SPEED / MOTOR_OUTPUT_TOLERANCE)) {
            shooterMotorSpeedSetForAimMode = true;
        } else {
            shooterMotorSpeedSetForAimMode = false;
        }

        double currentHoodMotorPosition = hoodMotor.getSelectedSensorPosition();
        if (currentHoodMotorPosition < (hoodTravelDistance + MOTOR_POSITION_TOLERANCE) && currentHoodMotorPosition > (hoodTravelDistance - MOTOR_POSITION_TOLERANCE)) {
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
                shooterMasterMotor.selectProfileSlot(1, 0);
                shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
                aimToGoal();
            } 
            else { // Exiting aim mode
                aimModeEnabled = false;
                shooterMasterMotor.selectProfileSlot(0, 0);
                shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
                hoodMotor.set(ControlMode.Position, 0.0);
            }
        }
        if(source == pointBlankShot) {
            if(pointBlankShot.getValue()) {
                hoodMotor.set(ControlMode.Position, POINTBLANK_HOOD);
                shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
            }
            else {
                hoodMotor.set(ControlMode.Position, 0);
                shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
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

    // Decides whether the inner goal is within range (+/- 15 degrees horizontally)
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
            hoodTravelDistance = AIMING_INNER_REGRESSION_A; // TODO: Perform regression calculation

            hoodMotor.set(ControlMode.Position, hoodTravelDistance * TICKS_PER_INCH);
        } else {
            hoodTravelDistance = AIMING_OUTER_REGRESSION_A; // TODO: Perform regression calculation
            
            hoodMotor.set(ControlMode.Position, 0);
        }
    }
    public void setAutonShooterSpeed(){
        shooterMasterMotor.selectProfileSlot(1, 0);
        shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
    }
    public void setHoodPosition(double position){
        hoodMotor.set(ControlMode.Position, position);
    }
    public void setAim(){
        aimToGoal();
    }

}