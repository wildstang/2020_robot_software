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

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive implements Subsystem {
    private DigitalInput ResetYaw;
    private AnalogInput TurnThrottle;
    private AnalogInput VerticalInput;
    private AnalogInput HorizontalInput;
    private AnalogInput QuickturnInput;
    private DigitalInput TurnRight;
    private DigitalInput TurnLeft;
    public double ControlHeading;
    public double Velocity;
    public double Throttle;
    public double Quick;
    public double GoToAngle;
    public double Offset; //the offset in degrees of gyro in relation to 90 degrees clockwise of robot direction
    public double TuningA = 0.9; //  turn throttle multiplier
    public double TuningB = 1; //    banking throttle exponent
    public double TuningC = 0.9;//   Quickturn throttle multiplier
    private TalonSRX DriveMotorRight;
    private TalonSRX DriveMotorLeft;
    private TalonSRX SwerveMotor;
    private TalonSRX DriveMotorRightSlave;
    private TalonSRX DriveMotorLeftSlave;
    private TalonSRX SwerveMotorSlave;
    
    public double V = 20; //this is a multiplier representing max robot velocity
    @Override
    public void init() {
       ResetYaw = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_SHOULDER_LEFT.getName());
        ResetYaw.addInputListener(this);
        TurnRight = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_RIGHT.getName());
        TurnRight.addInputListener(this);
        TurnLeft = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_LEFT.getName());
        TurnLeft.addInputListener(this);
        TurnThrottle = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_Y.getName());
        TurnThrottle.addInputListener(this);
        VerticalInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y.getName());
        VerticalInput.addInputListener(this);
        QuickturnInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_X.getName());
        QuickturnInput.addInputListener(this);
        HorizontalInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_X.getName());
        HorizontalInput.addInputListener(this);

         DriveMotorLeft = new TalonSRX(CANConstants.LeftDrive);
         DriveMotorLeftSlave = new TalonSRX(CANConstants.LeftDriveSlave);
         DriveMotorRight = new TalonSRX(CANConstants.RightDrive);
         DriveMotorRightSlave = new TalonSRX(CANConstants.RightDriveslave);
         SwerveMotor = new TalonSRX(CANConstants.SwerveMotor);
         SwerveMotorSlave = new TalonSRX(CANConstants.SwerveMotorSlave);
       AHRS.reset(); //gyro rest command
       ControlHeading = 0;
       Velocity = 0;
       Quick = 0;
       ReYaw = false;
    }

    @Override
    public void resetState() {
        AHRS.reset();
    ControlHeading = 0;
       Velocity = 0;
       Quick = 0;
       ReYaw = false;
    }
    public void CallibrateGyro(){
        AHRS.Callibrate();
    }

    @Override
    public void inputUpdate(Input source) {
        if (HorizontalInput.getValue()>0.0){
        ControlHeading = (3.14/180)*Math.atan(VerticalInput.getValue()/HorizontalInput.getValue());
        }
        else{
             ControlHeading = 180+((3.14/180)*Math.atan(VerticalInput.getValue()/HorizontalInput.getValue()));
        }
        Velocity = V*(Math.pow(Math.pow(Math.pow(HorizontalInput.getValue,2)+Math.pow(VerticalInput.getValue,2),0.5),TuningB));
        if (TurnRight.getValue()){
            turnstate = 1;
        }
        else {
            turnstate = 0;
            if (TurnLeft.getValue()){
                turnstate = -1;
            }
        }
        if (ResetYaw.getValue()){
            ReYaw = true;
        }
        
        Throttle = TurnThrottle.getValue()*TuningA;
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
    public void update() {
        //Turning and moving
        if (Quick == 0){
        DriveMotorLeft.set(ControlMode.Velocity, Velocity+(turnstate*Throttle*Velocity));// Ex: if is turn left, -1 = turn state, so it goes at velocity - throttle percent of velocity
        DriveMotorLeftSlave.set(ControlMode.Velocity, Velocity+(turnstate*Throttle*Velocity));
        DriveMotorRight.set(ControlMode.Velocity, Velocity-(turnstate*Throttle*Velocity)); //- instead of + because should change power oppisite way as left side
        DriveMotorRightSlave.set(ControlMode.Velocity, Velocity-(turnstate*Throttle*Velocity));
        }
        else{
        DriveMotorLeft.set(ControlMode.Velocity,Quick*V); 
        DriveMotorLeftSlave.set(ControlMode.Velocity,Quick*V); 
        DriveMotorRight.set(ControlMode.Velocity,-1*Quick*V); //right goes completely oppisite left in Quick porportion of max power when quick-turning
        DriveMotorRightSlave.set(ControlMode.Velocity,-1*Quick*V);
        }
        //Banking
        GotoAngle = ((ControlHeading-(AHRS.getAngle()+Offset))/360)*4096; //offset is for if gyro is not physically offset 90 degrees clockwise from the robot. 
        if (GotoAngle > 4096){   //to ensure GoToAngle ranges between 0 and 4096. adding or subrtacting 4096 does not change the angle because 4096 is a full rev.
            GotoAngle = GotoAngle - 4096;
        }
        if (RobotAngle < 0){
            GotoAngle = GotoAngle + 4096;
        }
        SwerveMotor.set(ControlMode.Position,GoToAngle); 
        SwerveMotorSlave.set(ControlMode.Position,GoToAngle); 
        //Reset yaw
        if (ReYaw){
            ReYaw = false;
            AHRS.zeroYaw();
        }
    }
}
