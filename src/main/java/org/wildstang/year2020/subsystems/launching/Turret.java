package org.wildstang.year2020.subsystems.launching;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSSubsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret implements Subsystem {

    // Inputs
    private AnalogInput aimModeTrigger;
    private DigitalInput backPositionButton;
    private DigitalInput frontPositionButton;

    // Outputs
    private TalonSRX turretMotor;
    private TalonSRX kickerMotor;
    private Limelight limelightSubsystem;
    private Shooter shooterSubsystem;

    // Constants
    public static final double kP = -0.07;
    public static final double minimumAdjustmentCommand = 0.05;

    public static final double REVS_PER_INCH = 1.0 / 2.0;
    public static final double TICKS_PER_REV = 4096.0;
    public static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;

    public static final double TURRET_BASE_CIRCUMFERENCE = 8.0;

    // Logic
    private boolean aimModeEnabled;
    private boolean turretAimed;
    private double lastSetpoint;

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
        backPositionButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT);
        backPositionButton.addInputListener(this);
        frontPositionButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT);
        frontPositionButton.addInputListener(this);
    }

    // Initializes outputs
    private void initOutputs() {
        turretMotor = new TalonSRX(CANConstants.TURRET_TALON);

        kickerMotor = new TalonSRX(11);
        kickerMotor.set(ControlMode.PercentOutput, -1.0);
        
        // BELOW IS IMPORTED FROM 2019 LIFT -- MAY NOT BE APPLICABLE TO THIS YEAR'S CODE
        turretMotor.setInverted(false);
        turretMotor.setSensorPhase(true);
        turretMotor.configNominalOutputForward(0, 0);
        turretMotor.configNominalOutputReverse(0, 0);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT.getName());
        shooterSubsystem = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName());
    }

    @Override
    // Responds to updates from inputs
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (aimModeTrigger.getValue() > 0.75) { // Entering aim mode
                aimModeEnabled = true;
                lastSetpoint = turretMotor.getSelectedSensorPosition();
            } else { // Exiting aim mode
                aimModeEnabled = false;
                turretAimed = false;
                turretMotor.set(ControlMode.Position, lastSetpoint);
            }
        }

        if (source == backPositionButton) {
            if (backPositionButton.getValue() == true) {
                turretMotor.set(ControlMode.Position, 0.0);
            }
        }

        if (source == frontPositionButton) {
            if (frontPositionButton.getValue() == true) {
                turretMotor.set(ControlMode.Position, (TURRET_BASE_CIRCUMFERENCE / 2.0) * TICKS_PER_INCH);
            }
        }
    }

    @Override
    // Updates the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        if (aimModeEnabled == true) {
            double tyValue = limelightSubsystem.getTYValue() - 0.8;

            SmartDashboard.putNumber("Adjusted TY", tyValue);

            double headingError = -tyValue;
            double rotationalAdjustment = 0.0;

            if (Math.abs(tyValue) > 1.0) { // Pull Turret in, not close enough
                rotationalAdjustment = kP * headingError;
            } else if (Math.abs(tyValue) < 0.1) { // Do nothing, we're close enough
                rotationalAdjustment = 0.0;
            } else if (tyValue < 1.0 && tyValue > 0.0) { // Keep pulling Turret in, almost there
                rotationalAdjustment = kP * headingError + minimumAdjustmentCommand;
            } else if (tyValue < 0.0 && tyValue > -1.0) { // Keep pulling Turret in, almost there
                rotationalAdjustment = kP * headingError - minimumAdjustmentCommand;
            }

            SmartDashboard.putNumber("Rotational Adjustment", rotationalAdjustment);

            turretMotor.set(ControlMode.PercentOutput, rotationalAdjustment);
        } else {
            turretMotor.set(ControlMode.PercentOutput, 0.0);
        }

        boolean hoodAimed = shooterSubsystem.isHoodAimed();
        boolean shooterMotorSpeedSet = shooterSubsystem.isShooterMotorSpeedSetForAimMode();
        if (hoodAimed && shooterMotorSpeedSet && turretAimed) {
            SmartDashboard.putBoolean("Shooter Ready", true);
        } else {
            SmartDashboard.putBoolean("Shooter Ready", false);
        }
    }

    @Override
    // Resets all variables to the default state
    public void resetState() {
        aimModeEnabled = false;
        turretAimed = false;
        lastSetpoint = 0.0;
    }

    @Override
    // Returns the subsystem's name
    public String getName() {
        return "Turret";
    }
    
    @Override
    // Tests the subsystem (unimplemented right now)
    public void selfTest() {}

}