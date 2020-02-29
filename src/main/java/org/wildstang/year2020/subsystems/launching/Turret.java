package org.wildstang.year2020.subsystems.launching;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.*;
import com.kauailabs.navx.frc.AHRS;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.pid.PIDConstants;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSSubsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.I2C;
// import edu.wpi.first.wpilibj.interfaces.*; 

public class Turret implements Subsystem {

    // Inputs
    private AnalogInput aimModeTrigger;
    private DigitalInput backPositionButton;
    private DigitalInput frontPositionButton;
    private DigitalInput sidePositionButton;
    // private DigitalInput faceWall; 
    private AnalogInput manualTurret;
    // private static AHRS gyroSensor;
    // private static I2C.Port gyroPort;
    private DigitalInput turretEncoderResetButton;

    // Outputs
    private TalonSRX turretMotor;
    private Limelight limelightSubsystem;
    private Shooter shooterSubsystem;
    public static final PIDConstants TURRET_PID_CONSTANTS = new PIDConstants(0.0, 0.15, 0.0, 0.1); // 0.0 0.3 0.0 0.1


    // Constants
    public static final double kP = -0.05; // -.07
    public static final double minimumAdjustmentCommand = 0.025; // 0.05

    public static final double REVS_PER_INCH = 1.0 / 2.0;
    public static final double TICKS_PER_REV = 4096.0;
    public static final double TICKS_PER_INCH = 108.8;//TICKS_PER_REV * REVS_PER_INCH;     

    public static final double TURRET_BASE_CIRCUMFERENCE = 8.0;

    // public static final double TICK_PER_DEGREE = (TICKS_PER_INCH * TURRET_BASE_CIRCUMFERENCE) / 360;
    public static final double TICK_PER_DEGREE = 108.89;

    // Logic
    private boolean aimModeEnabled;
    // private boolean wallTracking;  
    private boolean turretAimed;
    private double lastSetpoint;
    private double manualSpeed;
    // private double wallDirection; 
    private double turretTarget;

