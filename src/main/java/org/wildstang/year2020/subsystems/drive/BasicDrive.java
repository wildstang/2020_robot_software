package org.wildstang.year2020.subsystems.drive;

import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BasicDrive implements Subsystem {

    private TalonSRX masterL;
    private TalonSRX masterR;
    private TalonSRX followL;
    private TalonSRX followR;

    private AnalogInput headingInput;
    private AnalogInput throttleInput;
    private AnalogInput quickTurnInput;

    private double commandThrottle;
    private double commandHeading;
    private boolean isQuick = false;

    private double leftDrive = 0;
    private double rightDrive = 0;

    private double leftDrivePolarity = -1.0;
    private double rightDrivePolarity = 1.0;
    private double throttleJoystickPolarity = -1.0;
    private double headingJoystickPolarity = -1.0;

    private boolean masterLFlip = true;
    private boolean masterRFlip = true;
    private boolean followLFlip = true;
    private boolean followRFlip = true;

    public BasicDrive() {}

    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }
    
    public void initOutputs(){
        masterL = new TalonSRX(CANConstants.left1);
        masterL.setInverted(masterLFlip);
        masterR = new TalonSRX(CANConstants.right1);
        masterR.setInverted(masterRFlip);
        followL = new TalonSRX(CANConstants.left2);
        followL.setInverted(followLFlip);
        followR = new TalonSRX(CANConstants.right2);
        followR.setInverted(followRFlip);
        followL.follow(masterL);
        followR.follow(masterR);
    }

    @Override
    public void resetState() {
        masterL.set(ControlMode.PercentOutput, 0.0);
        masterR.set(ControlMode.PercentOutput, 0.0);
    }

    @Override
    public void inputUpdate(Input source) {
        commandHeading = -headingInput.getValue();
        if (Math.abs(commandHeading)<0.15) commandHeading=0;
        commandThrottle = -throttleInput.getValue();
        if (Math.abs(commandThrottle)<0.15) commandThrottle=0;
        isQuick = (Math.abs(quickTurnInput.getValue())>0.75);
    }

    @Override
    public void selfTest() {}

    @Override
    public void update() {
        if (isQuick){
            if (commandThrottle>=0){
                if (commandHeading>0){
                    leftDrive = -commandHeading;
                    rightDrive = commandHeading*(1-commandThrottle);
                } else if (commandHeading<0){
                    rightDrive = commandHeading;
                    leftDrive = -commandHeading*(1-commandThrottle);
                } else {
                    leftDrive = 0;
                    rightDrive = 0;
                }
            } else if (commandThrottle<0){
                if (commandHeading>0){
                    rightDrive = commandHeading;
                    leftDrive = -commandHeading*(1-commandThrottle);
                } else if (commandHeading < 0){
                    leftDrive = -commandHeading;
                    rightDrive = commandHeading*(1-commandThrottle);
                } else {
                    leftDrive = 0;
                    rightDrive = 0;
                }
            }
        } else {
            leftDrive = commandThrottle - commandHeading;
            rightDrive = commandThrottle + commandHeading;
            if (leftDrive>1.0){
                rightDrive *= 1.0/leftDrive;
                leftDrive = 1.0;
            } else if (rightDrive > 1.0){
                leftDrive *= 1.0/rightDrive;
                rightDrive = 1.0;
            } else if (leftDrive < -1.0){
                rightDrive *= -1.0/leftDrive;
                leftDrive = -1.0;
            } else if (rightDrive < -1.0){
                leftDrive *= -1.0/rightDrive;
                rightDrive = -1.0;
            }
        }
        masterL.set(ControlMode.PercentOutput, leftDrivePolarity*leftDrive);
        masterR.set(ControlMode.PercentOutput, rightDrivePolarity*rightDrive);
    }

    @Override
    public String getName() {
        return "Drive Base";
    }

    private void initInputs() {
        // Set and subscribe to inputs
        headingInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_X);
        headingInput.addInputListener(this);
        throttleInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
        throttleInput.addInputListener(this);
        quickTurnInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_RIGHT.getName());
        quickTurnInput.addInputListener(this);
    }
}