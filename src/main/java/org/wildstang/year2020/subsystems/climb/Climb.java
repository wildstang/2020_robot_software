package org.wildstang.year2020.subsystems.climb;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.Robot;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;


public class Climb implements Subsystem {

    // Add variable definitions here
    // Inputs for extending climb
    private DigitalInput selectButton;
    private DigitalInput startButton;
    // Inputs for retracting climb
    private DigitalInput leftBumper;
    private DigitalInput rightBumper;

    private VictorSPX climbMotor1;
    private VictorSPX climbMotor2;
    private double motorspeed;

    // Status booleans for climb, these should be added in resetState method
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
            climbMotor1.setValue(motorspeed);
            climbMotor2.setValue(motorspeed);
        }
        // If anything else, set motorspeed to 0
        else {
            climbActiveStatus = false; // For Shuffleboard
            climbMotor1.setValue(0);
            climbMotor2.setValue(0);
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
        startButton = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_START);
        leftBumper = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_LEFT_BUMPER);
        rightBumper = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_RIGHT_BUMPER);
    }


}