package org.wildstang.year2020.subsystems.controlPanel;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;

public class controlPanelWheel implements Subsystem {
    // Inputs
    private DigitalInput leftDPAD;
    private DigitalInput rightDPAD;
    private DigitalInput leftJoystickButton;

    // Outputs
    private TalonSRX controlPanelMotor;

    // Variables
    private double motorspeed;

    // Statuses
    private boolean wheelInputStatus;
    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
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
        return "Control Panel Wheel";
    }

    private void initInputs() {

    }
    
    private void initOutputs() {

    }

}