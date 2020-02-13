package org.wildstang.year2020.subsystems.ballpath;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.core.Core;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import org.wildstang.framework.timer.WsTimer;

/**
 * Class:       Ballpath.java
 * Inputs:      Right trigger, "A" and "Y" buttons
 * Outputs:     4 talons (feed, feed2, kicker, intake)
 * Description: The ballpath subsystem helps feed the power cells in the hopper to the turret.
 *              The intake motor pulls the power cells into the ballpath which then moves balls throught it with the feed motor.
 *              The right trigger controls both of these motors simultaneously and the "Y" button reverses their direction.
 *              The power cells are finally handed off to the turret by the kicker motor, controlled by the "A" button.
 */
public class Ballpath implements Subsystem {

    // Motors
    private TalonSRX feedMotor;
    private TalonSRX feedMotor2;
    private TalonSRX kickerMotor;
    private TalonSRX intakeMotor;

    // Motor Speeds
    private double feedMotorSpeed;
    private double kickerMotorSpeed;
    private double intakeMotorSpeed;
    
    // Constants
    private final double FULL_SPEED = 1.0;
    private final double REVERSE_SPEED = -0.4;
    private final double TIME_PASSED = 1.0;

    // Inputs
    private AnalogInput rightTrigger;
    private DigitalInput yButton;
    private DigitalInput aButton;
    private DigitalInput startButton;
    private DigitalInput selectButton;
    private WsTimer timer = new WsTimer();

    //Logic Variables 
    private boolean running;
    private boolean kickerOn;
    private boolean selectPushed;


    /**
     * Update Methods
     */

    @Override
    public void inputUpdate(Input source) {
        if (rightTrigger.getValue() > 0.75) {
            // run the hopper and kicker motors at full power
            feedMotorSpeed = FULL_SPEED;
            // kickerMotorSpeed = FULL_SPEED;
            
        } else if (yButton.getValue()) {
            // run the hopper and kicker motors backwards at ~40% power
            feedMotorSpeed = REVERSE_SPEED;
           // kickerMotorSpeed = REVERSE_SPEED;

        } else {
            // don't run the motors if neither button is pressed
            feedMotorSpeed = 0;
           // kickerMotorSpeed = 0;
        }

        if (aButton.getValue()) {
            // run intake motor at 100% power
            intakeMotorSpeed = FULL_SPEED;
                
        } else {
            intakeMotorSpeed = 0;
        }

        if (selectButton.getValue()) {
            selectPushed = true;
        } else {
            selectPushed = false;
        }

        if (startButton.getValue()) {
            timer.reset();
            running = true;
        } else {
            running = false;
        }
    }

    @Override
    public void update() {
        feedMotor.set(ControlMode.PercentOutput, feedMotorSpeed);
        kickerMotor.set(ControlMode.PercentOutput, kickerMotorSpeed);
        intakeMotor.set(ControlMode.PercentOutput, intakeMotorSpeed);


        if (running == true && timer.hasPeriodPassed(TIME_PASSED) && selectPushed == false) {
            timer.reset();
            running = false; 
            
            if (kickerOn == true){
                kickerMotorSpeed = 0.0;
                kickerOn = false;
            } else {
                kickerMotorSpeed = FULL_SPEED;
                kickerOn = true;
            }
        }
    }

    /**
     * Auto Methods
     */

    public void turnOnIntake() {
        intakeMotorSpeed = FULL_SPEED;
    }

    public void turnOnFeed() {
        feedMotorSpeed = FULL_SPEED;
    }

    public void turnOffFeed() {
        feedMotorSpeed = 0;
    }

    /**
     * Subsystem Initialization Methods
     */

    @Override
    public void init() {
        initInputs();
        initOutputs();
        timer.start();
        resetState();
    }

    private void initInputs() {
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

    private void initOutputs() {
        feedMotor = new TalonSRX(CANConstants.BALLPATH_FEED);
        feedMotor2 = new TalonSRX(CANConstants.BALLPATH_FEED_2);
        feedMotor2.follow(feedMotor);
        kickerMotor = new TalonSRX(CANConstants.BALLPATH_KICKER);
        intakeMotor = new TalonSRX(CANConstants.BALLPATH_INTAKE);
    }

    @Override
    public void resetState() {
        feedMotorSpeed = 0;
        kickerMotorSpeed = FULL_SPEED;
        intakeMotorSpeed = 0;
        kickerOn = true;
    }

    /**
     * Meta Methods
     */

    @Override
    public String getName() {
        return "Ballpath";
    }

    @Override
    public void selfTest() {}
}