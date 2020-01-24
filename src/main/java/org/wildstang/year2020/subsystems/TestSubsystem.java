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

    // outputs
    private TalonSRX motor;
    private TalonSRX motor2;
    private TalonSRX motor3;

    // states
    private double speed;
    private double maxDriveInput;
    private double maxDriveInput2;

    // Shuffleboard materials
    private ShuffleboardTab driveTab;
    private NetworkTableEntry maxDriveInputEntry;
    private NetworkTableEntry maxDriveInputEntry2;

    // initializes the subsystem
    public void init() {
        // register buttons with arbitrary button names, since this is a test
        joystick = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVE_THROTTLE.getName());
        joystick.addInputListener(this);

        // register solenoids with arbitrary output names, since this is a test
        motor = new TalonSRX(1);//3
        motor2 = new TalonSRX(2);//2
        motor3 = new TalonSRX(3);//1
        //motor2.setInverted(false);
        //motor2.follow(motor);
        // //motor3.follow(motor);
        // motor.configContinuousCurrentLimit(60);
        // motor2.configContinuousCurrentLimit(60);
        // motor3.configContinuousCurrentLimit(60);

        SmartDashboard.putNumber("current",motor.getSupplyCurrent());


        // Add Drive tab and max drive input slider onto Shuffleboard
        driveTab = Shuffleboard.getTab("Drive");
        maxDriveInputEntry = driveTab.add("Max Input", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
        maxDriveInputEntry2 = driveTab.add("Max Input Kicker", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
        
        maxDriveInput = 1.0;
        maxDriveInput2 = 1.0;

        resetState();
    }

    // update the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        maxDriveInput = maxDriveInputEntry.getDouble(1.0);
        maxDriveInput2 = maxDriveInputEntry2.getDouble(1.0);

        motor.set(ControlMode.PercentOutput , -speed * maxDriveInput);
        motor3.set(ControlMode.PercentOutput, -maxDriveInput2);
        motor2.set(ControlMode.PercentOutput , speed * maxDriveInput);
        
    }

    // respond to input updates
    public void inputUpdate(Input signal) {
        // check to see which input was updated
        if (signal == joystick) {
            speed = joystick.getValue();
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