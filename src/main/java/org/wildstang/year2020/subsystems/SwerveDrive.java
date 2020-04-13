package org.wildstang.year2032.subsystems.drive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
//import org.wildstang.framework.logger.StateTracker;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import java.lang.Math; 
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.Quaternion;

import year2020.subsystems.Gyro;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveDrive implements Subsystem {
    private DigitalInput ResetYaw;
    
    private AnalogInput VerticalInput;
    private AnalogInput HorizontalInput;
    private AnalogInput QuickturnInput;
    private AnalogInput TurnRight;
    private AnalogInput TurnLeft;
    public double ControlHeading;
    public double Velocity;
    public double Throttle;
    public double Quick;
    public double turnstate;
    public double GoToAngle;
    public boolean ReYaw; //reset yaw
    public double Offset; //the offset in degrees of gyro in relation to 90 degrees clockwise of robot direction
    public double TuningA = 0.9; //  turn throttle multiplier
    public double TuningB = 1; //    banking throttle exponent
    public double TuningC = 0.9;//   Quickturn throttle multiplier
    private TalonSRX DriveMotorRight;
    private TalonSRX DriveMotorLeft;
    private TalonSRX SwerveMotor;
    private TalonSRX DriveMotorRightBack;
    private TalonSRX DriveMotorLeftBack;
    private TalonSRX SwerveMotorSlave;
 
    public double V = 20; //this is a multiplier representing max robot velocity
    @Override
    public void init() {

       ResetYaw = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_SHOULDER_LEFT.getName());
        ResetYaw.addInputListener(this);
        TurnRight = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_RIGHT.getName());
        TurnRight.addInputListener(this);
        TurnLeft = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_LEFT.getName());
        TurnLeft.addInputListener(this);
     
        
        VerticalInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y.getName());
        VerticalInput.addInputListener(this);
        QuickturnInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_X.getName());
        QuickturnInput.addInputListener(this);
        HorizontalInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_X.getName());
        HorizontalInput.addInputListener(this);

         DriveMotorLeft = new TalonSRX(CANConstants.LeftDrive);
         DriveMotorLeftBack = new TalonSRX(CANConstants.LeftDriveBack);
         DriveMotorRight = new TalonSRX(CANConstants.RightDrive);
         DriveMotorRightBack = new TalonSRX(CANConstants.RightDriveBack);
         SwerveMotor = new TalonSRX(CANConstants.SwerveMotor);
         SwerveMotorSlave = new TalonSRX(CANConstants.SwerveMotorSlave);
       Gyro.Reset(); //gyro rest command
       ControlHeading = 0;
       Velocity = 0;
       Quick = 0;
       ReYaw = false;
    }

    @Override
    public void resetState() {
        Gyro.Reset();
    ControlHeading = 0;
       Velocity = 0;
       Quick = 0;
       ReYaw = false;
    }
    

    @Override
    public void inputUpdate(Input source) {
        if (HorizontalInput.getValue()>0.0){
        ControlHeading = (3.14/180)*Math.atan(VerticalInput.getValue()/HorizontalInput.getValue());
        }
        else{
             ControlHeading = 180+((3.14/180)*Math.atan(VerticalInput.getValue()/HorizontalInput.getValue()));
        }
        Velocity = V*(Math.pow(Math.pow(Math.pow(HorizontalInput.getValue(),2)+Math.pow(VerticalInput.getValue(),2),0.5),TuningB));
       turnstate = (TurnRight.getValue() - TurnLeft.getValue()) * TuningA;
        if (ResetYaw.getValue()){
            ReYaw = true;
        }
        
         if ((QuickturnInput.getValue()>0.1) ||(QuickturnInput.getValue()<-0.1)){
             Quick = QuickturnInput.getValue()*TuningC;
         }
         else{
             Quick = 0;
         }
    }

    @Override
    public void selfTest() {
       
    }
    @Override
    public String getName() {
       return "SwerveDrive";
    }

    @Override
    public void update() {
        //Turning and moving
        if (Quick == 0){
        
       
        }
        else{
        
        }
        //Banking
        GoToAngle = ((ControlHeading-(Gyro.Yaw()+Offset))/360)*4096; //offset is for if gyro is not physically offset 90 degrees clockwise from the robot. 
        if (GoToAngle > 4096){   //to ensure GoToAngle ranges between 0 and 4096. adding or subrtacting 4096 does not change the angle because 4096 is a full rev.
            GoToAngle = GoToAngle - 4096;
        }
        if (GoToAngle < 0){
            GoToAngle = GoToAngle + 4096;
        }
        SwerveMotor.set(ControlMode.Position,GoToAngle); 
        SwerveMotorSlave.set(ControlMode.Position,GoToAngle); 
        //Reset yaw
        if (ReYaw){
            ReYaw = false;
            Gyro.Zero();
        }
    }
}
