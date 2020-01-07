package org.wildstang.year2016.subsystems;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.config.Config;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.outputs.AnalogOutput;
import org.wildstang.framework.motionprofile.ContinuousAccelFilter;
import org.wildstang.framework.pid.controller.SpeedPidController;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.inputs.WsMotionProfileControl;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoidState;
import org.wildstang.year2016.robot.Robot;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSOutputs;
import org.wildstang.year2016.robot.WsDriveBaseSpeedPidInput;
import org.wildstang.year2016.robot.WsDriveBaseSpeedPidOutput;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
//import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Smitty
 */
public class DriveBase implements Subsystem {

    private static Logger s_log = Logger.getLogger(Robot.class.getName());

    double throttleValue = 0.0;
    double headingValue = 0.0;

    private static final double MAX_INPUT_THROTTLE_VALUE = 1.0;
    private static final double MAX_INPUT_HEADING_VALUE = 1.0;
    private static final double HEADING_SENSITIVITY = 1.8;
    private static final double MAX_MOTOR_OUTPUT = 1.0;
    private static final double ANTI_TURBO_MAX_DEFLECTION = 0.500;
    private static double THROTTLE_LOW_GEAR_ACCEL_FACTOR = 0.250;
    private static double HEADING_LOW_GEAR_ACCEL_FACTOR = 0.500;
    private static double THROTTLE_HIGH_GEAR_ACCEL_FACTOR = 0.125;
    private static double HEADING_HIGH_GEAR_ACCEL_FACTOR = 0.250;
    private static double TICKS_PER_ROTATION = 256.0;
    private static double WHEEL_DIAMETER = 6;
    private static double MAX_HIGH_GEAR_PERCENT = 0.80;
    private static double ENCODER_GEAR_RATIO = 7.5;
    private static double DEADBAND = 0.1;
    private static double SLOW_TURN_FORWARD_SPEED;
    private static double SLOW_TURN_BACKWARD_SPEED;
    private static double MAX_ACCELERATION_DRIVE_PROFILE = 600.0;
    private static double STOPPING_DISTANCE_AT_MAX_SPEED_LOWGEAR = 10.0;
    private static double QUICK_TURN_CAP;
    private static double QUICK_TURN_ANTITURBO;
    private static boolean ACCELERATION_ENABLED = false;
    private static double SUPER_ANTITURBO_FACTOR = 0.22;
    private static double HELLA_ANTITURBO_FACTOR = 0.22;
    private static double driveBaseThrottleValue = 0.0;
    private static double driveBaseHeadingValue = 0.0;
    private static boolean antiTurboFlag = false;
    private static boolean superAntiTurboFlag = false;
    private static boolean hellaAntiTurboFlag = false;
    private static boolean turboFlag = false;
    private static boolean highGearFlag = false; // default to low gear
    private static boolean quickTurnFlag = false;
    private static Encoder leftDriveEncoder;
    private static Encoder rightDriveEncoder;
    private static AnalogGyro driveHeadingGyro;
    private static ContinuousAccelFilter continuousAccelerationFilter;
    // Set low gear top speed to 8.5 ft/ second = 102 inches / second = 2.04
    // inches/ 20 ms
    private static double MAX_SPEED_INCHES_LOWGEAR = 90.0;
    private double goal_velocity = 0.0;
    private double distance_to_move = 0.0;
    private double distance_moved = 0.0;
    private double distance_remaining = 0.0;
    private boolean motionProfileActive = false;
    private double currentProfileX = 0.0;
    private double currentProfileV = 0.0;
    private double currentProfileA = 0.0;
    private static double FEED_FORWARD_VELOCITY_CONSTANT = 1.00;
    private static double FEED_FORWARD_ACCELERATION_CONSTANT = 0.00018;
    private double previousRightPositionSinceLastReset = 0.0;
    private double previousLeftPositionSinceLastReset = 0.0;
    private static double pidSpeedValue = 0.0;
    private static SpeedPidController driveSpeedPid;
    private static WsDriveBaseSpeedPidInput driveSpeedPidInput;
    private static WsDriveBaseSpeedPidOutput driveSpeedPidOutput;
    private double deltaPosition = 0.0;
    private double deltaTime = 0.0;
    private double deltaPosError = 0.0;
    private double deltaProfilePosition = 0.0;
    private double previousTime = 0.0;
    private double previousVelocity = 0.0;
    private double currentVelocity = 0.0;
    private double currentAcceleration = 0.0;
    private double DECELERATION_VELOCITY_THRESHOLD = 48; // Velocity in in/sec
    private double DECELERATION_MOTOR_SPEED = 0.3;
    private static double outputScaleFactor = 1.0;
    private boolean isPistolGrip = false;
    private double leftIndividual = 0;
    private double rightIndividual = 0;

