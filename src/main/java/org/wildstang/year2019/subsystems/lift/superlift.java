package org.wildstang.year2019.subsystems.lift;


import com.ctre.phoenix.motorcontrol.ControlMode;
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
import org.wildstang.framework.timer.StopWatch;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.framework.pid.PIDConstants;
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
public class superlift implements Subsystem {

    private static final boolean INVERTED = false;
    private static final boolean SENSOR_PHASE = true;


    // All positions in inches above lower limit
    //position_1+28=position_3
    //position_3+28=position_4
    private static double POSITION_1 = 0.0;//low goal
    private static double POSITION_2 = -10.62;//cargo goal - cargo only
    private static double POSITION_3 = -22.0;//mid goal
    private static double POSITION_4 = -44;//high goal

    /** # of rotations of encoder in one inch of axis travel */
    private static final double REVS_PER_INCH = 1/9.087;//5.092                                                   
    /** Number of encoder ticks in one revolution */
    private static final double TICKS_PER_REV = 4096;//4096 
    /** # of ticks in one inch of axis movement */
    private static final double TICKS_PER_INCH = TICKS_PER_REV * REVS_PER_INCH;

    private DigitalInput position1Button;
    private DigitalInput position2Button;
    private DigitalInput position3Button;
    private DigitalInput position4Button;

    private TalonSRX motor;
    private VictorSPX follower;

    public static final double MAX_UPDATE_DT = .04;

    /** The accumulated manual adjustment from the manip oper */
    private double manualAdjustment;
    /** The rough target specified by the subclass */
    private double target;

    
    //private WsTimer timer;
    public StopWatch timer = new StopWatch();//timertesting
    public double lastUpdateTime;

    
    /** True during homing cycle */
    public boolean isHoming = false;
    /**
     * True iff the operator (or a failure condition) has caused us to enter
     * the overridden state
     */
    private boolean isOverridden = false;

    private boolean isLimitSwitchOverridden;
    private boolean isPIDOverridden;

    /** The last time (according to timer) that we have been on target */
    private double lastTimeOnTarget;

    public boolean isdown = false;

    // Logical variables
    public double runAcceleration = 2;
    /** The maximum motor speed during run in in/s*/
    public double runSpeed = 2;
    /** The maximum motor acceleration during homing in in/s^2 */
    public double homingAcceleration = 1;
    /** The maximum motor speed during homing in in/s */
    public double homingSpeed = 1;

    /** The maximum speed of the axis in fine-tuning in in/s */
    public double manualSpeed = 1;

    /** The furthest in the negative direction axis may travel in inches */
    public double minTravel = 0;
    /** The furthest in the positive direction that the axis may travel in inches */
    public double maxTravel = 0;

    /** This input is used by the manipulator controller to fine-tune the axis position. */
    public AnalogInput manualAdjustmentJoystick;

    /** General button references for Lift and StrafeAxis to use for overrides */
    public DigitalInput overrideButtonModifier;
    public DigitalInput limitSwitchOverrideButton;
    public DigitalInput pidOverrideButton;
    /** Locally stored Value of the modifier button required to initiate overrides */
    public boolean overrideButtonValue;
    /** The limit switch activated by max travel in negative direction */
    public DigitalInput lowerLimitSwitch; 
    /** The limit switch activated by max travel in positive direction */
    public DigitalInput upperLimitSwitch; 
    /** The position of the lower limit switch (used in homing) */
    public double lowerLimitPosition = 0;

    /** The PID slot to use while moving the axis to a target */
    public int runSlot = 0;
    /** PID constants to use while moving the axis to a target */
    public PIDConstants runK = LiftPID.TRACKING.k;
    /** The PID slot to use while homing the axis */
    public int homingSlot = 1;
    /** PID constants to use while homing the axis */
    public PIDConstants homingK= LiftPID.HOMING.k;

    public int downSlot = 2;
    public PIDConstants downK = LiftPID.DOWNTRACK.k;

    /** Maximum motor output during normal operation */
    public double maxMotorOutput = 1;
    /** Maximum motor output when we've hit a limit switch */
    public double maxLimitedOutput = 0.05;

    public double manualControlModifier = 1.0;

    /** Error within which we consider ourselves "on target" (inches). */
    public double targetWindow = 0.02;
    /**
     * If we go this long without making it into the target window,
     * we assume that we're jammed and go into override (seconds).
     *
     * Ideally, this is less than the time it takes to burn a motor out if
     * we get jammed.
     */
    public double maxTimeToTarget = 200;

    // Threshold (in ticks) for which axis motor can be considered in range of target
    public double axisInRangeThreshold;

    public boolean needtargetUpdate=false;
    public enum control {TRACK, HOME, MANUAL};
    public int currentcommand = 0;


