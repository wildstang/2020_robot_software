package org.wildstang.year2020.subsystems.controlPanel;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

public class controlPanelWheel implements Subsystem {
    // Inputs
    private DigitalInput leftDPAD;
    private DigitalInput rightDPAD;
    private DigitalInput leftJoystickButton;

    // Outputs
    private TalonSRX wheelMotor;

    // Variables
    private double motorSpeed;

    // Statuses
    private boolean wheelInputDPADStatus;
    private boolean wheelInputJoystickStatus;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (source == leftDPAD) {
            wheelInputDPADStatus = leftDPAD.getValue();
         motorSpeed = 1.0;
         } // run wheel at full power
        else if (source == rightDPAD) {           
            wheelInputDPADStatus = rightDPAD.getValue();
         motorSpeed = -1.0; // run wheel at full power in reverse
        }
     if (source == leftJoystickButton)
     {
     wheelInputJoystickStatus = leftJoystickButton.getValue();

     }

    }

    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }
    private void initInputs() {
        //add WSINputs objects to WSINPUTS
        IInputManager inputManager = Core.getInputManager();
        leftDPAD = (DigitalInput) inputManager.getInput(WSInputs.CPWHEEL_DPAD_LEFT);
        leftDPAD.addInputListener(this);
        rightDPAD = (DigitalInput) inputManager.getInput(WSInputs.CPWHEEL_DPAD_RIGHT);
        rightDPAD.addInputListener(this);
        leftJoystickButton = (DigitalInput) inputManager.getInput(WSInputs.CPWHEEL);
    }
    
    private void initOutputs() {
        wheelMotor = new TalonSRX(CANConstants.INTAKECPWHEEL_TALON);
    }


    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
           // If button is pressed, set the motorSpeed to the defined value in the
        // inputUpdate method
        if (wheelInputDPADStatus) {
            wheelMotor.set(ControlMode.PercentOutput, motorSpeed);
        }
        else if (wheelInputJoystickStatus)
        {
            //TODO
           // wheelMotor.set
            // set to encoder
        }
        // If anything else, set motorSpeed to 0
        else {
            wheelMotor.set(ControlMode.PercentOutput, 0);
        }
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        wheelInputDPADStatus = false;
        wheelInputJoystickStatus = false;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Control Panel Wheel";
    }

 
}