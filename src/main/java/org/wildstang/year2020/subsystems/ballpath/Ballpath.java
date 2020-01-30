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
    private TalonSRX feedMotor;
    private TalonSRX kickerMotor;
    private TalonSRX intakeMotor;

    // Constants 
    //What is put into setting motors
    private double feedMotorSpeed;
    private double kickerMotorSpeed;
    private double intakeMotorSpeed;
    
    private final double fullSpeed = 1.0;
    private final double reverseSpeed = -0.4;
    private boolean rightTriggerRunning = false;
    private boolean YButtonRunning = false;
    private boolean AButtonRunning = false;

    // Status for each motor
    private AnalogInput rightTrigger;
    private DigitalInput YButton;
    private DigitalInput AButton;

    @Override
    public void inputUpdate(Input source) {
    //they should act as a hold buttons 
    //ie. when pressed down 

        if (rightTrigger.getValue() > 0.75) {
            //runs the hopper motor full power and the kicker motor full power
            feedMotorSpeed = fullSpeed;
            kickerMotorSpeed = fullSpeed;
            
        } else if (source == YButton) {
            //runs hopper motor and kicker motor backwards at ~40% power

            if (YButtonRunning == true) {
                YButtonRunning = false;
                feedMotorSpeed = 0;
                kickerMotorSpeed = 0;

            } else {
                YButtonRunning = true;
                feedMotorSpeed = reverseSpeed;
                kickerMotorSpeed = reverseSpeed;

            }
        }

        if (source == AButton) {
            //run intake motor at 100% power

            if (YButtonRunning == true) {
                YButtonRunning = false;
                intakeMotorSpeed = 0;

            } else {
                YButtonRunning = true;
                intakeMotorSpeed = fullSpeed;

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
        feedMotor = new TalonSRX(CANConstants.BALLPATH_FEED);
        kickerMotor = new TalonSRX(CANConstants.BALLPATH_KICKER);
        intakeMotor = new TalonSRX(CANConstants.BALLPATH_INTAKE);
    }


    private void initInputs() {
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.RIGHT_TRIGGER.getName());
        rightTrigger.addInputListener(this);
        YButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.Y_BUTTON.getName());
        YButton.addInputListener(this);
        AButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.A_BUTTON.getName());
        AButton.addInputListener(this);
    }

}