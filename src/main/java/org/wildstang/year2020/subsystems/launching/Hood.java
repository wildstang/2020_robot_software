package org.wildstang.year2020.subsystems.launching;

import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;

/**
 * Class:       Hood.java
 * Inputs:      
 * Outputs:     
 * Description: 
 */
public class Hood implements Subsystem {

    // Inputs
    DigitalInput leftBumper;
    DigitalInput rightBumper;

    // Outputs
    TalonSRX hoodMotor;

    int hoodSpeed;
    double speedScale = 0.25;

    // initializes the subsystem
    public void init() {
        leftBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_LEFT);
        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_RIGHT);
        hoodMotor = new TalonSRX(CANConstants.HOOD_MOTOR);

        resetState();
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        hoodMotor.set(ControlMode.PercentOutput, hoodSpeed * speedScale);
    }

    // respond to input updates
    public void inputUpdate(Input signal) {
        if (signal == leftBumper || signal == rightBumper) {
            if (leftBumper.getValue()) {
                hoodSpeed = -1;
            }
            else if (rightBumper.getValue()) {
                hoodSpeed = 1;
            }
            else {
                hoodSpeed = 0;
            }
        }
    }

    // resets all variables to the default state
    public void resetState() {
        hoodSpeed = 0;
    }

    // returns the unique name of the example
    public String getName() {
        return "Hood";
    }

    // used for testing
    public void selfTest() {}
}