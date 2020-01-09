package org.wildstang.year2020.subsystems;

import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.subsystems.Subsystem;

/**
 * Class:       TestSubsystem.java
 * Inputs:      1 joystick
 * Outputs:     1 talon
 * Description: This is a testing subsystem that controls a motor with a joystick
 */
public class TestSubsystem implements Subsystem {

    // inputs
    private AnalogInput joystick;

    // outputs
    private TalonSRX motor;

    // states
    private double speed;

    // initializes the subsystem
    public void init() {
        // register buttons with arbitrary button names, since this is a test
        joystick = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVE_THROTTLE.getName());
        joystick.addInputListener(this);

        // register solenoids with arbitrary output names, since this is a test
        motor = new TalonSRX(CANConstants.LEFT_DRIVE_TALON);

        resetState();
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        motor.set(ControlMode.PercentOutput , speed);
    }

    // respond to input updates
    public void inputUpdate(Input signal) {
        // check to see which input was updated
        if (signal == joystick) {
            speed = joystick.getValue();
        }
    }

    // used for testing
    public void selfTest() {}

    // resets all variables to the default state
    public void resetState() {
        speed = 0.0;
    }

    // returns the unique name of the example
    public String getName() {
        return "TestSubsystem";
    }
}