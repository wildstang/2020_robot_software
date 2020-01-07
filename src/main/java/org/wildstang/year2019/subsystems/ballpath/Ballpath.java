package org.wildstang.year2019.subsystems.ballpath;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2019.robot.WSInputs;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2019.robot.WSOutputs;
import org.wildstang.year2019.robot.CANConstants;
import org.wildstang.year2019.robot.Robot;
import org.wildstang.hardware.crio.outputs.WsSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoidState;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This subsystem is responsible for handling cargo from entry to exit in the
 * robot.
 * 
 * This subsystem includes the intake, ball hopper, and carriage machinery.
 * 
 * Sensors:
 * <ul>
 * <li>Ball presence detector (beam break or something?) in carriage
 * </ul>
 * 
 * Actuators:
 * <ul>
 * <li>Intake roller motor
 * <li>Intake deploy piston solenoid
 * <li>Hopper belt drive motor
 * <li>Hopper belt position piston solenoid
 * <li>Carriage roller
 * </ul>
 * 
 */
public class Ballpath implements Subsystem {

    // Constants
    private static final double ROLLER_SPEED = 1.0;
    private static final double ROLLER_SPEED_SLOWED_1 = 0.8; // For sensor A
    private static final double ROLLER_SPEED_SLOWED_2 = 0.54; // For sensor A + B
    private static final double ROLLER_SPEED_BRAKE = 0.0; // For setting to zero AND sensor B
    private static final double BACKWARDS_ROLLER_SPEED = -1.0;
    private static final double CARRIAGE_ROLLER_SPEED = 1.0;// subject to change
    private static final double PHYSICAL_DIR_CHANGE = -1;

    // Inputs
    private AnalogInput carriageRollersInput;
    private DigitalInput intakeInput;
    private DigitalInput fullBallpathInput;
    private DigitalInput reverseInput;
    private DigitalInput hopperInput;
    private DigitalInput safetyInput;
    private DigitalInput otbInput;

    // UPDATE code for sensors
    // private DigitalInput Sensor_A_Input;//controlled by sensor values, sensors to
    // be set later
    // private DigitalInput Sensor_B_Input;//controlled by sensor values, sensors to
    // be set later

    // Solenoids
    private WsSolenoid hopper_solenoid;
    private WsDoubleSolenoid intake_solenoid;

    // Victors
    private VictorSPX intakeVictor;
    private VictorSPX hopperVictor1;
    private VictorSPX hopperVictor2;
    private VictorSPX carriageVictor;

    // Values updated by inputs
    private boolean reverseValue;
    private boolean hopper_position;
    private boolean intake_position;
    private boolean isIntake_motor;
    private boolean isCarriageMotor;
    private boolean isHopper_motor;
    private boolean Sensor_A_Value;
    private boolean Sensor_B_Value;
    private boolean carriage_slowed;
    private boolean transferCarriage;
    private boolean isOTB;
    private boolean otbPrev;
    private boolean otbCurrent;

    /**
     * TODO: Names set up for each Victor that we are going to need TODO: Add
     * variables that can be updated when buttons are pressed down
     * 
     */

    public void enableIntake(boolean enable) {
        isIntake_motor = enable;
        intake_position = enable;
    }

    public void enableWholePath(boolean enable) {
        isIntake_motor = enable;
        isCarriageMotor = enable;
        isHopper_motor = enable;
    }

