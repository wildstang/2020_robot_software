package org.wildstang.year2019.subsystems.lift;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2019.robot.CANConstants;
import org.wildstang.year2019.robot.WSInputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * Another attempt at writing a lift.
 * 
 * Design principles in this iteration:
 *  - All state change and motor manipulation is in setStopped(),
 *    setManualPower(), setTrackingTarget(), and resetState(). No other
 *    code touches currentCommand or sets the motor behavior.
 *  - inputUpdate() is short as possible and focuses on calling out to other
 *    methods that do the work.
 *  - Tried to not implement anything that doesn't work. That's not quite true ---
 *    there's some code for limit and PID override, neither of which do anything.
 *    But for example, 
 *    there's no limit switch support, b/c the robot does not have limit switches.
 */
public class LiftMark3 implements Subsystem {

    ////////////////////////////////////////////////////////////////////////////
    // CONFIGURATION CONSTANTS

    /** Position of low goal (inches above lower limit) */
    private static final double POSITION_1 = 0.0;
    /** Position of cargo goal (cargo only) (inches above lower limit) */
    private static final double POSITION_2 = 10.62;
    /** Position of mid rocket goal (inches above lower limit) */
    private static final double POSITION_3 = 22.0;
    /** Position of high rocket goal (inches above lower limit) */
    private static final double POSITION_4 = 44;

    /** # of rotations of encoder in one inch of axis travel */
    private static final double REVS_PER_INCH = 1 / 9.087;
    /** Number of encoder ticks in one revolution */
    private static final double TICKS_PER_REV = 4096;
    /** # of ticks in one inch of axis movement */
    private static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;

    private static final boolean INVERTED = false;
    private static final boolean SENSOR_PHASE = true;

    /** Deadband on manual lift control stick. Units are fraction of full deflection. */
    private static final double MANUAL_DEADBAND = .2;

    /** Approximate constant upward force needed to hold the lift against gravity.
     * Units are fraction of max power. This is used to make the lift a little more even
     * in manual control. */
    private static final double HOLDING_POWER = .2;

    /** Gain on manual control */
    private static final double MANUAL_GAIN = 0.3;

    ////////////////////////////////////////////////////////////////////////////
    // PRIVATE DATA MEMBERS

    private DigitalInput position1Button;
    private DigitalInput position2Button;
    private DigitalInput position3Button;
    private DigitalInput position4Button;

    /**
     * This input is used by the manipulator controller to fine-tune the axis
     * position.
     */
    public AnalogInput manualAdjustmentJoystick;
    // General button references for Lift and StrafeAxis to use for overrides
    public DigitalInput overrideButtonModifier;
    public DigitalInput limitSwitchOverrideButton;
    public DigitalInput pidOverrideButton;

    private TalonSRX motor;
    private VictorSPX follower;

    private boolean isLimitSwitchOverridden;
    private boolean isPIDOverridden;

    /** Enum of all control modes we can be in. */
    private enum Command {
        TRACK, MANUAL, STOPPED;
    }
    /** Control mode we're currently operating under. */
    Command currentCommand;

    ////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS

    public void init() {
        initMotors();
        initInputs();
        resetState();
    }

    public void inputUpdate(Input source) {
        if (source == position1Button) {
            if (position1Button.getValue()) {
                setTrackingTarget(POSITION_1);
            }
        } else if (source == position2Button) {
            if (position2Button.getValue()) {
                setTrackingTarget(POSITION_2);
            }
        } else if (source == position3Button) {
            if (position3Button.getValue()) {
                setTrackingTarget(POSITION_3);
            }
        } else if (source == position4Button) {
            if (position4Button.getValue()) {
                setTrackingTarget(POSITION_4);
            }
        }

        if (source == manualAdjustmentJoystick) {
            double commandOutput = deadband(manualAdjustmentJoystick.getValue(), MANUAL_DEADBAND);
            if (commandOutput != 0) {
                setManualPower(commandOutput);
            } else if (currentCommand == Command.MANUAL) {
                setStopped();
            }
        } else if (source == pidOverrideButton) {
            if (pidOverrideButton.getValue() && overrideButtonModifier.getValue()) {
                togglePIDOverride();
            }
        } else if (source == limitSwitchOverrideButton) {
            if (limitSwitchOverrideButton.getValue() && overrideButtonModifier.getValue()) {
                toggleLimitSwitchOverride();
            }
        }
    }

    public void selfTest() {
        // TODO: actually selftest?
    }

    public void update() {
        SmartDashboard.putNumber("Lift Encoder Value", motor.getSelectedSensorPosition());
        SmartDashboard.putNumber("Lift Voltage", motor.getMotorOutputVoltage());
        SmartDashboard.putString("Current Command", currentCommand.name());
    }

    public void resetState() {
        motor.set(ControlMode.PercentOutput, 0);
        currentCommand = Command.TRACK;
    }

    public String getName() {
        return "LiftMark3";
    }

    ///////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS

