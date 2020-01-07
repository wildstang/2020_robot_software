package org.wildstang.year2019.subsystems.common;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
//import org.wildstang.framework.timer.WsTimer;
import org.wildstang.framework.timer.StopWatch;//timertesting

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.framework.pid.PIDConstants;


/**
 * This is a base class for controlled axes. This year it's the base for the lift and the strafe axes.
 * 
 * PID-controlled axes like these have some traits in common. They have limit switches and need to react
 * to them. They need a fine-tune input separate from their coarse commanded inputs. This encapsulates that.
 * 
 * The child class must add itself as a listener to the inputs it passes in via the AxisConfig, and call
 * super methods everywhere.
 */
public abstract class Axis implements Subsystem {

    /**
     * The update() method takes into account time since update() was last called. If a
     * glitch causes a long pause, however, we don't want to suddenly make a huge position update and
     * run off the rails (potentially literally). So we cap that time at some sane value.
     */
    public static final double MAX_UPDATE_DT = .04;

    /**
     * Timeout for configuring Talons DURING INIT ONLY. During run we should set a timeout of -1.
     */
    private static final int TIMEOUT = -1;

    /** The accumulated manual adjustment from the manip oper */
    private double manualAdjustment;
    /** The rough target specified by the subclass */
    private double roughTarget;

    
    //private WsTimer timer;
    public StopWatch timer = new StopWatch();//timertesting
    public double lastUpdateTime;

    public AxisConfig config = new AxisConfig();
    public IMotorController motor;
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

    protected static class AxisConfig {
        public AxisConfig() {
            // Configuration is done by writing members
        }

        // TODO: refactor CAN motors into output so this can just be an output
        /**
         * The motor used to control the axis. Subclass must configure the motor and hand it
         * off to us. That includes setting up 
         */
        public IMotorController motor;
    
        /** The number of motor encoder ticks in one inch of axis travel. */
        public double ticksPerInch;
        /** The maximum motor acceleration during run in in/s^2 */
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
        public PIDConstants runK;
        /** The PID slot to use while homing the axis */
        public int homingSlot = 1;
        /** PID constants to use while homing the axis */
        public PIDConstants homingK;

        /** Maximum motor output during normal operation */
        public double maxMotorOutput = 1;
        /** Maximum motor output when we've hit a limit switch */
        public double maxLimitedOutput = 1;

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
        
    }

    public void update() {
        //double time = timer.get();
        double time = timer.GetTimeInSec();//timertesting
        double dT = time - lastUpdateTime;
        lastUpdateTime = time;
        // Clamp the dT to be no more than MAX_UPDATE_DT so that
        // if we glitch and don't update for a while we don't do a big jerk motion
        if (dT > MAX_UPDATE_DT) {
            System.out.println("WARNING: MAX_UPDATE_DT exceeded in Axis");
            dT = MAX_UPDATE_DT;
        }
        if(isPIDOverridden)
        {
            motor.set(ControlMode.PercentOutput, config.manualAdjustmentJoystick.getValue());
        }
        else {
            if (isOverridden) {
                motor.set(ControlMode.PercentOutput, config.manualAdjustmentJoystick.getValue());
            } else {
                
                    manualAdjustment += config.manualAdjustmentJoystick.getValue() * config.manualSpeed * dT;
                    setRunTarget(roughTarget + manualAdjustment);
                }
                
            }

            if (Math.abs(motor.getClosedLoopError(0) / config.ticksPerInch) < config.targetWindow) {
                lastTimeOnTarget = timer.GetTimeInSec();//timertesting old was timer.get
            } else {
                if (timer.GetTimeInSec() - lastTimeOnTarget > config.maxTimeToTarget) {//timertesting
                    setOverride(true);
                    
                }
                SmartDashboard.putNumber("rough target", roughTarget);
                SmartDashboard.putNumber("adjust", config.manualAdjustmentJoystick.getValue());
                SmartDashboard.putNumber("talon target", motor.getClosedLoopTarget(0));
                SmartDashboard.putNumber("talon error", motor.getClosedLoopError(0));

            }
        SmartDashboard.putNumber("rough target", roughTarget);
        SmartDashboard.putNumber("adjust", config.manualAdjustmentJoystick.getValue());
        SmartDashboard.putNumber("talon target", motor.getClosedLoopTarget(0));
        SmartDashboard.putNumber("talon error", motor.getClosedLoopError(0));

        // Puts a colored box (true: green, false: red) on SmartDashboard based on if axis motor is
        // within +/- <> inches of target (rough target +/- manual adjustment)
        if (motor.getSelectedSensorPosition(0) >= ((roughTarget + manualAdjustment) - config.axisInRangeThreshold)
                && motor.getSelectedSensorPosition(0) <= ((roughTarget + manualAdjustment) + config.axisInRangeThreshold)) {
            SmartDashboard.putBoolean("Axis in Range of Target", true);
        } else {
            SmartDashboard.putBoolean("Axis in Range of Target", false);
        }

        }// isStrafeOverride if statement
    

