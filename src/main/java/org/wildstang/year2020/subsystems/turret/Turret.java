package org.wildstang.year2020.subsystems.turret;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;

/**
 * Class:       Turret.java
 * Inputs:      2 encoders
 *                - shooter talon
 *                - turret pivot talon
 *              1 limit switch
 *                - detects 0 position of turret
 *              1 potentiometer (adc)
 *                - measures hood angle
 *              2 joystick buttons
 *                - (X) back firing position hot key (+45 degrees counter-clockwise)
 *                - (Y) front firing position hot key (+225 degrees clockwise)
 *              1 joystick trigger
 *                - Activates aiming mode
 *                  - Warms up flywheel
 *                  - Activates limelight
 *                  - Adjusts hood angle to outer/inner port distance distance
 *                - On release
 *                  - Flywheel spins down
 *                  - Limelight deactivated
 *                  - Hood dropped
 *                  - Turret resets to last requested preset
 * Outputs:     2 talons
 *                - Main flywheel motor
 *                - Turret pivot motor
 *              2 victors
 *                - Secondary flywheel motor
 *                - Hood move motor
 *              1 shuffleboard boolean
 *                - Combo of turret and hood in position and flywheel at speed
 * Description: The turret kicks balls into the flywheel, pivots the turret and hood, the shoots the power cells.    
 * Notes:       This currently accounts on 100% limelight driven shooting, there will likely be some manual controls later.  
 */
public class Turret implements Subsystem {
    // digital inputs
    DigitalInput zeroSwitch;
    DigitalInput rearHotkey;
    DigitalInput frontHotkey;

    // analog inputs
    AnalogInput hoodAngle;
    AnalogInput aimButton;

    // talons
    TalonSRX flyWheelMaster;
    TalonSRX turretPivot;

    // victors
    VictorSPX flyWheelSecondary;
    VictorSPX hoodDeploy;

	@Override
	public void init() {
        // initialize inputs and outputs
	}

	@Override
	public void resetState() {
        // set default values
	}

	@Override
	public void inputUpdate(Input source) {
        // respond to registered inputs
	}

	@Override
	public void update() {
        // update outputs to desired values
	}

	@Override
	public String getName() {
		return "Turret";
	}

	@Override
	public void selfTest() {
        // we don't really test... we probably should
	}
}