    private void initMotors() {
        motor = new TalonSRX(CANConstants.LIFT_TALON);

        // Basic motor settings
        motor.setInverted(INVERTED);
        // Set up the encoder on the motor
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        motor.setSensorPhase(SENSOR_PHASE);

        for (LiftPID constants : LiftPID.values()) {
            motor.config_kF(constants.slot, constants.k.f, 0);
            motor.config_kP(constants.slot, constants.k.p, 0);
            motor.config_kI(constants.slot, constants.k.i, 0);
            motor.config_kD(constants.slot, constants.k.d, 0);
        }

        follower = new VictorSPX(CANConstants.LIFT_VICTOR);
        follower.setInverted(INVERTED);
        follower.follow(motor);
        follower.setNeutralMode(NeutralMode.Brake);
    }

    private void initInputs() {
        IInputManager inputManager = Core.getInputManager();
        position1Button = (DigitalInput) inputManager.getInput(WSInputs.LIFT_PRESET_1);
        position1Button.addInputListener(this);
        position2Button = (DigitalInput) inputManager.getInput(WSInputs.LIFT_PRESET_2);
        position2Button.addInputListener(this);
        position3Button = (DigitalInput) inputManager.getInput(WSInputs.LIFT_PRESET_3);
        position3Button.addInputListener(this);
        position4Button = (DigitalInput) inputManager.getInput(WSInputs.LIFT_PRESET_4);
        position4Button.addInputListener(this);
        manualAdjustmentJoystick = (AnalogInput) inputManager.getInput(WSInputs.LIFT_MANUAL);
        manualAdjustmentJoystick.addInputListener(this);
        overrideButtonModifier = (DigitalInput) inputManager.getInput(WSInputs.WEDGE_SAFETY_2);
        overrideButtonModifier.addInputListener(this);
        pidOverrideButton = (DigitalInput) inputManager.getInput(WSInputs.HATCH_COLLECT);
        pidOverrideButton.addInputListener(this);
        limitSwitchOverrideButton = (DigitalInput) inputManager.getInput(WSInputs.LIFT_LIMIT_SWITCH_OVERRIDE);
        limitSwitchOverrideButton.addInputListener(this);
    }

    /** Set the lift to start tracking towards the given position.
     * 
     * @param target The position (in inches) towards which the lift should track.
     */
    private void setTrackingTarget(double target) {
        currentCommand = Command.TRACK;
        // If we're going downward, use the down PID. If we're going upward, use the up PID.
        double targetTicks = target * TICKS_PER_INCH;
        boolean goingDown = motor.getSelectedSensorPosition() > targetTicks;
        SmartDashboard.putBoolean("Is Down", goingDown);
        SmartDashboard.putNumber("Target", target);
        if (goingDown) {
            motor.selectProfileSlot(LiftPID.DOWNTRACK.slot, 0);
        } else {
            motor.selectProfileSlot(LiftPID.TRACKING.slot, 0);
        }
        motor.set(ControlMode.Position, targetTicks);
    }

    /** Sets manual control of the lift
     * 
     * @param power The fraction of maximum motor exertion to apply to the lift.
    */
    private void setManualPower(double power) {
        currentCommand = Command.MANUAL;
        double output = HOLDING_POWER + power * MANUAL_GAIN;
        SmartDashboard.putNumber("Lift manual output", output);
        motor.set(ControlMode.PercentOutput, output);
    }

    /** Set lift to be manually stopped.
     */
    private void setStopped() {
        currentCommand = Command.STOPPED;
        double positionTicks = motor.getSelectedSensorPosition();
        motor.selectProfileSlot(LiftPID.HOMING.slot, 0);
        motor.set(ControlMode.Position, positionTicks);
        SmartDashboard.putNumber("Target", positionTicks / TICKS_PER_INCH);
    }

    /** Compute deadbanded value
     * TODO move this helper somewhere sensible 
     * 
     * @param value Joystick input value to deadband
     * @param deadband Minimum input value that will translate to a nonzero output
     */
    private static double deadband(double value, double deadband) {
        double gain = 1.0 / (1.0 - deadband);
        double deadbanded_value;
        if (value > deadband) {
            deadbanded_value = (value - deadband) * gain;
        } else if (value < deadband) {
            deadbanded_value = (value + deadband) * gain;
        } else {
            deadbanded_value = 0;
        }
        return deadbanded_value;
    }

    private void toggleLimitSwitchOverride() {
        isLimitSwitchOverridden = !isLimitSwitchOverridden;
        if (isLimitSwitchOverridden) {
            //motor.configForwardLimitSwitchSource(type, normalOpenOrClose);
            System.out.println("WARNING! LIMIT SWITCH DISABLE NOT IMPLEMENTED!");
            // TODO actually suppress limit switch
        }
    }

    private void togglePIDOverride() {
        isPIDOverridden = !isPIDOverridden;
        if (isPIDOverridden) {
            System.out.println("WARNING! PID OVERRIDE NOT IMPLEMENTED!");
            // TODO actually suppress PID?
        }
    }
}
