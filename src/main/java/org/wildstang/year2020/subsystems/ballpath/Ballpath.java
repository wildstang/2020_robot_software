package org.wildstang.year2020.subsystems.ballpath;

import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
//import org.wildstang.framework.logger.StateTracker;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.inputs.WSInputType;
import org.wildstang.year2020.robot.CANConstants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Ballpath implements Subsystem {

    private AnalogInput ballpathFullTrigger;
    private DigitalInput ballpathReverseButton; 
    private DigitalInput intakeButton; 

    private TalonSRX HopperMotor;
    private static int HOPPER_ID = 0;
    private TalonSRX KickerMotor;
    private static int KICKER_ID = 1; 
    private TalonSRX IntakeMotor; 
    private static int INTAKE_ID = 2; 

    public boolean fullActive;
    public boolean fullActiveReverse;
    public boolean intakeActive; 

    public static boolean intakeEnabled;


    public boolean AnalogToDigital(AnalogInput source, double threshold) {
        if(source.getValue() < threshold) {
            return false;
        }
        return true;
    }

    @Override
    public void inputUpdate(Input source) {
        if(source == ballpathFullTrigger) {
            if(AnalogToDigital(ballpathFullTrigger, 0.5)) {
                fullActive = true; 
            }
        }
        else if(source == ballpathReverseButton) {
            fullActiveReverse = ballpathReverseButton.getValue();
        }
        else if(source == intakeButton) {
            intakeActive = intakeButton.getValue();
        }
        else {
            fullActive = false;
            fullActiveReverse = false;
            intakeActive = false; 
        }
        
    }

    @Override
    public void init() {
        initInputs();
        initOutputs();

    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        if(fullActiveReverse && fullActive == false) {
            if(fullActive) {
                HopperMotor.set(ControlMode.PercentOutput, 1);
                KickerMotor.set(ControlMode.PercentOutput, 1);
            }
            else {
                HopperMotor.set(ControlMode.PercentOutput, -0.4);
                KickerMotor.set(ControlMode.PercentOutput, -0.4);
            }

        }
        else { 
            HopperMotor.set(ControlMode.PercentOutput, 0);
            KickerMotor.set(ControlMode.PercentOutput, 0);
        }
        
        if(intakeActive && intakeEnabled) {
            IntakeMotor.set(ControlMode.PercentOutput, 1);
        }
        else if (intakeEnabled) {
            IntakeMotor.set(ControlMode.PercentOutput, 0);
        }

    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "ballpath";
    }

    private void initOutputs() {
        HopperMotor = new TalonSRX(HOPPER_ID);
        IntakeMotor = new TalonSRX(INTAKE_ID);
        KickerMotor = new TalonSRX(KICKER_ID);


    }

    private void initInputs() {
        ballpathFullTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_RIGHT);
        ballpathReverseButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_UP);
        intakeButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN);
        ballpathFullTrigger.addInputListener(this);
        ballpathReverseButton.addInputListener(this);
        intakeButton.addInputListener(this);
    }

}