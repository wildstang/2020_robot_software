package org.wildstang.year2020.subsystems;

import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;

/**
 * Class:       TestSubsystem.java
 * Inputs:      1 joystick
 * Outputs:     1 talon
 * Description: This is a testing subsystem that controls a motor with a joystick
 */
public class TestSubsystem implements Subsystem {

    // inputs
    private AnalogInput joystick;
    private DigitalInput hoodUp;
    private DigitalInput hoodDown;
    private DigitalInput floor;

    // outputs
    private TalonSRX motor;
    private TalonSRX motor2;
    private TalonSRX motor3;
    private TalonSRX motor4;
    private TalonSRX motor5;

    // states
    private double speed;
    private double maxDriveInput;
    private double kickerInput;
    private double hoodInput;
    private double hopperFloorInput;
    private double hoodDrive;
    private double floormod;

    // Shuffleboard materials
    private ShuffleboardTab driveTab;
    private NetworkTableEntry maxDriveInputEntry;
    private NetworkTableEntry kickerInputEntry;
    private NetworkTableEntry hoodInputEntry;
    private NetworkTableEntry hopperFloorInputEntry;

    // initializes the subsystem
    public void init() {
        // register buttons with arbitrary button names, since this is a test
        joystick = (AnalogInput) Core.getInputManager().getInput(WSInputs.LIFT_MANUAL.getName());
        joystick.addInputListener(this);
        hoodUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.HATCH_COLLECT.getName());
        hoodUp.addInputListener(this);
        hoodDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.HATCH_DEPLOY.getName());
        hoodDown.addInputListener(this);
        floor = (DigitalInput) Core.getInputManager().getInput(WSInputs.REVERSE_BUTTON.getName());
        floor.addInputListener(this);

        // register solenoids with arbitrary output names, since this is a test
        motor = new TalonSRX(1);//primary flywheel
        motor2 = new TalonSRX(2);//follower flywheel
        motor3 = new TalonSRX(3);//kicker
        motor4 = new TalonSRX(4);//shooter hood
        motor5 = new TalonSRX(5);//hopper floor

        // Add Drive tab and max drive input slider onto Shuffleboard
        driveTab = Shuffleboard.getTab("Drive");
        maxDriveInputEntry = driveTab.add("Max Input", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
        kickerInputEntry = driveTab.add("Max Input Kicker", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
        hoodInputEntry = driveTab.add("Max Input hood", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
        hopperFloorInputEntry = driveTab.add("Max Input hopper", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();

        maxDriveInput = 1.0;
        kickerInput = 1.0;
        hoodInput = 1.0;
        hopperFloorInput = 1.0;
        hoodDrive = 0;
        floormod = 0;

        resetState();
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        maxDriveInput = maxDriveInputEntry.getDouble(1.0);
        kickerInput = kickerInputEntry.getDouble(1.0);
        hoodInput = hoodInputEntry.getDouble(1.0);
        hopperFloorInput = hopperFloorInputEntry.getDouble(1.0);

        motor.set(ControlMode.PercentOutput , -speed * maxDriveInput);
        motor2.set(ControlMode.PercentOutput , speed * maxDriveInput);
        motor3.set(ControlMode.PercentOutput, -kickerInput);
        motor4.set(ControlMode.PercentOutput, hoodDrive*hoodInput);
        motor5.set(ControlMode.PercentOutput, -hopperFloorInput * floormod);

        SmartDashboard.putNumber("Shooter encoder velocity",motor.getSensorCollection().getQuadratureVelocity());
    }

    // respond to input updates
    public void inputUpdate(Input signal) {
        // check to see which input was updated
        if (signal == joystick) {
            speed = joystick.getValue();
        }
        if (hoodUp.getValue()){
            hoodDrive=hoodInput;
        }
        else if (hoodDown.getValue()){
            hoodDrive=-hoodInput;
        }
        else {
            hoodDrive=0;
        }
        if (floor.getValue()){
            floormod=1;
        } else {
            floormod=0;
        }
        
    }

    // used for testing
    public void selfTest() {}

    // resets all variables to the default state
    public void resetState() {
        speed = 0.0;
    }

    // returns the unique name of the example
    public String getName() {
        return "TestSubsystem";
    }
}