    @Override
    public void inputUpdate(Input source) {
        // Set up 4 buttons
        /**
         * 1 button (INTAKE) to deploy the hopper intake and run the motors (HOLD) 1
         * button (HOPPER_SOLENOID) to actuate the hopper rollers (HOLD) 1 button
         * (CARRIAGE_ROLLERS) to run carriage rollers (JOYSTICK) 1 button
         * (FULL_BALLPATH) to deploy the hopper intake, run the intake motors, run the
         * hopper rollers, and the carriage rollers (HOLD)
         * 
         * Update local variables
         */

        // UPDATE when sensors are live
        // Sensor_A_Value = Sensor_A_Input.getValue();
        // Sensor_B_Value = Sensor_B_Input.getValue();

        // Check if we are running in reverse or not
        if (reverseInput.getValue()) {
            reverseValue = true;
            // intake_position = true;
        } else {
            reverseValue = false;
        }

        if (hopperInput.getValue()) {
            hopper_position = true;
        } else {
            hopper_position = false;
        }

        if (carriageRollersInput.getValue() > 0.75) {
            isCarriageMotor = true;
            carriage_slowed = false;
        } else {
            isCarriageMotor = false;
        }

        // check if everything should be activated
        if (fullBallpathInput.getValue()) {
            intake_position = true;
            isIntake_motor = true;
            isHopper_motor = true;
            hopper_position = true;

            // If carriage motor is already running because of the carriage input, that
            // should override our logic in this branch
            if (isCarriageMotor == false) {
                if (Sensor_B_Value || Sensor_A_Value) {
                    carriage_slowed = true;
                    isCarriageMotor = true;
                } else if (Sensor_B_Value && !Sensor_A_Value) {
                    isCarriageMotor = false;
                } else { // neither sensor pressed
                    carriage_slowed = false;
                    isCarriageMotor = true;
                }
            }
        }
        // Everything is not activated so we check each indivigual button!
        else {
            if (intakeInput.getValue()) {
                isCarriageMotor = true;
                isHopper_motor = true;
                isIntake_motor = true;

                transferCarriage = true;
            } else {
                isHopper_motor = false;
                intake_position = false;
                isIntake_motor = false;
                transferCarriage = false;
            }
        }
        if(source == otbInput) {
                otbCurrent = otbInput.getValue();
                if (otbCurrent && !otbPrev) {
                    isOTB = !isOTB;
                } 
            otbPrev = otbCurrent;
        }
    }

