package org.wildstang.year2017.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsVictor;
import org.wildstang.year2017.robot.WSInputs;
import org.wildstang.year2017.robot.WSOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber implements Subsystem {
    // add variables here
    private DigitalInput upbutton;
    private DigitalInput halfButton;
    private WsVictor w_motor;
    private double motorspeed = -1.0;

    // ALL these are state that should be reset in resetState()
    private boolean m_runClimber;
    private boolean running;

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return "Climber";
    }

    @Override
    public void init() {
        // Output
        w_motor = (WsVictor) Core.getOutputManager().getOutput(WSOutputs.WINCH.getName());

        // Inputs
        upbutton = (DigitalInput) Core.getInputManager().getInput(WSInputs.CLIMBER_UP.getName());
        upbutton.addInputListener(this);
        halfButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.CLIMBER_HALF_SPEED.getName());
        halfButton.addInputListener(this);

        resetState();
    }

    @Override
    public void resetState() {
        m_runClimber = false;
        running = false;
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == upbutton) {
            m_runClimber = upbutton.getValue();
            motorspeed = -1.0;
        } else if (source == halfButton) {
            m_runClimber = halfButton.getValue();
            motorspeed = -0.5;
        }
    }

    @Override
    public void update() {

        if (m_runClimber) // Checks if the dpad button is being pushed
        {
            running = true; // for Smart Dashboard
            w_motor.setValue(motorspeed);
        } else // Checks to see if the button is not being pushed
        {
            running = false; // For smart dashboard
            w_motor.setValue(0); // When not pushed the motor speed is 0.
        }

        SmartDashboard.putBoolean("Winch Running", running);
    }
}
