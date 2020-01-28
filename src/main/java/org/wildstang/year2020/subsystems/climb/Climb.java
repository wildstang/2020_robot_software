package org.wildstang.year2020.subsystems.climb;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.timer.WsTimer;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.Robot;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;

public class Climb implements Subsystem {

    // Inputs
    private DigitalInput selectButton;
    private DigitalInput startButton;

    // Outputs
    private VictorSPX climbMotor1;
    private VictorSPX climbMotor2;

    // Variables
    private double motorspeed;

    // Statuses
    private boolean climbInputStatus;
    private boolean climbActiveStatus; // For Shuffleboard

    @Override
    public void inputUpdate(Input source) {
        if (source == selectButton && source == startButton) {
            climbInputStatus = true;
            motorspeed = 1.0; // Extends climb
        } else {
            climbInputStatus = false;
            motorspeed = 0; // Retracts climb via elastic magic
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
         // If button is pressed, set the motorspeed to the defined value in the inputUpdate method
        if (climbInputStatus) {
            climbActiveStatus = true; // For Shuffleboard
            climbMotor1.set(ControlMode.PercentOutput, motorspeed);
            climbMotor2.set(ControlMode.PercentOutput, motorspeed);
        }
        // If anything else, set motorspeed to 0
        else {
            climbActiveStatus = false; // For Shuffleboard
            climbMotor1.set(ControlMode.PercentOutput, 0);
            climbMotor2.set(ControlMode.PercentOutput, 0);
        }
    }

    @Override
    public void resetState() {
        climbInputStatus = false;
        climbActiveStatus = false;
    }

    @Override
    public String getName() {
        return "Climb";
    }

    private void initOutputs() {
        //CANConstants.CLIMB_VICTOR_1.getName()????
        climbMotor1 = new VictorSPX(CANConstants.CLIMB_VICTOR_1);
        climbMotor2 = new VictorSPX(CANConstants.CLIMB_VICTOR_2);
    }

    private void initInputs() {
        IInputManager inputManager = Core.getInputManager();
        selectButton = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_SELECT.getName());
        selectButton.addInputListener(this);
        startButton = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_START.getName());
        startButton.addInputListener(this);
    }

}