    public void inputUpdate(Input source) {
        if (source == config.manualAdjustmentJoystick) {
            // Handled in update, nothing to do
        } else if (source == config.lowerLimitSwitch) {
            SmartDashboard.putBoolean("lowerLimit", config.lowerLimitSwitch.getValue());
            if (config.lowerLimitSwitch.getValue() && isHoming) {
                finishHoming();
            } 
            if (config.lowerLimitSwitch.getValue() && !isLimitSwitchOverridden) {
                motor.configPeakOutputReverse(-config.maxLimitedOutput, -1);
            } else {
                motor.configPeakOutputReverse(-config.maxMotorOutput, -1);
            }
        } else if (source == config.upperLimitSwitch) {
            SmartDashboard.putBoolean("upperLimit", config.upperLimitSwitch.getValue());
            if (config.upperLimitSwitch.getValue() && !isLimitSwitchOverridden) {
                motor.configPeakOutputForward(config.maxLimitedOutput, -1);
            } else {
                motor.configPeakOutputForward(config.maxMotorOutput, -1);
            }
        } else if (source == config.overrideButtonModifier) {
            if (config.overrideButtonModifier.getValue() == true) {
                config.overrideButtonValue = true;
            } else {
                config.overrideButtonValue = false;
            }
        } else if (source == config.pidOverrideButton) {
            if (config.pidOverrideButton.getValue() == true && config.overrideButtonValue == true) {
                isPIDOverridden = !isPIDOverridden;
            }
        } else if (source == config.limitSwitchOverrideButton) {
            if (config.limitSwitchOverrideButton.getValue() == true && config.overrideButtonValue == true) {
                isLimitSwitchOverridden = !isLimitSwitchOverridden;
            }
        }

    }

    public void resetState() {
        manualAdjustment = 0;
        lastTimeOnTarget = timer.GetTimeInSec();//timertesting old was .get
        isLimitSwitchOverridden = false;
        isPIDOverridden = false;
    }

    /** 
     * Set the override state of the axis.
     *
     * @param doOverride IFF this is true, then we will disable PID and switch
     *                   to fully-manual open-loop control. IFF this is false,
     *                   we will re-enable PID and switch to closed-loop
     *                   control.
    **/
    public void setOverride(boolean doOverride) {
        isOverridden = doOverride;
        SmartDashboard.putBoolean(getName() + " Override", isOverridden);
    }

    public boolean getOverride() {
        return isOverridden;
    }

    public void toggleOverride() {
        setOverride(!getOverride());
    }

    /**
     * Set the rough position (e.g. preset position, or vision-detected
     * position). Actual axis position will also take into account the manual
     * adjustment.
     * @param position Position to travel to in inches
     */
    protected void setRoughTarget(double target) {
        roughTarget = target;
    }

    /**
     * Initialize the axis with relevant settings
     */
    protected void initAxis(AxisConfig config) {
        
        this.config = config;
        this.motor = config.motor;
        timer.Start();//timertesting old was .start
        /*CoreUtils.checkCTRE*/motor.config_kF(config.runSlot, config.runK.f, TIMEOUT);
        /*CoreUtils.checkCTRE*/motor.config_kP(config.runSlot, config.runK.p, TIMEOUT);
        /*CoreUtils.checkCTRE*/motor.config_kI(config.runSlot, config.runK.i, TIMEOUT);
        /*CoreUtils.checkCTRE*/motor.config_kD(config.runSlot, config.runK.d, TIMEOUT);
        /*CoreUtils.checkCTRE*/motor.config_kF(config.homingSlot, config.homingK.f, TIMEOUT);
        /*CoreUtils.checkCTRE*/motor.config_kP(config.homingSlot, config.homingK.p, TIMEOUT);
        /*CoreUtils.checkCTRE*/motor.config_kI(config.homingSlot, config.homingK.i, TIMEOUT);
        /*CoreUtils.checkCTRE*/motor.config_kD(config.homingSlot, config.homingK.d, TIMEOUT);
        setSpeedAndAccel(config.runSpeed, config.runAcceleration);
        motor.setNeutralMode(NeutralMode.Brake);
        motor.setSelectedSensorPosition(0, 0, -1);
    }

    /** Begin homing the axis */
    public void beginHoming(double target) {    
        isHoming = true;
        motor.selectProfileSlot(config.homingSlot, 0);
        if(target < motor.getSelectedSensorPosition(0)) {   
            motor.set(ControlMode.Velocity, -config.homingSpeed * config.ticksPerInch);
        }
        else {
            motor.set(ControlMode.Velocity, config.homingSpeed * config.ticksPerInch);
        }
        
        System.out.println("begin homing");
    }

    /** Triggered when homing switch is triggered */
    public void finishHoming() {
        isHoming = false;
        motor.setSelectedSensorPosition((int)(config.lowerLimitPosition * config.ticksPerInch), 0, -1);
        motor.selectProfileSlot(config.runSlot, 0);
        motor.set(ControlMode.Velocity, 0);
        setSpeedAndAccel(config.runSpeed, config.runAcceleration);
        System.out.println("endHoming");
    }

    private void setSpeedAndAccel(double speed, double accel) {
        // Change from inches per second to ticks per decisecond
        double speedTicks = speed / 10 * config.ticksPerInch;
        // Change from in/s^2 to ticks/ds/s 
        double accelTicks = speed / 10 * config.ticksPerInch;
        
        motor.configMotionAcceleration((int) accelTicks, -1);
        motor.configMotionCruiseVelocity((int) speedTicks, -1);
    }

    /**
     * Set the exact target of axis motion in run mode. If the axis is homing, 
     * this does nothing.
     */
    private void setRunTarget(double target) {
        SmartDashboard.putNumber("axis target", target);
        if (!isHoming) {
            double clampedTarget = Math.max(Math.min(target, config.maxTravel), config.minTravel);
            motor.set(ControlMode.MotionMagic, clampedTarget * config.ticksPerInch);    
        }
    }
}
