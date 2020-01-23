package org.wildstang.year2020.subsystems.launching;

import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSSubsystems;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.subsystems.Subsystem;

/**
 * Class:       TestSubsystem.java
 * Inputs:      1 joystick
 * Outputs:     1 talon
 * Description: This is a testing subsystem that controls a motor with a joystick
 */
public class Shooter implements Subsystem {

    // Inputs
    

    // Outputs
    private TalonSRX shooterMasterMotor;
    private VictorSPX shooterFollowerMotor;

    private VictorSPX hoodMotor;

    private Limelight limelightSubsystem;

    // Constants
    public static final double safeShooterMotorOutput = 0.5;
    public static final double aimModeShooterMotorOutput = 0.8;

    public static final double kP = -0.1;
    public static final double minimumAdjustmentCommand = 0.05;

    // Logic
    private double shooterMotorOutput;
    private boolean aimModeEnabled;
    private boolean aimModeShooterMotorSpeedSet;
    private boolean hoodAimed;

    // initializes the subsystem
    public void init() {
        aimModeEnabled = false;

        shooterMotorOutput = safeShooterMotorOutput;

        shooterMasterMotor = new TalonSRX(0);
        // Setup carap

        shooterFollowerMotor = new VictorSPX(0);
        shooterFollowerMotor.follow(shooterMasterMotor);

        hoodMotor = new VictorSPX(0);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT);
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        if (aimModeEnabled == true) {
            // Hood aiming
            double txValue = limelightSubsystem.getTXValue();

            double headingError = -txValue;
            double rotationalAdjustment = 0.0;

            if (txValue > 1.0) {
                rotationalAdjustment = kP * headingError - minimumAdjustmentCommand;
            } else if (txValue < 1.0) {
                rotationalAdjustment = kP * headingError + minimumAdjustmentCommand;
            }

            hoodMotor.set(ControlMode.PercentOutput, rotationalAdjustment);

            // Shooter motor speed check
            double currentOutput = shooterMasterMotor.getMotorOutputPercent();
            if (currentOutput < aimModeShooterMotorOutput + 0.02 && currentOutput > aimModeShooterMotorOutput - 0.02) {
                aimModeShooterMotorSpeedSet = true;
            } else {
                aimModeShooterMotorSpeedSet = false;
            }
        }

        shooterMasterMotor.set(ControlMode.PercentOutput, shooterMotorOutput);
    }

    // respond to input updates
    public void inputUpdate(Input signal) {
        
    }

    // used for testing
    public void selfTest() {

    }

    // resets all variables to the default state
    public void resetState() {
        
    }

    // returns the unique name of the example
    public String getName() {
        return "Shooter";
    }

    public void enableAimMode() {
        shooterMotorOutput = aimModeShooterMotorOutput;

        aimModeEnabled = true;
    }

    public void disableAimMode() {
        shooterMotorOutput = safeShooterMotorOutput;

        aimModeEnabled = false;
    }

    public boolean getAimModeShooterMotorSpeedSet() {
        return aimModeShooterMotorSpeedSet;
    }

    public boolean getHoodAimed() {
        return hoodAimed;
    }

}