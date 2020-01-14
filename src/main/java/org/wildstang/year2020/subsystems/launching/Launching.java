package org.wildstang.year2020.subsystems;

import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

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
public class Launching implements Subsystem {


    // initializes the subsystem
    public void init() {
        
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        
    }

    // respond to input updates
    public void inputUpdate(Input signal) {
        
    }

    // used for testing
    public void selfTest() {}

    // resets all variables to the default state
    public void resetState() {
        
    }

    // returns the unique name of the example
    public String getName() {
        return "Launching";
    }
}