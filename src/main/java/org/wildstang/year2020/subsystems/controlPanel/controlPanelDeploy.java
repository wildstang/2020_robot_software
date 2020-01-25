package org.wildstang.year2020.subsystems.controlPanel;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.timer.WsTimer;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.Robot;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;
import org.wildstang.framework.io.IInputManager;
public class controlPanelDeploy implements Subsystem {

    // TODO add upper and lower limit switches for the deploy motor

    // Inputs
    private DigitalInput upDPAD;
    private DigitalInput downDPAD;

    // Outputs
    private VictorSPX deployMotor;

    // Variables
    private double motorspeed;

    // Statuses
    private boolean deployInputStatus;

    @Override
    public void inputUpdate(Input source) {
        if (source == upDPAD) {
            deployInputStatus = upDPAD.getValue();
            motorspeed = -1.0; // Deploy wheel
        } else if (source == downDPAD) {
            deployInputStatus = downDPAD.getValue();
            motorspeed = 1.0; // Retract wheel
        }
    }

    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        // If button is pressed, set the motorspeed to the defined value in the
        // inputUpdate method
        if (deployInputStatus) {
            deployMotor.set(ControlMode.PercentOutput, motorspeed);
        }
        // If anything else, set motorspeed to 0
        else {
            deployMotor.set(ControlMode.PercentOutput, 0);
        }
    }

    @Override
    public void resetState() {
        deployInputStatus = false;
    }

    @Override
    public String getName() {
        return "Control Panel Deploy";
    }

    private void initOutputs() {
        deployMotor = new VictorSPX(CANConstants.CPDEPLOY_VICTOR);
    }

    private void initInputs() {
        IInputManager inputManager = Core.getInputManager();
        upDPAD = (DigitalInput) inputManager.getInput(WSInputs.CPDEPLOY_DPAD_UP);
        upDPAD.addInputListener(this);
        downDPAD = (DigitalInput) inputManager.getInput(WSInputs.CPDEPLOY_DPAD_DOWN);
        downDPAD.addInputListener(this);
        upperLimitSwitch = (DigitalInput) inputManager.getInput(WSInputs.CPDEPLOY_UPPER_LIMIT_SWITCH);
        upperLimitSwitch.addInputListener(this);
        lowerLimitSwitch = (DigitalInput) inputManager.getInput(WSInputs.CPDEPLOY_LOWER_LIMIT_SWITCH);
        lowerLimitSwitch.addInputListener(this);
    }

}