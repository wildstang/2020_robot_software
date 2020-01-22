package org.wildstang.year2020.subsystems.climb;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
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
    private DigitalInput leftBumper;
    private DigitalInput rightBumper;

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
        if (source == selectButton && startButton) {
            climbInputStatus = selectButton.getValue();
            motorspeed = -1.0; // Extends climb
        } else if (source == leftBumper && rightBumper) {
            climbInputStatus = leftBumper.getValue();
            motorspeed = 1.0; // Retracts climb
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
        climbMotor1 = new VictorSPX(CANConstants.CLIMB_VICTOR_1);
        climbMotor2 = new VictorSPX(CANConstants.CLIMB_VICTOR_2);
    }

    private void initInputs() {
        selectButton = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_SELECT);
        selectButton.addInputListener(this);
        startButton = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_START);
        startButton.addInputListener(this);
        leftBumper = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_LEFT_BUMPER);
        leftBumper.addInputListener(this);
        rightBumper = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_RIGHT_BUMPER);
        rightBumper.addInputListener(this);
    }

}