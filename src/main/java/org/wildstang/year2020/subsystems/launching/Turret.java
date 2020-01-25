package org.wildstang.year2020.subsystems.launching;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSSubsystems;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret implements Subsystem {

    // Inputs
    private DigitalInput aimModeTrigger;

    // Outputs
    private TalonSRX turretMotor;
    private Limelight limelightSubsystem;
    private Shooter shooterSubsystem;

    // Constants
    public static final double kP = -0.1;
    public static final double minimumAdjustmentCommand = 0.05;

    // Logic
    private boolean aimModeEnabled;
    private boolean turretAimed;

    @Override
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (aimModeTrigger.getValue() == true) { // Entering aim mode
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

        aimModeTrigger = (DigitalInput) Core.getInputManager().getInput(WSInputs.TURRET_AIM_MODE_TRIGGER);

        turretMotor = new TalonSRX(CANConstants.TURRET_MOTOR);

        // BELOW IS IMPORTED FROM 2019 LIFT -- MAY NOT BE APPLICABLE TO THIS YEAR'S CODE
        turretMotor.setInverted(false);
        turretMotor.setSensorPhase(true);
        turretMotor.configNominalOutputForward(0, 0);
        turretMotor.configNominalOutputReverse(0, 0);

        limelightSubsystem = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT);
        shooterSubsystem = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        if (aimModeEnabled == true) {
            double txValue = limelightSubsystem.getTXValue();

            double headingError = -txValue;
            double rotationalAdjustment = 0.0;

            if (txValue > 1.0) {
                rotationalAdjustment = kP * headingError - minimumAdjustmentCommand;
            } else if (txValue < 1.0) {
                rotationalAdjustment = kP * headingError + minimumAdjustmentCommand;
            }

            turretMotor.set(ControlMode.PercentOutput, rotationalAdjustment);
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
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        return "Turret";
    }
    
}