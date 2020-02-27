package org.wildstang.year2020.subsystems.ballpath;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.timer.WsTimer;
import org.wildstang.year2020.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Map;

import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.core.Core;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class Ballpath implements Subsystem{

    //Motors
    private TalonSRX feedMotor;
    private TalonSRX hopperMotor;
    private TalonSRX kickerMotor;
    private TalonSRX intakeMotor;

    //Motor Speeds
    private double feedMotorSpeed;
    private double intakeMotorSpeed;
    private double hopperSlow;

    //Shuffleboard entries
    private ShuffleboardTab driveTab;
    private NetworkTableEntry maxDriveInputEntry;

    //Constants
    private final double FULL_SPEED = 1.0;
    private final double KICKER_MOTOR_CONSTANT = 0.7;
    private final double REVERSE_SPEED = -0.4;

    //Inputs
    private AnalogInput rightTrigger;
    private DigitalInput dpadRight;
    private DigitalInput xButton;

    @Override
    public void inputUpdate(Input source) {
        //set feed and hopper motor speeds
        if (Math.abs(rightTrigger.getValue())>0.75){
            feedMotorSpeed = FULL_SPEED;
        } else if (dpadRight.getValue()){
            feedMotorSpeed = REVERSE_SPEED;
        } else {
            feedMotorSpeed = 0;
        }
        //set intake motor speed
        if (xButton.getValue()){
            intakeMotorSpeed = FULL_SPEED;
        } else {
            intakeMotorSpeed = 0;
        }
    }

    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }
    private void initInputs(){
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_RIGHT.getName());
        rightTrigger.addInputListener(this);
        dpadRight = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_RIGHT.getName());
        dpadRight.addInputListener(this);
        xButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT.getName());
        xButton.addInputListener(this);
        driveTab = Shuffleboard.getTab("Drive");
        maxDriveInputEntry = driveTab.add("Max Input", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
    }

    private void initOutputs(){
        feedMotor = new TalonSRX(CANConstants.BALLPATH_FEED);
        hopperMotor = new TalonSRX(CANConstants.BALLPATH_HOPPER);
        hopperMotor.setInverted(true);
        kickerMotor = new TalonSRX(CANConstants.BALLPATH_KICKER);
        intakeMotor = new TalonSRX(CANConstants.BALLPATH_INTAKE);
        kickerMotor.setInverted(true);
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        
        hopperSlow = maxDriveInputEntry.getDouble(1.0);
        
        SmartDashboard.putNumber("intake speedd", intakeMotorSpeed);
        feedMotor.set(ControlMode.PercentOutput, feedMotorSpeed);
        hopperMotor.set(ControlMode.PercentOutput, feedMotorSpeed * hopperSlow);
        intakeMotor.set(ControlMode.PercentOutput, intakeMotorSpeed);
        kickerMotor.set(ControlMode.PercentOutput, feedMotorSpeed * KICKER_MOTOR_CONSTANT);   
    }

    @Override
    public void resetState() {
        feedMotorSpeed = 0.0;
        intakeMotorSpeed = 0.0;
        hopperSlow=1.0;
    }

    @Override
    public String getName() {
        return "Ballpath";
    }
    public void turnOnIntake(){
        intakeMotorSpeed = FULL_SPEED;
    }
    public void turnOnFeed(){
        feedMotorSpeed = FULL_SPEED;
    }
    public void turnOffFeed(){
        feedMotorSpeed = 0.0;
    }
}