    private double overriddenThrottle, overriddenHeading;
    private boolean driveOverrideEnabled = false;

    private boolean AutoDriveOverride = false;

    public DriveBase() {
        // Load the config parameters
        // notifyConfigChange(Core.getConfigManager().getConfig());

        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_5.getName()).addInputListener(this);
        // Turbo
        // Initialize the drive base encoders
        leftDriveEncoder = new Encoder(0, 1, true, EncodingType.k4X);
        leftDriveEncoder.reset();
        rightDriveEncoder = new Encoder(2, 3, false, EncodingType.k4X);
        rightDriveEncoder.reset();

        // Initialize the gyro
        // @TODO: Get the correct port
        driveHeadingGyro = new AnalogGyro(0);

        continuousAccelerationFilter = new ContinuousAccelFilter(0, 0, 0);
        driveSpeedPidInput = new WsDriveBaseSpeedPidInput();
        driveSpeedPidOutput = new WsDriveBaseSpeedPidOutput();
        driveSpeedPid = new SpeedPidController(driveSpeedPidInput, driveSpeedPidOutput,
                "DriveBaseSpeedPid");
    }

    @Override
    public void init() {
        driveBaseThrottleValue = 0.0;
        driveBaseHeadingValue = 0.0;
        antiTurboFlag = false;
        superAntiTurboFlag = false;
        hellaAntiTurboFlag = false;
        turboFlag = false;
        // Default to low gear
        highGearFlag = false;
        quickTurnFlag = false;
        motionProfileActive = false;
        stopStraightMoveWithMotionProfile();
        // previousTime = Timer.getFPGATimestamp();
        currentProfileX = 0.0;
        continuousAccelerationFilter = new ContinuousAccelFilter(0, 0, 0);
        // Zero out all motor values left over from autonomous

        // Shifter Button
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_6.getName()).addInputListener(this);
        // Super anti-turbo button
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_5.getName()).addInputListener(this);
        // Turbo
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_8.getName()).addInputListener(this);
        // Flip Drive
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_4.getName()).addInputListener(this);
        // Drive
        Core.getInputManager().getInput(WSInputs.DRV_HEADING.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.DRV_THROTTLE.getName()).addInputListener(this);

        // Check to see if pistol grip controller is being used
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_12_PG.getName()).update();
        isPistolGrip = ((DigitalInput) Core.getInputManager()
                .getInput(WSInputs.DRV_BUTTON_12_PG.getName())).getValue();

        // Clear encoders
        resetLeftEncoder();
        resetRightEncoder();

        // Clear overrides
        overriddenHeading = overriddenThrottle = 0.0;
        driveOverrideEnabled = false;
    }

    @Override
    public void update() {
        // System.out.println("Motion Profile " + motionProfileActive);
        // System.out.println("Override " + driveOverrideEnabled);

        // if(firstRun = true)
        // {
        // leftDriveEncoder.reset();
        // rightDriveEncoder.reset();
        // firstRun = false;
        // if(!DriverStation.getInstance().isAutonomous())
        // stopStraightMoveWithMotionProfile();
        // }
        updateSpeedAndAccelerationCalculations();
        if (true == motionProfileActive && AutoDriveOverride) {

            // Update PID using profile velocity as setpoint and measured
            // velocity as PID input
            enableSpeedPidControl();
            setDriveSpeedPidSetpoint(continuousAccelerationFilter.getCurrVel());
            // Update system to get feed forward terms
            deltaPosError = this.deltaPosition - (deltaProfilePosition);
            distance_moved += this.deltaPosition;
            // distance_remaining = this.distance_to_move - currentProfileX;
            distance_remaining = this.distance_to_move - distance_moved;
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("AccelFilter: distance_left: " + distance_remaining + " p: "
                        + continuousAccelerationFilter.getCurrPos() + " v: "
                        + continuousAccelerationFilter.getCurrVel() + " a: "
                        + continuousAccelerationFilter.getCurrAcc());
            }
            continuousAccelerationFilter.calculateSystem(distance_remaining, currentProfileV,
                    goal_velocity, MAX_ACCELERATION_DRIVE_PROFILE, MAX_SPEED_INCHES_LOWGEAR,
                    deltaTime);
            deltaProfilePosition = continuousAccelerationFilter.getCurrPos() - currentProfileX;
            currentProfileX = continuousAccelerationFilter.getCurrPos();
            currentProfileV = continuousAccelerationFilter.getCurrVel();
            currentProfileA = continuousAccelerationFilter.getCurrAcc();
            SmartDashboard.putNumber("Speed PID Error", driveSpeedPid.getError());
            SmartDashboard.putNumber("Speed PID Output", DriveBase.pidSpeedValue);
            SmartDashboard.putNumber("Distance Error", this.deltaPosError);
            // Update motor output with PID output and feed forward velocity and
            // acceleration
            throttleValue = DriveBase.pidSpeedValue
                    + FEED_FORWARD_VELOCITY_CONSTANT
                            * (continuousAccelerationFilter.getCurrVel() / MAX_SPEED_INCHES_LOWGEAR)
                    + FEED_FORWARD_ACCELERATION_CONSTANT
                            * continuousAccelerationFilter.getCurrAcc();

            if (((distance_remaining < getStoppingDistanceFromDistanceToMove(distance_to_move))
                    && (currentProfileV > 0) && (currentProfileA < 0))
                    || ((distance_remaining > -getStoppingDistanceFromDistanceToMove(
                            distance_to_move)) && (currentProfileV < 0) && (currentProfileA > 0))) {
                throttleValue = 0.0;
            }

            // Update the throttle value outside the function so that the
            // acceleration factor is not applied.
            driveBaseThrottleValue = throttleValue;
            if (driveBaseThrottleValue > MAX_INPUT_THROTTLE_VALUE) {
                driveBaseThrottleValue = MAX_INPUT_THROTTLE_VALUE;
            } else if (driveBaseThrottleValue < -MAX_INPUT_THROTTLE_VALUE) {
                driveBaseThrottleValue = -MAX_INPUT_THROTTLE_VALUE;
            }
            SmartDashboard.putNumber("Motion Profile Throttle", driveBaseThrottleValue);
            updateDriveMotors();
        } else {
            // We are in manual control
            // heading and throttle values have been updated by inputUpdate
            // or direct setter methods

            // TODO: These need to be moved to inputUpdate

            SmartDashboard.putNumber("Throttle Joystick Value", throttleValue);
            SmartDashboard.putNumber("Heading Joystick Value", headingValue);
            if (!driveOverrideEnabled) {
                setThrottleValue(throttleValue);
                setHeadingValue(headingValue);
            } else {
                setThrottleValue(overriddenThrottle);
                setHeadingValue(overriddenHeading);
            }

            // Use updated values to update the quickTurnFlag
            checkAutoQuickTurn();

            // Set the drive motor outputs
            updateDriveMotors();

            SmartDashboard.putNumber("Throttle Value", driveBaseThrottleValue);
            SmartDashboard.putNumber("Heading Value", driveBaseHeadingValue);
            SmartDashboard.putBoolean("High Gear", highGearFlag);
            SmartDashboard.putBoolean("Anti-Turbo Flag", antiTurboFlag);
            SmartDashboard.putBoolean("Quickturn", quickTurnFlag);

            // Set gear shift output
            ((WsDoubleSolenoid) Core.getOutputManager().getOutput(WSOutputs.SHIFTER.getName()))
                    .setValue(new Integer(
                            highGearFlag == true ? WsDoubleSolenoidState.FORWARD.ordinal()
                                    : WsDoubleSolenoidState.REVERSE.ordinal()));
        }

        SmartDashboard.putNumber("Left encoder count: ", this.getLeftEncoderValue());
        SmartDashboard.putNumber("Right encoder count: ", this.getRightEncoderValue());
        SmartDashboard.putNumber("Right Distance: ", this.getRightDistance());
        SmartDashboard.putNumber("Left Distance: ", this.getLeftDistance());
        SmartDashboard.putNumber("Gyro angle", this.getGyroAngle());
    }

    public void overrideThrottleValue(double throttle) {
        overriddenThrottle = throttle;
        driveOverrideEnabled = true;
    }

    public void overrideHeadingValue(double heading) {
        overriddenHeading = heading;
        driveOverrideEnabled = true;
    }

    public void disableDriveOverride() {
        driveOverrideEnabled = false;
        overriddenHeading = overriddenThrottle = 0.0;
    }

    private void updateSpeedAndAccelerationCalculations() {
        double newTime = Timer.getFPGATimestamp();
        double leftDistance = this.getLeftDistance();
        double rightDistance = this.getRightDistance();
        double rightDelta = (rightDistance - previousRightPositionSinceLastReset);
        double leftDelta = (leftDistance - previousLeftPositionSinceLastReset);
        this.deltaPosition = (Math.abs(rightDelta) > Math.abs(leftDelta) ? rightDelta : leftDelta);
        this.deltaTime = (newTime - previousTime);
        if (this.deltaTime > 0.060) {
            this.deltaTime = 0.060;
        }
        // Do velocity internally in in/sec
        currentVelocity = (deltaPosition / deltaTime);
        currentAcceleration = ((currentVelocity - previousVelocity) / deltaTime);

        // Output velocity in ft/sec
        SmartDashboard.putNumber("Velocity: ", this.currentVelocity / 12.0);
        SmartDashboard.putNumber("Accel: ", this.currentAcceleration / 144.0);

        if (Math.abs(deltaPosition) > 0.005) {
            // Logger.getLogger().debug(this.getClass().getName(), "Kinematics",
            // "tP: "+ totalPosition + " dP: " + deltaPosition + "dpp:" +
            // deltaProfilePosition + " dt: " + deltaTime + " cv: " +
            // currentVelocity + " pv: " + previousVelocity + " ca: " +
            // currentAcceleration);
        }
        previousRightPositionSinceLastReset += rightDelta;
        previousLeftPositionSinceLastReset += leftDelta;
        previousTime = newTime;
        previousVelocity = currentVelocity;

    }

    public void resetKinematics() {
        previousVelocity = 0.0;
        currentVelocity = 0.0;
        currentAcceleration = 0.0;
    }

    public double getDistanceRemaining() {
        return this.distance_remaining;
    }

    public double getAcceleration() {
        return currentAcceleration;
    }

    public double getVelocity() {
        return currentVelocity;
    }

    public void setThrottleValue(double tValue) {

        // Taking into account Anti-Turbo
        double new_throttle = tValue;

        if (true == superAntiTurboFlag) {
            new_throttle *= SUPER_ANTITURBO_FACTOR;
        } else if (true == hellaAntiTurboFlag) {
            new_throttle *= HELLA_ANTITURBO_FACTOR;
        } else if (true == antiTurboFlag) {
            new_throttle *= ANTI_TURBO_MAX_DEFLECTION;

            // Cap the throttle at the maximum deflection allowed for anti-turbo
            if (new_throttle > ANTI_TURBO_MAX_DEFLECTION) {
                new_throttle = ANTI_TURBO_MAX_DEFLECTION;
            }
            if (new_throttle < -ANTI_TURBO_MAX_DEFLECTION) {
                new_throttle = -ANTI_TURBO_MAX_DEFLECTION;
            }
        }

        if (highGearFlag) {
            // We are in high gear, see if the turbo button is pressed
            if (true == turboFlag) {
                // We are in turbo mode, don't cap the output
            } else {
                // We aren't in turbo mode, cap the output at the max percent
                // for high gear
                if (new_throttle > MAX_MOTOR_OUTPUT * MAX_HIGH_GEAR_PERCENT) {
                    new_throttle = MAX_MOTOR_OUTPUT * MAX_HIGH_GEAR_PERCENT;
                }
                if (new_throttle < -MAX_MOTOR_OUTPUT * MAX_HIGH_GEAR_PERCENT) {
                    new_throttle = -MAX_MOTOR_OUTPUT * MAX_HIGH_GEAR_PERCENT;
                }
            }

        } else {
            // We are in low gear, don't modify the throttle
        }

        if (ACCELERATION_ENABLED) {
            // Use the acceleration factor based on the current shifter state
            if (!highGearFlag) {
                // We are in low gear, use that acceleration factor
                driveBaseThrottleValue = driveBaseThrottleValue
                        + (new_throttle - driveBaseThrottleValue) * THROTTLE_LOW_GEAR_ACCEL_FACTOR;
            } else if (highGearFlag) {
                // We are in high gear, use that acceleration factor
                driveBaseThrottleValue = driveBaseThrottleValue
                        + (new_throttle - driveBaseThrottleValue) * THROTTLE_HIGH_GEAR_ACCEL_FACTOR;
            } else {
                // This is bad...
                // If we get here we have a problem
            }
        } else {
            driveBaseThrottleValue = new_throttle;
        }

        if (driveBaseThrottleValue > MAX_INPUT_THROTTLE_VALUE) {
            driveBaseThrottleValue = MAX_INPUT_THROTTLE_VALUE;
        } else if (driveBaseThrottleValue < -MAX_INPUT_THROTTLE_VALUE) {
            driveBaseThrottleValue = -MAX_INPUT_THROTTLE_VALUE;
        }
    }

    public void setHeadingValue(double hValue) {

        // Taking into account anti-turbo
        double new_heading = hValue;
        if (true == antiTurboFlag) {
            new_heading *= ANTI_TURBO_MAX_DEFLECTION;

            // Cap the heading at the maximum deflection allowed for anti-turbo
            if (new_heading > ANTI_TURBO_MAX_DEFLECTION) {
                new_heading = ANTI_TURBO_MAX_DEFLECTION;
            }
            if (new_heading < -ANTI_TURBO_MAX_DEFLECTION) {
                new_heading = -ANTI_TURBO_MAX_DEFLECTION;
            }
        }

        if (ACCELERATION_ENABLED) {
            // Use the acceleration factor based on the current shifter state
            if (!highGearFlag) {
                // We are in low gear, use that acceleration factor
                driveBaseHeadingValue = driveBaseHeadingValue
                        + (new_heading - driveBaseHeadingValue) * HEADING_LOW_GEAR_ACCEL_FACTOR;
            } else if (highGearFlag) {
                // We are in high gear, use that acceleration factor
                driveBaseHeadingValue = driveBaseHeadingValue
                        + (new_heading - driveBaseHeadingValue) * HEADING_HIGH_GEAR_ACCEL_FACTOR;
            } else {
                // This is bad...
                // If we get here we have a problem
            }
        } else {
            driveBaseHeadingValue = new_heading;
        }

        if (driveBaseHeadingValue > MAX_INPUT_HEADING_VALUE) {
            driveBaseHeadingValue = MAX_INPUT_HEADING_VALUE;
        } else if (driveBaseHeadingValue < -MAX_INPUT_HEADING_VALUE) {
            driveBaseHeadingValue = -MAX_INPUT_HEADING_VALUE;
        }
    }

    // TODO: Update
    public void updateDriveMotors() {
        double rightMotorSpeed = 0.0;
        double leftMotorSpeed = 0.0;
        double angularPower = 0.0;
        if (Math.abs(driveBaseHeadingValue) > 0.05) {
            // Absolute value on driveBaseThrottleValue, creates a S curve, none
            // is banana

            angularPower = (driveBaseThrottleValue) * driveBaseHeadingValue * HEADING_SENSITIVITY;
            if (driveBaseThrottleValue > 0) {
                angularPower *= -1;
            }
        }

        SmartDashboard.putNumber("Angular power", angularPower);

        rightMotorSpeed = driveBaseThrottleValue + angularPower;
        leftMotorSpeed = driveBaseThrottleValue - angularPower;

        if (true == quickTurnFlag) {
            rightMotorSpeed = 0.0f;
            leftMotorSpeed = 0.0f;
            driveBaseThrottleValue = 0.0f;

            // Quick turn does not take throttle into account

            leftMotorSpeed = -(leftMotorSpeed -= driveBaseHeadingValue);
            rightMotorSpeed = -(rightMotorSpeed += driveBaseHeadingValue);

            if (superAntiTurboFlag) {
                leftMotorSpeed *= SUPER_ANTITURBO_FACTOR;
                rightMotorSpeed *= SUPER_ANTITURBO_FACTOR;
            } else if (hellaAntiTurboFlag) {
                leftMotorSpeed *= HELLA_ANTITURBO_FACTOR;
                rightMotorSpeed *= HELLA_ANTITURBO_FACTOR;
            }

        } else {
            if (driveBaseThrottleValue >= 0) {
                if (rightMotorSpeed < 0) {
                    rightMotorSpeed = 0;
                }
                if (leftMotorSpeed < 0) {
                    leftMotorSpeed = 0;
                }
            } else {
                if (rightMotorSpeed >= 0) {
                    rightMotorSpeed = 0;
                }
                if (leftMotorSpeed >= 0) {
                    leftMotorSpeed = 0;
                }
            }

            if (rightMotorSpeed > MAX_MOTOR_OUTPUT) {
                rightMotorSpeed = MAX_MOTOR_OUTPUT;
            }
            if (leftMotorSpeed > MAX_MOTOR_OUTPUT) {
                leftMotorSpeed = MAX_MOTOR_OUTPUT;
            }
            if (rightMotorSpeed < -MAX_MOTOR_OUTPUT) {
                rightMotorSpeed = -MAX_MOTOR_OUTPUT;
            }
            if (leftMotorSpeed < -MAX_MOTOR_OUTPUT) {
                leftMotorSpeed = -MAX_MOTOR_OUTPUT;
            }
        }

        // If our throttle is within the zero deadband and our velocity is above
        // the threshold, use deceleration to slow us down
        if ((Math.abs(driveBaseThrottleValue) < DEADBAND)
                && (Math.abs(currentVelocity) > DECELERATION_VELOCITY_THRESHOLD)
                && !quickTurnFlag) {
            // We are above the velocity threshold, apply a small inverse motor
            // speed to decelerate
            if (currentVelocity > 0) {
                // We are moving forward, apply a negative motor value
                rightMotorSpeed = -DECELERATION_MOTOR_SPEED;
                leftMotorSpeed = -DECELERATION_MOTOR_SPEED;
            } else {
                // We are moving backward, apply a positive motor value
                rightMotorSpeed = DECELERATION_MOTOR_SPEED;
                leftMotorSpeed = DECELERATION_MOTOR_SPEED;
            }
        } else if ((Math.abs(driveBaseThrottleValue) < DEADBAND)
                && (Math.abs(currentVelocity) <= DECELERATION_VELOCITY_THRESHOLD)
                && !quickTurnFlag) {
            // We are below the velocity threshold, zero the motor values to
            // brake
            rightMotorSpeed = 0.0;
            leftMotorSpeed = 0.0;
        }

        // If we're within the deadband, zero out the throttle and heading
        if ((leftMotorSpeed < DEADBAND && leftMotorSpeed > -DEADBAND)
                && (rightMotorSpeed < DEADBAND && rightMotorSpeed > -DEADBAND)) {
            this.setHeadingValue(0.0);
            this.setThrottleValue(0.0);
            rightMotorSpeed = 0.0;
            leftMotorSpeed = 0.0;
        }

        leftMotorSpeed *= outputScaleFactor;
        rightMotorSpeed *= outputScaleFactor;

        SmartDashboard.putNumber("LeftDriveSpeed", leftMotorSpeed);
        SmartDashboard.putNumber("RightDriveSpeed", rightMotorSpeed);

        Config config = Core.getConfigManager().getConfig();
        double right_drive_bias = config.getDouble(this.getClass().getName() + ".right_drive_bias",
                1.0);
        double left_drive_bias = config.getDouble(this.getClass().getName() + ".left_drive_bias",
                1.0);

        // Update Outputs
        if (rightIndividual == 0 && leftIndividual == 0) {
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.LEFT_2.getName()))
                    .setValue(leftMotorSpeed * left_drive_bias);
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.LEFT_1.getName()))
                    .setValue(leftMotorSpeed * left_drive_bias);
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.RIGHT_1.getName()))
                    .setValue(rightMotorSpeed * right_drive_bias);
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.RIGHT_2.getName()))
                    .setValue(rightMotorSpeed * right_drive_bias);
        } else {
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.RIGHT_1.getName()))
                    .setValue(rightIndividual);
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.RIGHT_2.getName()))
                    .setValue(rightIndividual);
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.LEFT_2.getName()))
                    .setValue(leftIndividual);
            ((AnalogOutput) Core.getOutputManager().getOutput(WSOutputs.LEFT_1.getName()))
                    .setValue(leftIndividual);
        }

    }

    public void checkAutoQuickTurn() {
        double throttle = driveBaseThrottleValue;
        double heading = driveBaseHeadingValue;

        throttle = Math.abs(throttle);
        heading = Math.abs(heading);

        if ((throttle < 0.1) && (heading > 0.1)) {
            quickTurnFlag = true;
        } else {
            quickTurnFlag = false;
        }
    }

    /*
     * ENCODER/GYRO STUFF
     */
    public Encoder getLeftEncoder() {
        return leftDriveEncoder;
    }

    public Encoder getRightEncoder() {
        return rightDriveEncoder;
    }

    public double getLeftEncoderValue() {
        return leftDriveEncoder.get();
    }

    public double getRightEncoderValue() {
        return rightDriveEncoder.get();
    }

    public double getLeftDistance() {
        double distance = 0.0;
        distance = (leftDriveEncoder.get() / (TICKS_PER_ROTATION * ENCODER_GEAR_RATIO)) * 2.0
                * Math.PI * (WHEEL_DIAMETER / 2.0);
        return distance;
    }

    public double getRightDistance() {
        double distance = 0.0;
        distance = (rightDriveEncoder.get() / (TICKS_PER_ROTATION * ENCODER_GEAR_RATIO)) * 2.0
                * Math.PI * (WHEEL_DIAMETER / 2.0);
        return distance;
    }

    public void resetLeftEncoder() {
        leftDriveEncoder.reset();
    }

    public void resetRightEncoder() {
        rightDriveEncoder.reset();
    }

    public Gyro getGyro() {
        return driveHeadingGyro;
    }

    public double getGyroAngle() {
        return driveHeadingGyro.getAngle();
    }

    public void resetGyro() {
        driveHeadingGyro.reset();
    }

    public void setPidSpeedValue(double pidSpeed) {
        // Add the feed forward velocity and acceleration

        pidSpeedValue = pidSpeed;
    }

    public double getPidSpeedValue() {
        // Add the feed forward velocity and acceleration

        return pidSpeedValue;
    }

    public void enableSpeedPidControl() {
        driveSpeedPid.enable();
    }

    public void setDriveSpeedPidSetpoint(double speed) {
        driveSpeedPid.setSetPoint(speed);
        driveSpeedPid.calcPid();
    }

    public void startStraightMoveWithMotionProfile(double distance, double goal_velocity) {
        startMoveWithHeadingAndMotionProfile(distance, goal_velocity, 0.0);
    }

    public void startMoveWithHeadingAndMotionProfile(double distance, double goal_velocity,
            double heading) {
        this.distance_to_move = distance;
        this.distance_moved = 0.0;
        this.distance_remaining = distance;
        this.goal_velocity = goal_velocity;
        motionProfileActive = true;
        overrideHeadingValue(heading);
    }

    public void stopStraightMoveWithMotionProfile() {
        disableSpeedPidControl();
        continuousAccelerationFilter = new ContinuousAccelFilter(0, 0, 0);
        currentProfileX = 0;
        this.distance_to_move = 0.0;
        this.distance_remaining = 0.0;
        this.goal_velocity = 0.0;
        motionProfileActive = false;
        overrideHeadingValue(0.0);
        disableDriveOverride();
    }

    public void disableSpeedPidControl() {
        driveSpeedPid.disable();
        resetSpeedPid();
        s_log.logp(Level.FINE, this.getClass().getName(), "disableSpeedPidControl",
                "Speed PID is disabled");
    }

    public void resetSpeedPid() {
        driveSpeedPid.reset();
    }

    public boolean getQuickTurnFlag() {
        return quickTurnFlag;
    }

    public void notifyConfigChange(Config config) {

        // Changed config params
        ACCELERATION_ENABLED = Core.getConfigManager().getConfig()
                .getBoolean(this.getClass().getName() + ".acceleration_enabled", false);

        WHEEL_DIAMETER = config.getDouble(this.getClass().getName() + ".wheel_diameter", 6);
        TICKS_PER_ROTATION = config.getDouble(this.getClass().getName() + ".ticks_per_rotation",
                360.0);
        THROTTLE_LOW_GEAR_ACCEL_FACTOR = config
                .getDouble(this.getClass().getName() + ".throttle_low_gear_accel_factor", 0.250);
        HEADING_LOW_GEAR_ACCEL_FACTOR = config
                .getDouble(this.getClass().getName() + ".heading_low_gear_accel_factor", 0.500);
        THROTTLE_HIGH_GEAR_ACCEL_FACTOR = config
                .getDouble(this.getClass().getName() + ".throttle_high_gear_accel_factor", 0.125);
        HEADING_HIGH_GEAR_ACCEL_FACTOR = config
                .getDouble(this.getClass().getName() + ".heading_high_gear_accel_factor", 0.250);
        MAX_HIGH_GEAR_PERCENT = config
                .getDouble(this.getClass().getName() + ".max_high_gear_percent", 0.80);
        ENCODER_GEAR_RATIO = config.getDouble(this.getClass().getName() + ".encoder_gear_ratio",
                7.5);
        SLOW_TURN_FORWARD_SPEED = config
                .getDouble(this.getClass().getName() + ".slow_turn_forward_speed", 0.16);
        SLOW_TURN_BACKWARD_SPEED = config
                .getDouble(this.getClass().getName() + ".slow_turn_backward_speed", -0.19);
        FEED_FORWARD_VELOCITY_CONSTANT = config
                .getDouble(this.getClass().getName() + ".feed_forward_velocity_constant", 1.00);
        FEED_FORWARD_ACCELERATION_CONSTANT = config.getDouble(
                this.getClass().getName() + ".feed_forward_acceleration_constant", 0.00018);
        MAX_ACCELERATION_DRIVE_PROFILE = config
                .getDouble(this.getClass().getName() + ".max_acceleration_drive_profile", 600.0);
        MAX_SPEED_INCHES_LOWGEAR = config
                .getDouble(this.getClass().getName() + ".max_speed_inches_lowgear", 90.0);
        DEADBAND = config.getDouble(this.getClass().getName() + ".deadband", 0.1);
        DECELERATION_VELOCITY_THRESHOLD = config
                .getDouble(this.getClass().getName() + ".deceleration_velocity_threshold", 48.0);
        DECELERATION_MOTOR_SPEED = config
                .getDouble(this.getClass().getName() + ".deceleration_motor_speed", 0.3);
        STOPPING_DISTANCE_AT_MAX_SPEED_LOWGEAR = config.getDouble(
                this.getClass().getName() + ".stopping_distance_at_max_speed_lowgear", 10.0);
        QUICK_TURN_CAP = config.getDouble(this.getClass().getName() + ".quick_turn_cap", 10.0);
        QUICK_TURN_ANTITURBO = config.getDouble(this.getClass().getName() + ".quick_turn_antiturbo",
                10.0);
        SUPER_ANTITURBO_FACTOR = config
                .getDouble(this.getClass().getName() + ".super_antiturbo_factor", 0.25);
        ACCELERATION_ENABLED = Core.getConfigManager().getConfig()
                .getBoolean(this.getClass().getName() + ".acceleration_enabled", false);
        outputScaleFactor = config.getDouble(this.getClass().getName() + ".output_scale_factor",
                1.0);
        driveSpeedPid.notifyConfigChange();
    }

    @Override
    public void inputUpdate(Input source) {
        if (!isPistolGrip) {
            if (source.getName().equals(WSInputs.DRV_BUTTON_6.getName())) {
                if (((DigitalInput) source).getValue()) {
                    highGearFlag = !highGearFlag;
                }
            } else if (source.getName().equals(WSInputs.DRV_BUTTON_8.getName())) {
                turboFlag = ((DigitalInput) source).getValue();
            }
        } else {
            if (source.getName().equals(WSInputs.DRV_BUTTON_8.getName())) {
                highGearFlag = ((DigitalInput) source).getValue();
            } else if (source.getName().equals(WSInputs.DRV_BUTTON_6.getName())) {
                turboFlag = ((DigitalInput) source).getValue();
            }
        }
        if (source.getName().equals(WSInputs.DRV_BUTTON_5.getName())) {
            superAntiTurboFlag = ((DigitalInput) source).getValue();
        } else if (source.getName().equals(WSInputs.MOTION_PROFILE_CONTROL.getName())) {
            motionProfileActive = ((WsMotionProfileControl) source).getProfileEnabled();
            if (((WsMotionProfileControl) source).getResetKinematics()) {
                resetKinematics();
            }
        } else if (source.getName().equals(WSInputs.DRV_THROTTLE.getName())) {
            throttleValue = ((AnalogInput) Core.getInputManager()
                    .getInput(WSInputs.DRV_THROTTLE.getName())).getValue();
            SmartDashboard.putNumber("throttleValue", throttleValue);
        } else if (source.getName().equals(WSInputs.DRV_HEADING.getName())) {
            headingValue = ((AnalogInput) Core.getInputManager()
                    .getInput(WSInputs.DRV_HEADING.getName())).getValue();
            headingValue *= -1;
            SmartDashboard.putNumber("heading value", headingValue);
        }

    }

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return "Drive Base";
    }

    public double getSpeedError() {
        return driveSpeedPid.getError();
    }

    public double getDeltaPosError() {
        return deltaPosError;
    }

    private double getStoppingDistanceFromDistanceToMove(double distance) {
        if (Math.abs(distance) > 40.0) {
            return STOPPING_DISTANCE_AT_MAX_SPEED_LOWGEAR;
        } else {
            return (STOPPING_DISTANCE_AT_MAX_SPEED_LOWGEAR
                    - ((3.0f / 15.0f) * (40 - Math.abs(distance))));
        }
    }

    public void setShifter(boolean highGear) {
        highGearFlag = highGear;
    }

    public boolean shifterState() {
        return highGearFlag;
    }

    public void setSuperDriveOverride(boolean state) {
        AutoDriveOverride = state;
    }

    public void setLeftDrive(double speed) {
        rightIndividual = 0;
        leftIndividual = speed;
    }

    public void setRightDrive(double speed) {
        leftIndividual = 0;
        rightIndividual = speed;
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }
}