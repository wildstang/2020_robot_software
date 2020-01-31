package org.wildstang.year2020.subsystems.launching;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSSubsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret implements Subsystem {

    // Inputs
    private AnalogInput aimModeTrigger;

    // Outputs
    private TalonSRX turretMotor;
    private Limelight limelightSubsystem;
    private Shooter shooterSubsystem;

    // Constants
    public static final double kP = -0.03;
    public static final double minimumAdjustmentCommand = 0.05;

    // Logic
    private boolean aimModeEnabled;
    private boolean turretAimed;

    @Override
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (aimModeTrigger.getValue() > 0.75) { // Entering aim mode
                aimModeEnabled = true;
            } else { // Exiting aim mode
                aimModeEnabled = false;
                turretAimed = false;
            }
        }
    }

    @Override
    public void init() {
        aimModeEnabled = false;
        turretAimed = false;

        aimModeTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.TURRET_AIM_MODE_TRIGGER);
        aimModeTrigger.addInputListener(this);

        turretMotor = new TalonSRX(CANConstants.TURRET_MOTOR);

        // BELOW IS IMPORTED FROM 2019 LIFT -- MAY NOT BE APPLICABLE TO THIS YEAR'S CODE
        turretMotor.setInverted(false);
        turretMotor.setSensorPhase(true);
        turretMotor.configNominalOutputForward(0, 0);
        turretMotor.configNominalOutputReverse(0, 0);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT.getName());
        shooterSubsystem = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName());
    }

    @Override
    public void update() {
        if (aimModeEnabled == true) {
            double tyValue = limelightSubsystem.getTYValue();

            double headingError = -tyValue;
            double rotationalAdjustment = 0.0;

            if (tyValue > 1.0) {
                rotationalAdjustment = kP * headingError - minimumAdjustmentCommand;
            } else if (tyValue < 1.0) {
                rotationalAdjustment = kP * headingError + minimumAdjustmentCommand;
            }

            turretMotor.set(ControlMode.PercentOutput, rotationalAdjustment);
        } else {
            turretMotor.set(ControlMode.PercentOutput, 0);
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
    public void resetState() {
        aimModeEnabled = false;
    }

    @Override
    public String getName() {
        return "Turret";
    }
    
    @Override
    public void selfTest() {}
}