    @Override
    public void inputUpdate(Input source) {
        
        if (source == position1Button) {
            if (position1Button.getValue()){
                target = POSITION_1 * TICKS_PER_INCH;
                currentcommand = control.TRACK.ordinal();
                needtargetUpdate=false;
            }
        } else if (source == position2Button) {
            if (position2Button.getValue()){
                target = POSITION_2 * TICKS_PER_INCH;
                currentcommand = control.TRACK.ordinal();
                needtargetUpdate=false;
            }
        } else if (source == position3Button) {
            if (position3Button.getValue()){
                target = POSITION_3 * TICKS_PER_INCH;
                currentcommand = control.TRACK.ordinal();
                needtargetUpdate=false;
            }
        } else if (source == position4Button) {
            if (position4Button.getValue()){
                target = POSITION_4 * TICKS_PER_INCH;
                currentcommand = control.TRACK.ordinal();
                needtargetUpdate=false;
                SmartDashboard.putBoolean("High triggered",true);
            }
        }
        if (source == manualAdjustmentJoystick) {
            if (Math.abs(manualAdjustmentJoystick.getValue()) > 0.2){
                needtargetUpdate=true;
                currentcommand = control.MANUAL.ordinal();
            } else if (needtargetUpdate){
                currentcommand = control.HOME.ordinal();
            }
        } else if (source == overrideButtonModifier) {
            if (overrideButtonModifier.getValue() == true) {
                overrideButtonValue = true;
            } else {
                overrideButtonValue = false;
            }
        } else if (source == pidOverrideButton) {
            if (pidOverrideButton.getValue() == true && overrideButtonValue == true) {
                isPIDOverridden = !isPIDOverridden;
            }
        } else if (source == limitSwitchOverrideButton) {
            if (limitSwitchOverrideButton.getValue() == true && overrideButtonValue == true) {
                isLimitSwitchOverridden = !isLimitSwitchOverridden;
            }
        } else if (source == lowerLimitSwitch) {
            SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.getValue());
            
            if (lowerLimitSwitch.getValue() == true && !isLimitSwitchOverridden) {
                motor.configPeakOutputForward(maxLimitedOutput, -1);
            } else {
                motor.configPeakOutputForward(maxMotorOutput, -1);
            }
        } else if (source == upperLimitSwitch) {
            SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.getValue());

