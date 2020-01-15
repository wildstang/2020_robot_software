package org.wildstang.year2020.subsystems.climb;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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
    // TODO Assuming that climb will be controlled by the Select button (8)
    private DigitalInput selectButton;
    // If we want manual control of both climb motors, add another climb button
    // private DigitialInput climbButton2;
    // TODO Needs proper reference to Falcon motors (if not named WsFalcon)
    private WsFalcon climbMotor1;
    private WsFalcon climbMotor2;
    private double motorspeed;

    // Status booleans for climb, these should be added in resetState method
    private boolean climbInputStatus; // To check if climb button is pressed
    private boolean climbActiveStatus; // For Shuffleboard

    @Override
    public void inputUpdate(Input source) {
        if (source == selectButton) {
            climbInputStatus = selectButton.getValue();
            motorspeed = -1.0; // Might need to be properly adjusted for Falcon motors
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
        //TODO Assuming the use of TalonSRX motors
        climbMotor1 = new TalonSRX(CANConstants.CLIMB_TALON_1);
        climbMotor2 = new TalonSRX(CANConstants.CLIMB_TALON_2);
    }

    private void initInputs() {
        //TODO Assuming the use of the "Select" button (8)
        selectButton = (DigitalInput) inputManager.getInput(WSInputs.CLIMB_SELECT);
    }


}