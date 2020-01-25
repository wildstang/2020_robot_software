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

public class intake implements Subsystem {
        // Inputs
        private DigitalInput buttonA;
    
        // Outputs
        private TalonSRX intakeMotor;
    
        // Variables
        private double motorSpeed;
    
        // Statuses
        private boolean intakeInputStatus;
    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if(source == buttonA && buttonA.getValue() == true)
        {
            if (motorSpeed > 1) 
            {
                // if pressed and already running
                motorSpeed = 0;
                intakeInputStatus = false;
                   
                   
            }
            else if (motorSpeed < 1) 
            {
                // if pressed and not running
                motorSpeed = 1;
                intakeInputStatus = true;
                
            }
            else
            {
                //default
                 motorSpeed = 0;
                intakeInputStatus = false;
            }
    }
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        initInputs();
        initOutputs();
        resetState();
       
       
    }
    public void initInputs() {
        IInputManager inputManager = Core.getInputManager();
        buttonA = (DigitalInput) inputManager.getInput(WSInputs.INTAKE);
        buttonA.addInputListener(this);

        
    }
    public void initOutputs() {
        intakeMotor = new TalonSRX(CANConstants.INTAKECPWHEEL_TALON);

    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        intakeInputStatus = false;

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        // If button is pressed, set the motorSpeed to the defined value in the
        // inputUpdate method
        if (intakeInputStatus)
        {
        intakeMotor.set(ControlMode.PercentOutput,motorSpeed);
        }
        // If anything else, set motorSpeed to 0
        else
        {
        intakeMotor.set(ControlMode.PercentOutput,0);
        }
    }
    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        intakeInputStatus = false;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Intake";
    }


}
