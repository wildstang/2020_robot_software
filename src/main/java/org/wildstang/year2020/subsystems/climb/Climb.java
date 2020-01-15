package org.wildstang.year2020.subsystems.climb;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsFalcon;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;
import org.wildstang.year2020.robot.Robot;

public class Climb implements Subsystem {

    // Add variable definitions here
    // TODO Rename climbButton variable to the actual button on controller for easier reading
    private DigitalInput climbButton;
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
        if (source == climbButton) {
            climbInputStatus = climbButton.getValue();
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
        //TODO Add controller mappings
        climbButton = null;
    }

    private void initInputs() {
        //TODO Add Falcon motors
        climbMotor1 = null;
        climbMotor2 = null;
    }

}