    @Override
    public void init() {

        // Input listeners
        intakeInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.INTAKE.getName());
        intakeInput.addInputListener(this);
        carriageRollersInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.CARRIAGE_ROLLERS);
        carriageRollersInput.addInputListener(this);
        fullBallpathInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.FULL_BALLPATH.getName());
        fullBallpathInput.addInputListener(this);
        hopperInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.HOPPER_SOLENOID.getName());
        hopperInput.addInputListener(this);
        reverseInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.REVERSE_BUTTON.getName());
        reverseInput.addInputListener(this);

        otbInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.BUMPER.getName());
        otbInput.addInputListener(this); //Ben's OTB toggle button
        // Sensor_A_Input = (DigitalInput)
        // Core.getInputManager().getInput(WSInputs.CARRIAGE_SENSOR_A.getName());
        // Sensor_A_Input.addInputListener(this);
        // Sensor_B_Input = (DigitalInput)
        // Core.getInputManager().getInput(WSInputs.CARRIAGE_SENSOR_B.getName());
        // Sensor_B_Input.addInputListener(this);
        // uncomment when sensors are live

        // Button to not run carriage motors while lift limit override
        safetyInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.WEDGE_SAFETY_2.getName());
        safetyInput.addInputListener(this);

        // Solenoids
        hopper_solenoid = (WsSolenoid) Core.getOutputManager().getOutput(WSOutputs.HOPPER_SOLENOID.getName());
        intake_solenoid = (WsDoubleSolenoid) Core.getOutputManager().getOutput(WSOutputs.INTAKE_SOLENOID.getName());

        // WsVictors
        intakeVictor = new VictorSPX(CANConstants.INTAKE_VICTOR);
        hopperVictor1 = new VictorSPX(CANConstants.HOPPER_VICTOR1);
        hopperVictor2 = new VictorSPX(CANConstants.HOPPER_VICTOR2);
        carriageVictor = new VictorSPX(CANConstants.CARRIAGE_VICTOR);
        resetState();
    }

    @Override
    public void selfTest() {
        // TODO
    }

    @Override
    public void update() {
        /**
         * If INTAKE is pressed down then deploy the intake and set the intake motors to
         * run - Deploy pistons that put the intake mech into position - Set motors to
         * run at full power
         * 
         * If HOPPER_SOLENOID is pressed then actuate the piston. In when it's false,
         * out when it's true - Set the value of the solenoid while the button is being
         * pressed
         * 
         * If CARRIAGE_ROLLER is pressed then run the carriage motors - JOYSTICK? Button
         * HOLD? - Button - Set the motors to run at full power - JOYSTICK - Set the
         * value of the
         * 
         * If EVERYTHING is pressed when deploy the intake, run the intake motors,
         * carriage and hopper rollers
         * 
         */
        
        hopper_solenoid.setValue(hopper_position);
        SmartDashboard.putBoolean("Hopper Position", hopper_position);
        
        SmartDashboard.putBoolean("Intake Position", intake_position);
        SmartDashboard.putBoolean("isOTB", isOTB);
        if(isOTB) {
             intake_solenoid.setValue(WsDoubleSolenoidState.REVERSE.ordinal());
         } else{
            if (intake_position){
                intake_solenoid.setValue(WsDoubleSolenoidState.REVERSE.ordinal());
            } else {
                intake_solenoid.setValue(WsDoubleSolenoidState.FORWARD.ordinal());
            }
        }   
        if (isIntake_motor) {
            intakeVictor.set(ControlMode.PercentOutput, ROLLER_SPEED);
            SmartDashboard.putNumber("Intake Speed", ROLLER_SPEED);
        } else {
            intakeVictor.set(ControlMode.PercentOutput, 0);
            SmartDashboard.putNumber("Intake Speed", 0);
        }

        if (isCarriageMotor) {
            if ((carriage_slowed && Sensor_B_Value) || transferCarriage) {
                carriageVictor.set(ControlMode.PercentOutput, PHYSICAL_DIR_CHANGE * ROLLER_SPEED_SLOWED_2);
            } else if (carriage_slowed) {
                carriageVictor.set(ControlMode.PercentOutput, PHYSICAL_DIR_CHANGE * ROLLER_SPEED_SLOWED_1);
            } else
                carriageVictor.set(ControlMode.PercentOutput, PHYSICAL_DIR_CHANGE * CARRIAGE_ROLLER_SPEED);

        } else {
            carriageVictor.set(ControlMode.PercentOutput, 0);
        }
        SmartDashboard.putNumber("Carriage Speed", carriageVictor.getMotorOutputPercent());
        if (isHopper_motor) {
            hopperVictor1.set(ControlMode.PercentOutput, PHYSICAL_DIR_CHANGE * ROLLER_SPEED);
            hopperVictor2.set(ControlMode.PercentOutput,  ROLLER_SPEED);

        } else {
            hopperVictor1.set(ControlMode.PercentOutput, 0);
            hopperVictor2.set(ControlMode.PercentOutput, 0);
        }
        SmartDashboard.putNumber("Hopper Speed (Victor 1)", hopperVictor1.getMotorOutputPercent());
        SmartDashboard.putNumber("Hopper Speed (Victor 2)", hopperVictor2.getMotorOutputPercent());

        if (reverseValue) {
            hopperVictor1.set(ControlMode.PercentOutput, PHYSICAL_DIR_CHANGE * BACKWARDS_ROLLER_SPEED);
            hopperVictor2.set(ControlMode.PercentOutput, BACKWARDS_ROLLER_SPEED);
            carriageVictor.set(ControlMode.PercentOutput, PHYSICAL_DIR_CHANGE * BACKWARDS_ROLLER_SPEED);
            intakeVictor.set(ControlMode.PercentOutput, BACKWARDS_ROLLER_SPEED);
        }
    }

    @Override
    public void resetState() {
        hopper_solenoid.setValue(false);
        intake_solenoid.setValue(WsDoubleSolenoidState.FORWARD.ordinal());
        intakeVictor.set(ControlMode.PercentOutput, 0.0);
        carriageVictor.set(ControlMode.PercentOutput, 0.0);
        hopperVictor1.set(ControlMode.PercentOutput, 0.0);
        hopperVictor2.set(ControlMode.PercentOutput, 0.0);

        isOTB = false;
        otbPrev = false;
        otbCurrent = false;

        // Set desired positions for solenoids
    }

    public void runCarriage() {
        carriageVictor.set(ControlMode.PercentOutput, CARRIAGE_ROLLER_SPEED);
    }

    @Override
    public String getName() {
        return "Ballpath";
    }
    public void setIntake(){
        //intake_solenoid.setValue(true);
    }
}