            if (upperLimitSwitch.getValue() == true && !isLimitSwitchOverridden) {
                motor.configPeakOutputReverse(-maxLimitedOutput, -1);
            } else {
                motor.configPeakOutputReverse(-maxMotorOutput, -1);
            }
        }
    }

    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        SmartDashboard.putNumber("Lift Encoder Value", motor.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("Lift Voltage", motor.getMotorOutputVoltage());
        SmartDashboard.putNumber("Current Command", currentcommand);
        SmartDashboard.putNumber("Target", target);
        SmartDashboard.putBoolean("Is Down", isdown);

        // DEBUG
        SmartDashboard.putNumber("Lift Target Difference", Math.abs(motor.getSensorCollection().getQuadraturePosition() -target));

        if (isPIDOverridden){
            currentcommand = control.MANUAL.ordinal();
            manualControlModifier = 1.0;
            needtargetUpdate=true;
            manualDrive();
        } else if (currentcommand == control.MANUAL.ordinal()){
            manualControlModifier = 0.3;
            manualDrive();
        } else if (currentcommand == control.TRACK.ordinal()){
            track();
        } else if (currentcommand == control.HOME.ordinal()){
            if (needtargetUpdate){
                target = motor.getSensorCollection().getQuadraturePosition();
                needtargetUpdate=false;
            } 
            
            home();
        }
    }

    public void manualDrive(){
        //TODO: Change 0.6 to Variable
        motor.set(ControlMode.PercentOutput, manualControlModifier * manualAdjustmentJoystick.getValue());
    }
    public void home(){
        motor.selectProfileSlot(homingSlot, 0);
        motor.set(ControlMode.Position, -target);
    }
    public void track(){
        if (Math.abs(Math.abs(motor.getSensorCollection().getQuadraturePosition()) - Math.abs(target)) < getEncoderLocation(2)){
            
            currentcommand = control.HOME.ordinal();
            home();
        } else{
            if(Math.abs(motor.getSensorCollection().getQuadraturePosition()) < Math.abs(-target)) {
                motor.selectProfileSlot(runSlot, 0);
                isdown = false;
                motor.set(ControlMode.Position, -target-200);
            } else if(Math.abs(motor.getSensorCollection().getQuadraturePosition()) > Math.abs(-target)){
                motor.selectProfileSlot(downSlot, 0);
                isdown=true;
                motor.set(ControlMode.Position, -target + 800);
            }

        }
    }

    @Override
    public void resetState() {
        manualAdjustment = 0;
        lastTimeOnTarget = timer.GetTimeInSec();//timertesting old was .get
        isLimitSwitchOverridden = false;
        isPIDOverridden = false;
        target = POSITION_1 * TICKS_PER_INCH;
    }

    @Override
    public String getName() {
        return "Lift";
    }

    public double getEncoderLocation(double inputTarget){
        return inputTarget*TICKS_PER_INCH;
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
        manualAdjustmentJoystick = (AnalogInput) inputManager.getInput(WSInputs.LIFT_MANUAL);
        manualAdjustmentJoystick.addInputListener(this);
        overrideButtonModifier = (DigitalInput) inputManager.getInput(WSInputs.WEDGE_SAFETY_2);
        overrideButtonModifier.addInputListener(this);
        pidOverrideButton = (DigitalInput) inputManager.getInput(WSInputs.HATCH_COLLECT);
        pidOverrideButton.addInputListener(this);
        limitSwitchOverrideButton = (DigitalInput) inputManager.getInput(WSInputs.LIFT_LIMIT_SWITCH_OVERRIDE);
        limitSwitchOverrideButton.addInputListener(this);
        lowerLimitSwitch = (DigitalInput) inputManager.getInput(WSInputs.LIFT_LOWER_LIMIT);
        lowerLimitSwitch.addInputListener(this);
        upperLimitSwitch = (DigitalInput) inputManager.getInput(WSInputs.LIFT_UPPER_LIMIT);
        upperLimitSwitch.addInputListener(this);
    }

    private void initOutputs() {
        System.out.println("Initializing lift Talon ID " + CANConstants.LIFT_TALON);
        motor = new TalonSRX(CANConstants.LIFT_TALON);
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        motor.setInverted(INVERTED);
        motor.setSensorPhase(SENSOR_PHASE);
        /*CoreUtils.checkCTRE*/motor.configNominalOutputForward(0, 0);
        /*CoreUtils.checkCTRE*/motor.configNominalOutputReverse(0, 0);
        // Peak output is managed by Axis class
        // PID settings are managed by Axis class
        timer.Start();//timertesting old was .start
        /*CoreUtils.checkCTRE*/motor.config_kF(runSlot, runK.f, 0);
        /*CoreUtils.checkCTRE*/motor.config_kP(runSlot, runK.p, 0);
        /*CoreUtils.checkCTRE*/motor.config_kI(runSlot, runK.i, 0);
        /*CoreUtils.checkCTRE*/motor.config_kD(runSlot, runK.d, 0);
        /*CoreUtils.checkCTRE*/motor.config_kF(homingSlot, homingK.f, 0);
        /*CoreUtils.checkCTRE*/motor.config_kP(homingSlot, homingK.p, 0);
        /*CoreUtils.checkCTRE*/motor.config_kI(homingSlot, homingK.i, 0);
        /*CoreUtils.checkCTRE*/motor.config_kD(homingSlot, homingK.d, 0);
        
        /*CoreUtils.checkCTRE*/motor.config_kF(downSlot, downK.f, 0);
        /*CoreUtils.checkCTRE*/motor.config_kP(downSlot, downK.p, 0);
        /*CoreUtils.checkCTRE*/motor.config_kI(downSlot, downK.i, 0);
        /*CoreUtils.checkCTRE*/motor.config_kD(downSlot, downK.d, 0);
        setSpeedAndAccel(runSpeed, runAcceleration);
        motor.setNeutralMode(NeutralMode.Brake);
        motor.setSelectedSensorPosition(0, 0, -1);

        follower = new VictorSPX(CANConstants.LIFT_VICTOR);
        follower.setInverted(INVERTED);
        follower.follow(motor);
        follower.setNeutralMode(NeutralMode.Brake);
    }
    private void setSpeedAndAccel(double speed, double accel) {
        // Change from inches per second to ticks per decisecond
        double speedTicks = speed / 10 * TICKS_PER_INCH;
        // Change from in/s^2 to ticks/ds/s 
        double accelTicks = speed / 10 * TICKS_PER_INCH;
        
        motor.configMotionAcceleration((int) accelTicks, -1);
        motor.configMotionCruiseVelocity((int) speedTicks, -1);
    }
    public boolean autoLift(int liftLevel){
        if (liftLevel==1) {
            target = POSITION_1 * TICKS_PER_INCH;
            currentcommand = control.TRACK.ordinal();
        } else if (liftLevel==2){
            target = POSITION_3 * TICKS_PER_INCH;//not a typo, level 2 of rocket = position 3
            currentcommand = control.TRACK.ordinal();
        } else if (liftLevel==3){
            target = POSITION_4 * TICKS_PER_INCH;//not a typo, level 3 of rocket = position 4
            currentcommand = control.TRACK.ordinal();
        }
        update();
        return (Math.abs(Math.abs(motor.getSensorCollection().getQuadraturePosition()) - Math.abs(target)) < getEncoderLocation(2));
    }
     
}