package org.wildstang.year2020.subsystems.ballpath;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;

public class Ballpath implements Subsystem {

    // Motors
    private VictorSPX feedMotor;
    private VictorSPX kickerMotor;
    private VictorSPX intakeMotor;
     

    // Constants (to be set) 
    private double feedMotorSpeed;
    private double kickerMotorSpeed;
    private double intakeMotorSpeed;

    // Status for each motor
    private boolean rightTriggerStatus;
    private boolean YButtonStatus;
    private boolean AButtonStatus;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        
        
        if (rightTrigger.getvalue() > 0.75) {
            rightTriggerStatus = rightTrigger.getValue();

        }
        if (source == YButton) {
            YButtonStatus = YButton.getValue();
        }
        if (source == AButton) {
            AButtonStatus = AButton.getValue();
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
        // TODO Auto-generated method stub

        //runs the hopper motor full power and the kicker motor full power
        if (rightTriggerStatus) {
            feedMotor.set(ControlMode.PercentOutput, feedMotorSpeed);
            kickerMotor.set(ControlMode.PercentOutput, kickerMotorSpeed);
      
        }

        //run hopper motor and kicker motor backwards at ~40% power
        else if (YButtonStatus) {
            feedMotor.set(ControlMode.PercentOutput, -0.4);
            kickerMotor.set(ControlMode.PercentOutput, -0.4);

        }

        //run intake motor at 100% power
        else if (AButtonStatus) {
            intakeMotor.set(ControlMode.PercentOutput, intakeMotorspeed);
            
        } else {
            feedMotor.set(ControlMode.PercentOutput, 0);
            kickerMotor.set(ControlMode.PercentOutput, 0);
            intakeMotor.set(ControlMode.PercentOutput, 0);
            
        }

    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    private void initOutputs() {
        feedMotor = new VictorSPX(CANConstants.BALLPATH_FEED);
        kickerMotor= new VictorSPX(CANConstants.BALLPATH_KICKER);
        intakeMotor= new VictorSPX(CANConstants.BALLPATH_INTAKE);
    }

    private void initInputs() {
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        YButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.Y_BUTTON.getName());
        YButton.addInputListener(this);
        AButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.A_BUTTON.getName());
        AButton.addInputListener(this);
    }

}