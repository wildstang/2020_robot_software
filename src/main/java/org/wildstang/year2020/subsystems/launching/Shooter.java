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
import org.wildstang.framework.timer.WsTimer;

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

    private AnalogInput hoodManualAdjustment;
    private DigitalInput hoodManualOverrideButton;

    private DigitalInput hoodNudgeUpButton;
    private DigitalInput hoodNudgeDownButton;

    private DigitalInput startButton;
    private DigitalInput selectButton;

    private DigitalInput hoodEncoderResetButton;

    // Outputs
    private TalonSRX shooterMasterMotor;
    private TalonSRX shooterFollowerMotor;

    private TalonSRX hoodMotor;

    private Limelight limelightSubsystem;

    // Constants
    public static final double MOTOR_OUTPUT_TOLERANCE = 1.04;//flywheel tolerance for determining it it's at the setpoint
    public static final double MOTOR_POSITION_TOLERANCE = 1.0;//hood tolerance for determining if it's at the setpoint

    public static final double UPPER_GOAL_DISTANCE_LIMIT = 0.0;//unused

    public static final double REVS_PER_INCH = 1.0 / 2.0;
    public static final double TICKS_PER_REV = 1024.0;
    public static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;
    
    public static final double POINTBLANK_HOOD = 45; //hood angle for pointblank shot

    // Motor velocities in ticks per decisecond
    public static final double SAFE_SHOOTER_SPEED = (3750 * TICKS_PER_REV) / 600.0;//dropped to 25600 from 34133
    //public static final double SAFE_SHOOTER_SPEED = 8000;
    public static final double AIM_MODE_SHOOTER_SPEED = (6750 * TICKS_PER_REV) / 600.0;//51200

    // PID constants go in order of F, P, I, D
    public static final PIDConstants HOOD_PID_CONSTANTS = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    public static final PIDConstants SAFE_SHOOTER_PID_CONSTANTS = new PIDConstants(0.015, 0.024, 0.0, 0.0);//might push these P values way up
    public static final PIDConstants AIMING_SHOOTER_PID_CONSTANTS = new PIDConstants(0.025, 5.00, 0.0, 0.0);//same here // 0.02 0.032
    
    // TODO: More regression coefficients may be needed based on what regression type we choose to use
    public static final double AIMING_INNER_REGRESSION_A = -1.9325;
    public static final double AIMING_INNER_REGRESSION_B = 74.177;
    public static final double AIMING_INNER_REGRESSION_C = -69.84;
    public static final double AIMING_OUTER_REGRESSION_A = -1.9325;
    public static final double AIMING_OUTER_REGRESSION_B = 74.177;
    public static final double AIMING_OUTER_REGRESSION_C = -69.84;
    

    public static final double HOOD_OUTPUT_SCALE = 1.0;

    private static final double HOOD_REG_ADJUSTMENT_INCREMENT = 5;

    public static final double HOOD_KP = -0.015;

    public static final double INNER_GOAL_MIN_DISTANCE = 10.00;
    // Ratio of horizontal length of target to distance from target (should be constant if robot is straight on target)
    public static final double INNER_GOAL_STANDARD_RATIO = 1.0;
    public static final double INNER_GOAL_THRESHOLD = 0.1;

    // Logic
    private boolean aimModeEnabled;

    private boolean shooterMotorSpeedSetForAimMode;
    private boolean hoodAimed;

    public List<Double> trailingHorizontalAngleOffsets;
    private long lastValueAddedTimestamp;

    private double hoodTravelDistance;
    private double hoodMotorOutput;
    private double minimumHoodAdjustment;
    private boolean hoodManualOverride;
    private boolean isPointBlank;

    private boolean hoodEncoderResetPressed;
    private long hoodEncoderResetTimestamp;

    private double hoodEncoderOffset;

    // This counts how many times the incremental adjustment value should be added to the regression for hood position PID regression
    // Positive value -> upward adjustment, negative value -> downward adjustment, 0 -> no adjustment
    private int hoodRegAdjustmentCount;  

    private double hoodTarget;
    private WsTimer timer = new WsTimer();
    private static final double TIMEPASSED = 1.0;
    private boolean running;
    private boolean shooterOn;
    private boolean autoMode;

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
        hoodManualAdjustment = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_Y);
        hoodManualAdjustment.addInputListener(this);
        hoodManualOverrideButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_BUTTON);
        hoodManualOverrideButton.addInputListener(this);
        hoodNudgeUpButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        hoodNudgeUpButton.addInputListener(this);
        hoodNudgeDownButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        hoodNudgeDownButton.addInputListener(this);
        hoodEncoderResetButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_RIGHT);
        hoodEncoderResetButton.addInputListener(this);
        startButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_START);
        startButton.addInputListener(this);
        selectButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SELECT);
        selectButton.addInputListener(this);
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

        shooterMasterMotor.setInverted(false);

        shooterFollowerMotor = new TalonSRX(CANConstants.LAUNCHER_VICTOR);
        shooterFollowerMotor.follow(shooterMasterMotor);
        shooterFollowerMotor.setInverted(true);

        shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);

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
        // SmartDashboard.putNumber("Area Value", limelightSubsystem.getTAValue());
        // SmartDashboard.putNumber("Area Adjust", limelightSubsystem.getTAValue() / Math.cos(limelightSubsystem.getTXValue()));
        SmartDashboard.putNumber("Dist to Target", limelightSubsystem.getDistanceToTarget());
        SmartDashboard.putNumber("THOR Value", limelightSubsystem.getTHorValue());
        SmartDashboard.putNumber("Magic Ratio", limelightSubsystem.getTHorValue() / limelightSubsystem.getDistanceToTarget());
        
        if(running && timer.hasPeriodPassed(TIMEPASSED)) {
            timer.reset();
            running = false;
            shooterOn = !shooterOn;
        }
        if (hoodManualOverride == true) {
            hoodMotor.set(ControlMode.PercentOutput, hoodMotorOutput * HOOD_OUTPUT_SCALE);
        }
        SmartDashboard.putNumber("Hood moving", hoodMotorOutput * HOOD_OUTPUT_SCALE);

        if (!shooterOn){
            shooterMasterMotor.set(ControlMode.PercentOutput, 0.0);
        } else if (aimModeEnabled){
            shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
            aimToGoal();
        } else {
            if (autoMode){
                shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
            } else {
                shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
                if (!hoodManualOverride){
                    setHoodMotorPosition(0.0);
                }
            }
        }
        double currentShooterMotorSpeed = shooterMasterMotor.getSensorCollection().getQuadratureVelocity();
        SmartDashboard.putNumber("Shooter Position", shooterMasterMotor.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("Shooter Velocity", shooterMasterMotor.getSensorCollection().getQuadratureVelocity());
        shooterMotorSpeedSetForAimMode = (currentShooterMotorSpeed < (AIM_MODE_SHOOTER_SPEED * MOTOR_OUTPUT_TOLERANCE) && currentShooterMotorSpeed > (AIM_MODE_SHOOTER_SPEED / MOTOR_OUTPUT_TOLERANCE)); 

        double currentHoodMotorPosition = hoodMotor.getSensorCollection().getAnalogInRaw();
        SmartDashboard.putNumber("Hood Raw", currentHoodMotorPosition);
        SmartDashboard.putNumber("Hood Position", getHoodEncoderPosition());
        hoodAimed = (currentHoodMotorPosition < (hoodTravelDistance + MOTOR_POSITION_TOLERANCE) && currentHoodMotorPosition > (hoodTravelDistance - MOTOR_POSITION_TOLERANCE));

        double horizontalAngleOffset = limelightSubsystem.getTXValue();
        if (System.currentTimeMillis() > lastValueAddedTimestamp + 25L) {
            if (trailingHorizontalAngleOffsets.size() == 20) {
                trailingHorizontalAngleOffsets.remove(0);
            }
            trailingHorizontalAngleOffsets.add(horizontalAngleOffset);
            lastValueAddedTimestamp = System.currentTimeMillis();
        } 
        

        if (hoodEncoderResetPressed == true && System.currentTimeMillis() >= hoodEncoderResetTimestamp + 1000L) {
            resetHoodEncoder();
        }

        SmartDashboard.putNumber("Hood Target", hoodTarget);

        // if (Math.abs(hoodTarget - getHoodEncoderPosition()) > 500.0) {
        //     if (hoodTarget > 500.0) {
        //         hoodMotor.set(ControlMode.PercentOutput, ((hoodTarget - 1024.0) - getHoodEncoderPosition()) * HOOD_KP);
        //     } else if (getHoodEncoderPosition() > 500.0) {
        //         hoodMotor.set(ControlMode.PercentOutput, (hoodTarget - (getHoodEncoderPosition() - 1024.0)) * HOOD_KP);
        //     }
            
        // } else {
        //     hoodMotor.set(ControlMode.PercentOutput, (hoodTarget - getHoodEncoderPosition()) * -HOOD_KP);
        // }
        setHoodMotorPosition(hoodTarget);
        
    }

    @Override
    // Responds to updates from inputs
    public void inputUpdate(Input source) {
        if (Math.abs(aimModeTrigger.getValue()) > 0.75) { // Entering aim mode
            aimModeEnabled = true;
            shooterMasterMotor.selectProfileSlot(1, 0);
        } else { // Exiting aim mode
            aimModeEnabled = false;
            shooterMasterMotor.selectProfileSlot(0, 0);
        } 
        if (source == hoodManualOverrideButton && hoodManualOverrideButton.getValue()) {
            //hoodManualOverride = !hoodManualOverride;
        } else if (source == hoodManualAdjustment) {
            if (hoodManualOverride == true) {
                hoodMotorOutput = hoodManualAdjustment.getValue();
            }
        } else if (source == hoodNudgeDownButton) {
            if (hoodNudgeDownButton.getValue() == true) {
                hoodRegAdjustmentCount -= 1;
            }
        } else if (source == hoodNudgeUpButton) {
            if (hoodNudgeUpButton.getValue() == true) {
                hoodRegAdjustmentCount += 1;
            }
        } else if (source == hoodEncoderResetButton) {
            if (hoodEncoderResetButton.getValue() == true) {
                hoodEncoderResetPressed = true;
                hoodEncoderResetTimestamp = System.currentTimeMillis();
            } else {
                hoodEncoderResetPressed = false;
                hoodEncoderResetTimestamp = Long.MAX_VALUE;
            }
        } else if(source == pointBlankShot) {
            if(pointBlankShot.getValue()) {
                aimModeEnabled = true;
                shooterMasterMotor.selectProfileSlot(1, 0);
                isPointBlank = true;
            } else {
                isPointBlank = false;
            }
        } else if(source == startButton) {
            if(startButton.getValue()) {
                if(!running) {
                    timer.reset();
                    running = true;
                }
                running = !selectButton.getValue();
            } else {
                running = false;
            }
        } 
        SmartDashboard.putBoolean("Hood Manual Override", hoodManualOverride);
        SmartDashboard.putNumber("Hood PID Adjust", hoodRegAdjustmentCount);
        autoMode = false;
    }

    @Override
    // Resets all variables to the default state
    public void resetState() {
        aimModeEnabled = false;
        shooterMasterMotor.selectProfileSlot(0, 0);
        shooterMotorSpeedSetForAimMode = false;
        hoodAimed = false;

        hoodManualOverride = false;
        hoodMotorOutput = 0.0;
        minimumHoodAdjustment = 0.0;
        isPointBlank = false;

        trailingHorizontalAngleOffsets = new ArrayList<Double>();
        lastValueAddedTimestamp = 0L;

        hoodEncoderResetPressed = false;
        hoodEncoderResetTimestamp = Long.MAX_VALUE;

        hoodRegAdjustmentCount = 0;

        hoodMotor.getSensorCollection().setAnalogPosition(0, -1);

        hoodEncoderOffset = 940.0;

        running = false;
        shooterOn = true;
        timer.start();
        autoMode = true;
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

    // Decides whether the inner goal is within range
    public boolean willAimToInnerGoal() {
        if (limelightSubsystem.getDistanceToTarget() < INNER_GOAL_MIN_DISTANCE) { // Is the robot too close to see the inner goal?
            SmartDashboard.putBoolean("Aiming Inner", false);
            return false; // Too close
        } else {
            if (limelightSubsystem.getTHorValue() / limelightSubsystem.getDistanceToTarget() < INNER_GOAL_STANDARD_RATIO - INNER_GOAL_THRESHOLD) { // Is the robot's angle to the target too far off?
                SmartDashboard.putBoolean("Aiming Inner", false);
                return false; // Angle too far off of target
            } else {
                SmartDashboard.putBoolean("Aiming Inner", true);
                return true; // Within +/- 15 degree bubble and far enough away
            }
        }


        // double horizontalAngleOffsetSum = 0.0;

        // for (int i = 0; i < trailingHorizontalAngleOffsets.size(); i++) {
        //     horizontalAngleOffsetSum += trailingHorizontalAngleOffsets.get(i);
        // }

        // double netHorizontalAngleOffset = horizontalAngleOffsetSum / (double) trailingHorizontalAngleOffsets.size();

        // if (netHorizontalAngleOffset <= 15.0 && netHorizontalAngleOffset >= -15.0) {
        //     return true;
        // } else {
        //     return false;
        // }
    }

    // Aims to either the inner or outer goal
    private void aimToGoal() {
        if (isPointBlank) {
            setHoodMotorPosition(POINTBLANK_HOOD);
        } else if (hoodManualOverride == false) {
            if (willAimToInnerGoal()) {
                hoodTravelDistance = (AIMING_INNER_REGRESSION_A * (Math.pow(limelightSubsystem.getDistanceToTarget(), 2))) 
                                        + (AIMING_INNER_REGRESSION_B * limelightSubsystem.getDistanceToTarget()) 
                                        + (AIMING_INNER_REGRESSION_C + (hoodRegAdjustmentCount * HOOD_REG_ADJUSTMENT_INCREMENT));

                setHoodMotorPosition(hoodTravelDistance); // replace later with ma3
                // hoodMotor.set(ControlMode.PercentOutput, 0.0);
            } else {
                hoodTravelDistance = (AIMING_INNER_REGRESSION_A * (Math.pow(limelightSubsystem.getDistanceToInnerGoal(), 2))) 
                                        + (AIMING_INNER_REGRESSION_B * limelightSubsystem.getDistanceToInnerGoal()) 
                                        + (AIMING_INNER_REGRESSION_C + (hoodRegAdjustmentCount * HOOD_REG_ADJUSTMENT_INCREMENT));
            
                //hoodMotor.set(ControlMode.Position, 0); replace later with ma3
                // hoodMotor.set(ControlMode.PercentOutput, 0.0);
                setHoodMotorPosition(hoodTravelDistance);
            }
        }
    }

    //Usable for auto
    public void setAim(boolean aiming){
        aimModeEnabled = aiming;
    }

    public void resetHoodEncoder() {
        //hoodEncoderOffset = hoodMotor.getSensorCollection().getAnalogInRaw();
        //setHoodMotorPosition(0.0); this doesn't seem to be needed since the absolute encoder hasn't failed yet
    }

    public double getHoodEncoderPosition() {
        double currentHoodEncoderValue = hoodMotor.getSensorCollection().getAnalogInRaw();
        return (currentHoodEncoderValue - hoodEncoderOffset + 1024) % 1024;
    }

    //Usable for auto
    public void setHoodMotorPosition(double position) {
        hoodTarget = position;

        if (Math.abs(hoodTarget-getHoodEncoderPosition())<=1){
            minimumHoodAdjustment = 0.0;
        } else if (hoodTarget > getHoodEncoderPosition()){
            minimumHoodAdjustment = 0.05;
        } else {
            minimumHoodAdjustment = -0.05;
        }
        if (hoodTarget>1000){//if it flashes from 0 to 1023, for instance
            hoodMotor.set(ControlMode.PercentOutput, (0 - getHoodEncoderPosition()) * -HOOD_KP + minimumHoodAdjustment);
        } else if (getHoodEncoderPosition()>1000){//if it flashes from 0 to 1023, for instance
            hoodMotor.set(ControlMode.PercentOutput, (hoodTarget - 0) * -HOOD_KP + minimumHoodAdjustment);
        } else{
            hoodMotor.set(ControlMode.PercentOutput, (hoodTarget - getHoodEncoderPosition()) * -HOOD_KP + minimumHoodAdjustment);
        }
    }

    //usable for auto
    public void autoOn(){
        autoMode = true;
    }

}