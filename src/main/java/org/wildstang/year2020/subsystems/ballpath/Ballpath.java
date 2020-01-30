package org.wildstang.year2020.subsystems.ballpath;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSOutputs;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;


public class Ballpath implements Subsystem {

    // Motors
    private VictorSPX feedMotor;
    private VictorSPX kickerMotor;
    private VictorSPX intakeMotor;

    // Constants 
    //What is put into setting motors
    private double feedMotorSpeed;
    private double kickerMotorSpeed;
    private double intakeMotorSpeed;
    
    private final double FULL_SPEED = 1.0;
    private final double REVERSE_SPEED = -0.4;

    // Status for each motor
    private AnalogInput rightTrigger;
    private DigitalInput yButton;
    private DigitalInput aButton;

    @Override
    public void inputUpdate(Input source) {
    //they should act as a hold buttons 
    //ie. when pressed down 

        if (rightTrigger.getValue() > 0.75) {
            //runs the hopper motor full power and the kicker motor full power
            feedMotorSpeed = FULL_SPEED;
            kickerMotorSpeed = FULL_SPEED;
            
        } else if (yButton.getValue()) {
            //runs hopper motor and kicker motor backwards at ~40% power
            feedMotorSpeed = REVERSE_SPEED;
            kickerMotorSpeed = REVERSE_SPEED;

        } else {
                feedMotorSpeed = 0;
                kickerMotorSpeed = 0;
        }
    

        if (aButton.getValue()) {
            //run intake motor at 100% power
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

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
            feedMotor.set(ControlMode.PercentOutput, feedMotorSpeed);
            kickerMotor.set(ControlMode.PercentOutput, kickerMotorSpeed);
            intakeMotor.set(ControlMode.PercentOutput, intakeMotorSpeed);
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        feedMotorSpeed = 0;
        kickerMotorSpeed = 0;
        intakeMotorSpeed = 0;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Ballpath";
    }

    private void initOutputs() {
        feedMotor = new VictorSPX(CANConstants.BALLPATH_FEED);
        kickerMotor = new VictorSPX(CANConstants.BALLPATH_KICKER);
        intakeMotor = new VictorSPX(CANConstants.BALLPATH_INTAKE);
    }


    private void initInputs() {
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.RIGHT_TRIGGER.getName());
        rightTrigger.addInputListener(this);
        yButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.Y_BUTTON.getName());
        yButton.addInputListener(this);
        aButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.A_BUTTON.getName());
        aButton.addInputListener(this);
    }
}
