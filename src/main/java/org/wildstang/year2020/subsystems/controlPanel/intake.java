package org.wildstang.year2020.subsystems.controlPanel;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.WSInputs;

public class intake implements Subsystem {
    blic
    class controlPanelWheel implements Subsystem {
        // Inputs
        private DigitalInput buttonA;
    
        // Outputs
        private TalonSRX wheelMotor;
    
        // Variables
        private double motorspeed;
    
        // Statuses
        private boolean intakeInputStatus;
    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        buttonA = (DigitalInput) InputManager.getInput(WSInputs.INTAKE);
        buttonA.addInputListener(this);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Intake";
    }


}