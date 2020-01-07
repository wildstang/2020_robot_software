package org.wildstang.year2019.subsystems.lift;


import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
//import org.wildstang.framework.CoreUtils.CTREException;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2019.robot.CANConstants;
import org.wildstang.year2019.robot.WSInputs;
import org.wildstang.year2019.subsystems.common.Axis;
import org.wildstang.year2019.subsystems.lift.LiftPID;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * This subsystem goes up and down and puts hatches on holes.
 * 
 * Because this year's lift is continuous and not staged, the PID constants do
 * not need to change when the lift moves up and down.
 * 
 * This lift has no brake. There will be springs canceling out the weight of the
 * lift, making PID control alone sufficient.
 * 
 * Because the hatch injection mechanism and the lift are somewhat coupled, this
 * one subsystem is responsible for both. Hatch-specific code goes in
 * Hatch.java?
 * 
 * Sensors:
 * <ul>
 * <li>Limit switch(es). TODO: top, bottom or both?
 * <li>Encoder on lift Talon.
 * <li>pneumatic pressure sensor.
 * </ul>
 * 
 * Actuators:
 * <ul>
 * <li>Talon driving lift.
 * <li>Piston solenoids for hatch mechanism TODO detail here.
 * </ul>
 * 
 */
public class Lift extends Axis implements Subsystem {

    private static final boolean INVERTED = false;
    private static final boolean SENSOR_PHASE = true;



    // All positions in inches above lower limit
    //position_1+28=position_3
    //position_3+28=position_4
    private static double POSITION_1 = 0.0;//low goal
    private static double POSITION_2 = 8.0;//cargo goal - cargo only
    private static double POSITION_3 = 2.4228;//mid goal
    private static double POSITION_4 = 6.0;//high goal

    /** # of rotations of encoder in one inch of axis travel */
    private static final double REVS_PER_INCH = 5.092;                                                   
    /** Number of encoder ticks in one revolution */
    private static final double TICKS_PER_REV = 1024; 
    /** # of ticks in one inch of axis movement */
    private static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;

    /** The maximum speed the operator can command to move in fine-tuning */
    private static final double MANUAL_SPEED = 2; // in/s
    private static final double TRACKING_MAX_SPEED = 1; // in/s
    private static final double TRACKING_MAX_ACCEL = 1; // in/s^2
    private static final double HOMING_MAX_SPEED = 1; // in/s
    private static final double HOMING_MAX_ACCEL = 1; // in/s^2

    private static final double BOTTOM_STOP_POS = -.5;
    private static final double BOTTOM_MAX_TRAVEL = 0;
    private static final double TOP_MAX_TRAVEL = 40;

    private DigitalInput position1Button;
    private DigitalInput position2Button;
    private DigitalInput position3Button;
    private DigitalInput position4Button;

    private TalonSRX motor;
    private VictorSPX follower;

    /** The axis configuration we pass up to the axis initialization */
    private AxisConfig axisConfig = new AxisConfig();

    // Logical variables

    @Override
    public void inputUpdate(Input source) {
        super.inputUpdate(source);
        
        if (source == position1Button) {
            if (position1Button.getValue()){
                setRoughTarget(POSITION_1);
            }
        } else if (source == position2Button) {
            if (position2Button.getValue()){
                setRoughTarget(POSITION_2);
            }
        } else if (source == position3Button) {
            if (position3Button.getValue()){
                setRoughTarget(POSITION_3);
            }
        } else if (source == position4Button) {
            if (position4Button.getValue()){
                setRoughTarget(POSITION_4);
            }
        }
    }

    @Override
    public void init() {
        initInputs();
        initOutputs();
        initAxis();
        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        SmartDashboard.putNumber("Lift Encoder Value", motor.getSensorCollection().getQuadraturePosition());
        super.update();
    }

    @Override
    public void resetState() {
        // TODO
        super.resetState();
        setRoughTarget(POSITION_1);
    }

    @Override
    public String getName() {
        return "Lift";
    }

    ////////////////////////////
    // Private methods
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
    }

    private void initOutputs() {
        System.out.println("Initializing lift Talon ID " + CANConstants.LIFT_TALON);
        motor = new TalonSRX(CANConstants.LIFT_TALON);
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        motor.setInverted(INVERTED);
        motor.setSensorPhase(SENSOR_PHASE);
        motor.configNominalOutputForward(0, 0);
        motor.configNominalOutputReverse(0, 0);
        // Peak output is managed by Axis class
        // PID settings are managed by Axis class

        follower = new VictorSPX(CANConstants.LIFT_VICTOR);
        follower.setInverted(INVERTED);
        follower.follow(motor);
        follower.setNeutralMode(NeutralMode.Brake);
    }

    private void initAxis() {
        IInputManager inputManager = Core.getInputManager();
        axisConfig.lowerLimitSwitch = (DigitalInput) inputManager.getInput(WSInputs.LIFT_LOWER_LIMIT);
        axisConfig.lowerLimitSwitch.addInputListener(this);
        axisConfig.upperLimitSwitch = (DigitalInput) inputManager.getInput(WSInputs.LIFT_UPPER_LIMIT);
        axisConfig.upperLimitSwitch.addInputListener(this);
        axisConfig.manualAdjustmentJoystick = (AnalogInput) inputManager.getInput(WSInputs.LIFT_MANUAL);
        axisConfig.manualAdjustmentJoystick.addInputListener(this);
        axisConfig.overrideButtonModifier = (DigitalInput) inputManager.getInput(WSInputs.WEDGE_SAFETY_2);
        axisConfig.overrideButtonModifier.addInputListener(this);
        axisConfig.pidOverrideButton = (DigitalInput) inputManager.getInput(WSInputs.HATCH_COLLECT);
        axisConfig.pidOverrideButton.addInputListener(this);
        axisConfig.limitSwitchOverrideButton = (DigitalInput) inputManager.getInput(WSInputs.LIFT_LIMIT_SWITCH_OVERRIDE);
        axisConfig.limitSwitchOverrideButton.addInputListener(this);

        axisConfig.motor = motor;
        axisConfig.ticksPerInch = TICKS_PER_INCH;
        axisConfig.runAcceleration = TRACKING_MAX_ACCEL;
        axisConfig.runSpeed = TRACKING_MAX_SPEED;
        axisConfig.homingAcceleration = HOMING_MAX_ACCEL;
        axisConfig.homingSpeed = HOMING_MAX_SPEED;
        axisConfig.manualSpeed = MANUAL_SPEED;
        axisConfig.minTravel = BOTTOM_MAX_TRAVEL;
        axisConfig.maxTravel = TOP_MAX_TRAVEL;
        axisConfig.runSlot = LiftPID.TRACKING.slot;
        axisConfig.runK = LiftPID.TRACKING.k;
        axisConfig.homingSlot = LiftPID.HOMING.slot;
        axisConfig.homingK = LiftPID.HOMING.k;
        axisConfig.lowerLimitPosition = BOTTOM_STOP_POS;

        initAxis(axisConfig);
    }
}
