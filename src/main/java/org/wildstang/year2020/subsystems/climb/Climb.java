package org.wildstang.year2020.subsystems.climb;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climb implements Subsystem {

    // Inputs
    private DigitalInput selectButton;
    private DigitalInput startButton;
    private DigitalInput downButton;

    // Outputs
    private CANSparkMax climbMotor1;
    private CANSparkMax climbMotor2;

    // Variables
    private final double MOTOR_SPEED = 0.8;
    private final double LIFT_HEIGHT = 55;
    private final double LIFT_BOTTOM = 90.5;

    // Statuses
    private boolean climbActiveStatus; // For Shuffleboard
    private boolean climbCompleteStatus; // For Shuffleboard

    enum commands {
            INACTIVE, RAISING, RAISED, LOWERING, PAUSED, LIFEDMAX;
    }
    private commands currentCommand; // 0 = INACTIVE x
                                     // 1 = RAISING x
                                     // 2 = RAISED /
                                     // 3 = LOWERING /
                                     // 4 = PAUSED /
                                     // 5 = LIFEDMAX x


    @Override
    public void inputUpdate(Input source) {
        if (selectButton.getValue() && startButton.getValue() && currentCommand == commands.INACTIVE) {
            currentCommand = commands.RAISING;
        }

        if (downButton.getValue()) {
            if (currentCommand == commands.RAISED || currentCommand == commands.PAUSED) {
                currentCommand = commands.LOWERING;
            } else if (currentCommand == commands.LOWERING) {
                currentCommand = commands.PAUSED;
            }
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
        // If button is pressed, set the motorspeed to the defined value in the inputUpdate method
        if (currentCommand == commands.RAISING) {
            if (climbMotor1.getEncoder().getPosition() >= LIFT_HEIGHT) {
                climbActiveStatus = false; // For Shuffleboard
                climbMotor1.set(0);
                climbMotor2.set(0);
                currentCommand = commands.RAISED;
            } else {
                climbActiveStatus = true; // For Shuffleboard
                climbMotor1.set(0.5 * MOTOR_SPEED);
                climbMotor2.set(0.5 * MOTOR_SPEED);
            }
        }
        
        if (currentCommand == commands.LOWERING) {
            if (climbMotor1.getEncoder().getPosition() >= LIFT_BOTTOM) {
                climbActiveStatus = false; // For Shuffleboard
                climbCompleteStatus = true; // For Shuffleboard
                climbMotor1.set(0);
                climbMotor2.set(0);
                currentCommand = commands.LIFEDMAX;
            } else {
                climbActiveStatus = true;
                climbMotor1.set(MOTOR_SPEED);
                climbMotor2.set(MOTOR_SPEED);
            }
        }
        
        if (currentCommand == commands.PAUSED) {
            climbActiveStatus = false; // For Shuffleboard
            climbMotor1.set(0);
            climbMotor2.set(0);
        }
        
        SmartDashboard.putNumber("Climb Motor 1 Encoder", climbMotor1.getEncoder().getPosition());
        SmartDashboard.putBoolean("Climb Active", climbActiveStatus);
        SmartDashboard.putBoolean("Climb Complete", climbCompleteStatus);
    }

    @Override
    public void resetState() {
        climbActiveStatus = false;
        climbCompleteStatus = false;
        climbMotor1.getEncoder().setPosition(0.0);
        //climbMotor1.restoreFactoryDefaults();
        climbMotor1.setSmartCurrentLimit(80);
        climbMotor1.burnFlash();
        //climbMotor2.restoreFactoryDefaults();
        climbMotor2.setSmartCurrentLimit(80);
        climbMotor2.burnFlash();
    }

    @Override
    public String getName() {
        return "Climb";
    }

    private void initOutputs() {
        climbMotor1 = new CANSparkMax(CANConstants.CLIMB_VICTOR_1, MotorType.kBrushless);
        climbMotor2 = new CANSparkMax(CANConstants.CLIMB_VICTOR_2, MotorType.kBrushless);
    }

    private void initInputs() {
        IInputManager inputManager = Core.getInputManager();
        selectButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_SELECT.getName());
        selectButton.addInputListener(this);
        startButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_START.getName());
        startButton.addInputListener(this);
        downButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_DPAD_DOWN.getName());
        downButton.addInputListener(this);
    }

}