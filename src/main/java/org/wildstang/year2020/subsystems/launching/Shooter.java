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
<<<<<<< HEAD
    private AnalogInput fireTrigger;
    private DigitalInput pointBlankShot; 
=======

    private AnalogInput hoodManualAdjustment;
    private DigitalInput hoodManualOverrideButton;

    private DigitalInput hoodNudgeUpButton;
    private DigitalInput hoodNudgeDownButton;

    private DigitalInput hoodEncoderResetButton;
>>>>>>> 8af4aea46f187b4d860e6364c94aebd26ad3d114

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

    // Motor velocities in ticks per decisecond
    public static final double SAFE_SHOOTER_SPEED = (5000 * TICKS_PER_REV) / 600.0;//34133
    //public static final double SAFE_SHOOTER_SPEED = 8000;
    public static final double AIM_MODE_SHOOTER_SPEED = (6750 * TICKS_PER_REV) / 600.0;//51200

    // PID constants go in order of F, P, I, D
    public static final PIDConstants HOOD_PID_CONSTANTS = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    public static final PIDConstants SAFE_SHOOTER_PID_CONSTANTS = new PIDConstants(0.02, 0.024, 0.0, 0.0);//might push these P values way up
    public static final PIDConstants AIMING_SHOOTER_PID_CONSTANTS = new PIDConstants(0.8, 1.28, 0.0, 0.0);//same here // 0.02 0.032
    
    // TODO: More regression coefficients may be needed based on what regression type we choose to use
    public static final double AIMING_INNER_REGRESSION_A = 0.0;
    public static final double AIMING_OUTER_REGRESSION_A = 0.0;
    

    public static final double HOOD_OUTPUT_SCALE = 1.0;

    private static final double HOOD_REG_ADJUSTMENT_INCREMENT = 0.1;

    // Logic
    private boolean aimModeEnabled;

    private boolean shooterMotorSpeedSetForAimMode;
    private boolean hoodAimed;

    public List<Double> trailingHorizontalAngleOffsets;
    private long lastValueAddedTimestamp;

    private double hoodTravelDistance;
    private double hoodMotorOutput;
    private boolean hoodManualOverride;

    private boolean hoodEncoderResetPressed;
    private long hoodEncoderResetTimestamp;

    private double hoodEncoderOffset;

    // This counts how many times the incremental adjustment value should be added to the regression for hood position PID regression
    // Positive value -> upward adjustment, negative value -> downward adjustment, 0 -> no adjustment
    private int hoodRegAdjustmentCount;  

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
<<<<<<< HEAD
        fireTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_RIGHT);
        fireTrigger.addInputListener(this);
        pointBlankShot = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_LEFT);
        pointBlankShot.addInputListener(this);
=======
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
>>>>>>> 8af4aea46f187b4d860e6364c94aebd26ad3d114
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
        if (hoodManualOverride == true) {
            hoodMotor.set(ControlMode.PercentOutput, hoodMotorOutput * HOOD_OUTPUT_SCALE);
        }
        SmartDashboard.putNumber("Hood moving", hoodMotorOutput * HOOD_OUTPUT_SCALE);

        if (aimModeEnabled){
            shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
            aimToGoal();
        } else {
            shooterMasterMotor.set(ControlMode.Velocity, SAFE_SHOOTER_SPEED);
            //hoodMotor.set(ControlMode.Position, 0.0); replace later with ma3
            if (!hoodManualOverride){
                hoodMotor.set(ControlMode.PercentOutput, 0.0);
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
<<<<<<< HEAD
        } 
=======
        }

        if (hoodEncoderResetPressed == true && System.currentTimeMillis() >= hoodEncoderResetTimestamp + 1000L) {
            resetHoodEncoder();
        }
>>>>>>> 8af4aea46f187b4d860e6364c94aebd26ad3d114
    }

    @Override
    // Responds to updates from inputs
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (Math.abs(aimModeTrigger.getValue()) > 0.75) { // Entering aim mode
                aimModeEnabled = true;
                shooterMasterMotor.selectProfileSlot(1, 0);
<<<<<<< HEAD
                shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
                aimToGoal();
            } 
            else { // Exiting aim mode
=======
            } else { // Exiting aim mode
>>>>>>> 8af4aea46f187b4d860e6364c94aebd26ad3d114
                aimModeEnabled = false;
                shooterMasterMotor.selectProfileSlot(0, 0);
            }
        } else if (source == hoodManualOverrideButton && hoodManualOverrideButton.getValue()) {
            hoodManualOverride = !hoodManualOverride;
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
        }
<<<<<<< HEAD
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
=======

        SmartDashboard.putBoolean("Hood Manual Override", hoodManualOverride);
        SmartDashboard.putNumber("Hood PID Adjust", hoodRegAdjustmentCount);
>>>>>>> 8af4aea46f187b4d860e6364c94aebd26ad3d114
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

        trailingHorizontalAngleOffsets = new ArrayList<Double>();
        lastValueAddedTimestamp = 0L;

        hoodEncoderResetPressed = false;
        hoodEncoderResetTimestamp = Long.MAX_VALUE;

        hoodRegAdjustmentCount = 0;

        hoodMotor.getSensorCollection().setAnalogPosition(0, -1);

        hoodEncoderOffset = 940.0;
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
    public boolean willAimToInnerGoal() {
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
        if (hoodManualOverride == false) {
            if (willAimToInnerGoal()) {
                hoodTravelDistance = AIMING_INNER_REGRESSION_A + (hoodRegAdjustmentCount * HOOD_REG_ADJUSTMENT_INCREMENT); // TODO: Perform regression calculation

                setHoodMotorPosition(hoodTravelDistance * TICKS_PER_INCH); // replace later with ma3
                // hoodMotor.set(ControlMode.PercentOutput, 0.0);
            } else {
                hoodTravelDistance = AIMING_OUTER_REGRESSION_A + (hoodRegAdjustmentCount * HOOD_REG_ADJUSTMENT_INCREMENT); // TODO: Perform regression calculation
            
                //hoodMotor.set(ControlMode.Position, 0); replace later with ma3
                // hoodMotor.set(ControlMode.PercentOutput, 0.0);
                setHoodMotorPosition(hoodTravelDistance * TICKS_PER_INCH);
            }
        }
    }
    public void setAutonShooterSpeed(){
        shooterMasterMotor.selectProfileSlot(1, 0);
        shooterMasterMotor.set(ControlMode.Velocity, AIM_MODE_SHOOTER_SPEED);
    }
    public void setHoodPosition(double position){ // Fix name
        if (hoodManualOverride == false) {
            setHoodMotorPosition(position);
        }
    }
    public void setAim(){
        aimToGoal();
    }

    public void resetHoodEncoder() {
        hoodEncoderOffset = hoodMotor.getSensorCollection().getAnalogInRaw();
        setHoodMotorPosition(0.0);
    }

    public double getHoodEncoderPosition() {
        double currentHoodEncoderValue = hoodMotor.getSensorCollection().getAnalogInRaw();
        return (currentHoodEncoderValue - hoodEncoderOffset + 1024) % 1024;
    }

    public void setHoodMotorPosition(double position) {
        hoodMotor.set(ControlMode.Position, (position + hoodEncoderOffset + 1024) % 1024);
    }

}