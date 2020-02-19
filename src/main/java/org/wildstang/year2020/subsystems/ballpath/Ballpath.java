package org.wildstang.year2020.subsystems.ballpath;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.timer.WsTimer;
import org.wildstang.year2020.robot.WSInputs;
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
    private double kickerMotorSpeed;
    private double intakeMotorSpeed;

    //Constants
    private final double FULL_SPEED = 1.0;
    private final double REVERSE_SPEED = -0.4;
    private final double TIME_PASSED = 1.0;

    //Inputs
    private AnalogInput rightTrigger;
    private DigitalInput yButton;
    private DigitalInput aButton;
    private DigitalInput startButton;
    private DigitalInput selectButton;
    private WsTimer timer = new WsTimer();

    private boolean running;
    private boolean kickerOn;

    @Override
    public void inputUpdate(Input source) {
        //set feed and hopper motor speeds
        if (Math.abs(rightTrigger.getValue())>0.75){
            feedMotorSpeed = FULL_SPEED;
        } else if (yButton.getValue()){
            feedMotorSpeed = REVERSE_SPEED;
        } else {
            feedMotorSpeed = 0;
        }
        //set intake motor speed
        if (aButton.getValue()){
            intakeMotorSpeed = FULL_SPEED;
        } else {
            intakeMotorSpeed = 0;
        }
        running = !selectButton.getValue();
        if (startButton.getValue()){
            if (!running){
                timer.reset();
                running = true;
            }
            
        } else {
            running = false;
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
        yButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP.getName());
        yButton.addInputListener(this);
        aButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN.getName());
        aButton.addInputListener(this);
        startButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_START.getName());
        startButton.addInputListener(this);
        selectButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SELECT.getName());
        selectButton.addInputListener(this);
    }

    private void initOutputs(){
        feedMotor = new TalonSRX(CANConstants.BALLPATH_FEED);
        hopperMotor = new TalonSRX(CANConstants.BALLPATH_HOPPER);
        hopperMotor.follow(feedMotor);
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
        kickerMotorSpeed = 0.7*FULL_SPEED;
        feedMotor.set(ControlMode.PercentOutput, feedMotorSpeed);
        intakeMotor.set(ControlMode.PercentOutput, intakeMotorSpeed);

        if (running && timer.hasPeriodPassed(TIME_PASSED)){
            timer.reset();
            running = false;
            kickerOn = !kickerOn;
        } 
        if (kickerOn){
            kickerMotor.set(ControlMode.PercentOutput, kickerMotorSpeed);
        } else {
            kickerMotor.set(ControlMode.PercentOutput, 0.0);
        }
    }

    @Override
    public void resetState() {
        feedMotorSpeed = 0.0;
        kickerMotorSpeed = 0.0;
        intakeMotorSpeed = 0.0;
        kickerOn = true;
        running = false;
        timer.start();
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