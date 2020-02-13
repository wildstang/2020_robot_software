package org.wildstang.year2020.subsystems.launching;

import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
<<<<<<< HEAD
    double speedScale = 0.25;
    
=======
    double speedScale = 1.0;
>>>>>>> 8af4aea46f187b4d860e6364c94aebd26ad3d114

    // initializes the subsystem
    public void init() {
        leftBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_LEFT);
        leftBumper.addInputListener(this);
        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_RIGHT);
        rightBumper.addInputListener(this);
        hoodMotor = new TalonSRX(CANConstants.HOOD_MOTOR);

        resetState();
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        hoodMotor.set(ControlMode.PercentOutput, hoodSpeed * speedScale);
        SmartDashboard.putNumber("hood position",hoodMotor.getSelectedSensorPosition());
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