    private boolean turretEncoderResetPressed;
    private long turretEncoderResetTimestamp;

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
        backPositionButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        backPositionButton.addInputListener(this);
        frontPositionButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        frontPositionButton.addInputListener(this);
        sidePositionButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT);
        sidePositionButton.addInputListener(this);
        manualTurret = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_JOYSTICK_X);
        manualTurret.addInputListener(this);
        // faceWall = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_RIGHT);
        // faceWall.addInputListener(this);
        turretEncoderResetButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_LEFT);
        turretEncoderResetButton.addInputListener(this);

        // gyroSensor = new AHRS(gyroPort  );
    }

    // Initializes outputs
    private void initOutputs() {
        turretMotor = new TalonSRX(CANConstants.TURRET_TALON);

        // BELOW IS IMPORTED FROM 2019 LIFT -- MAY NOT BE APPLICABLE TO THIS YEAR'S CODE
        turretMotor.setInverted(false);
        turretMotor.setSensorPhase(false);
        turretMotor.configNominalOutputForward(0, 0);
        turretMotor.configNominalOutputReverse(0, 0);
        turretMotor.configPeakCurrentLimit(10);
        turretMotor.config_kF(0, TURRET_PID_CONSTANTS.f);
        turretMotor.config_kP(0, TURRET_PID_CONSTANTS.p);
        turretMotor.config_kI(0, TURRET_PID_CONSTANTS.i);
        turretMotor.config_kD(0, TURRET_PID_CONSTANTS.d);
        turretMotor.setNeutralMode( NeutralMode.Coast);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT.getName());
        shooterSubsystem = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName());

    }

    @Override
    // Responds to updates from inputs
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (Math.abs(aimModeTrigger.getValue()) > 0.75) { // Entering aim mode
                turretTarget = turretMotor.getSelectedSensorPosition();
                aimModeEnabled = true;
                turretMotor.setNeutralMode(NeutralMode.Brake);
            } else { // Exiting aim mode
                aimModeEnabled = false;
                turretMotor.setNeutralMode(NeutralMode.Coast);
                turretAimed = false;
                // if (!wallTracking) {
                    turretMotor.set(ControlMode.Position, lastSetpoint);
                // }
            }
        }

        if (source == backPositionButton) {
            if (backPositionButton.getValue()) {
                turretTarget = -29400;
            }
        }
        if (source == sidePositionButton){
            if (sidePositionButton.getValue()){
                turretTarget = -19600;
            }
        }

        if (source == frontPositionButton) {
            if (frontPositionButton.getValue()) {
                turretTarget = -9800;
                // turretTarget = (TURRET_BASE_CIRCUMFERENCE / 2.0) * TICKS_PER_INCH;
            }
        }

        // if (source == faceWall) {
        //     if(faceWall.getValue()) {
        //         wallTracking = true;
        //     }
        //     else {
        //         wallTracking = false;
        //     }
        // }

        if (Math.abs(manualTurret.getValue())>0.25){
            if (!frontPositionButton.getValue() && !backPositionButton.getValue() && !aimModeEnabled){
                manualSpeed = manualTurret.getValue();
            } 
        }else {
            manualSpeed = 0.0;
        }

        if (source == turretEncoderResetButton) {
            if (turretEncoderResetButton.getValue() == true) {
                turretEncoderResetPressed = true;
                turretEncoderResetTimestamp = System.currentTimeMillis();
            } else {
                turretEncoderResetPressed = false;
                turretEncoderResetTimestamp = Long.MAX_VALUE;
            }
        }
    }

    @Override
    // Updates the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        SmartDashboard.putNumber("Adjusted TX", limelightSubsystem.getTXValue() - 0.8);
        SmartDashboard.putBoolean("Aim Mode Enabled", aimModeEnabled);

        if (aimModeEnabled == true) {
            double txValue = 0.0;
            // if (shooterSubsystem.willAimToInnerGoal() == true) { // Do we need to adjust the turret aiming angle?
            //     txValue = Math.toDegrees(Math.atan(limelightSubsystem.getTHorValue() / limelightSubsystem.getDistanceToInnerGoal())) - 0.8;
            // } else {
                txValue = limelightSubsystem.getTXValue() - 1.8;
            // }

            double headingError = -txValue;
            // if (shooterSubsystem.willAimToInnerGoal()){
            //     TODO: calculate the new offset from the middle of the outer goal to the middle of the inner goal
            //     double horizontalAngleOffsetSum = 0.0;
            //     for (int i = 0; i < trailingHorizontalAngleOffsets.size(); i++) {
            //         horizontalAngleOffsetSum += trailingHorizontalAngleOffsets.get(i);
            //     }
            //     double netHorizontalAngleOffset = horizontalAngleOffsetSum / (double) trailingHorizontalAngleOffsets.size();
            //     tyValue += some math involving netHorizontalAngleOffset
            // }
            double rotationalAdjustment = 0.0;

            if (Math.abs(txValue) > 1.0) { // Pull Turret in, not close enough
                rotationalAdjustment = kP * headingError;
            } else if (Math.abs(txValue) < 0.1) { // Do nothing, we're close enough
                rotationalAdjustment = 0.0;
            } else if (txValue < 1.0 && txValue > 0.0) { // Keep pulling Turret in, almost there
                rotationalAdjustment = kP * headingError + minimumAdjustmentCommand;
            } else if (txValue < 0.0 && txValue > -1.0) { // Keep pulling Turret in, almost there
                rotationalAdjustment = kP * headingError - minimumAdjustmentCommand;
            }

            

            SmartDashboard.putNumber("Rotational Adjustment", rotationalAdjustment);

            turretMotor.set(ControlMode.PercentOutput, rotationalAdjustment);


        } // else if (wallTracking && !aimModeEnabled) { // End of aim mode; start of wall tracking
        //     if (gyroSensor.getAngle() < 180) {
        //          wallDirection = 90 + gyroSensor.getAngle();    
        //     }
        //     if(gyroSensor.getAngle() > 270) {
        //          wallDirection = gyroSensor.getAngle() - 270;
        //     }
            
        //     turretMotor.set(ControlMode.Position, wallDirection*TICK_PER_DEGREE);
        // }  // End of wall tracking; start of manual control
        else {
            turretTarget += TICKS_PER_INCH * manualSpeed * 135 / 50.0;

          
            turretMotor.set(ControlMode.Position, turretTarget);
            SmartDashboard.putBoolean("Deadzone Warning", true);
            
        } // End of manual control


        SmartDashboard.putNumber("Turret PID target", turretTarget);
        SmartDashboard.putNumber("Turret encoder", turretMotor.getSensorCollection().getQuadraturePosition());
        
        SmartDashboard.putNumber("turret raw", turretMotor.getMotorOutputPercent());

        boolean hoodAimed = shooterSubsystem.isHoodAimed();
        boolean shooterMotorSpeedSet = shooterSubsystem.isShooterMotorSpeedSetForAimMode();

        if (hoodAimed && shooterMotorSpeedSet && turretAimed) {
            SmartDashboard.putBoolean("Shooter Ready", true);
        } else {
            SmartDashboard.putBoolean("Shooter Ready", false);
        }

        if (turretEncoderResetPressed == true && System.currentTimeMillis() >= turretEncoderResetTimestamp + 1000L) {
            turretMotor.getSensorCollection().setQuadraturePosition(0, -1);
            turretTarget = 0.0;
            //turretMotor.set(ControlMode.Position, turretTarget);
        }  //end of encoder reset
    }

    @Override
    // Resets all variables to the default state
    public void resetState() {
        aimModeEnabled = false;
        turretAimed = false;
        lastSetpoint = 0.0;
        manualSpeed = 0;
        //gyroSensor.reset();
        turretTarget = 0.0;
        turretMotor.getSensorCollection().setQuadraturePosition(0,-1);
        turretEncoderResetPressed = false;
        turretEncoderResetTimestamp = Long.MAX_VALUE;
    }

    @Override
    // Returns the subsystem's name
    public String getName() {
        return "Turret";
    }
    
    @Override
    // Tests the subsystem (unimplemented right now)
    public void selfTest() {}

    //usable for auto
    public void autoAim(boolean parameter){
        aimModeEnabled = parameter;
    }
    //usable for auto
    public void setTarget(double newTarget){
        turretTarget = newTarget;
    }

}