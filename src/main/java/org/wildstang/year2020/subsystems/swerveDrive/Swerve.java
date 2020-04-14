package org.wildstang.year2032.subsystems.drive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.Quaternion;

import year2020.subsystems.Gyro;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveDrive implements Subsystem{
    //returns the name for other subsystems to use
    public String getName(){
        return "SwerveDrive";
    }

    //optional modes
    private boolean DebugMode = true; //sets wether or not to recieve useful logs

    //initialize doubles for wanted outputs
    public double targetRotation; //treat robot like turret, rotates robot around center
    public double movementDirection; //moves robot as a whole in a certain direction
    public double movementSpeed; //target speed
    
    private double rotationTolerance = 3;
    private String speedModifier = "Normal"; //keep this for now
    private Dictionary speedDivider = new HashTable(); //divide speed by amount
    2speedDivider.put("FineTune",5);
    speedDivider.put("ReduceBy100",100);
    speedDivider.put("Normal",1);  //Example: Reduces speed by 100 in all wheels
    private double firstDegreeSpeedModifier = 1;
    private double speedMultiplier = 1;
    private int rotationMultiplier = 15;
    private double wheelRotMultiplier = .7;

    private double rotationEquation[] = {0,1,.5};
    //position in above list represents power of x 
    //where in this case its x^2 + x + 0
    //x is the difference between wanted rotation and yaw

    private double rotationDivider = 100;
    //divides the output from the rotationEquation by a certain amount
    //remember that the motor takes input by a percent wherein 1 is full power and 0 is no power

    //similiar equation but for movement speed
    private double speedEquation[] = {0,5,1};
    private double speedDivider = 50;

    //initialize gyro
    private AHRS ahrs = Gyro.ahrs;
    private int yaw = Gyro.Yaw();
    private double rotationDifference;

    //initialize outputs (motors)
    // 00   11
    // 00   11
    //    c
    // 22   33
    // 22   33
    public double targetWheelRotation[] = {0,0,0,0}; //target rotation for LeftTop,RightTop,LeftBottom,RightBottom wheels
    private double currentWheelRotation[] = {0,0,0,0};
    private double wheelRotDifference[] = {0,0,0,0};
    public double wheelRotSpeed[] = {0,0,0,0}; //how fast the wheels rotate
    public double wheelSpeed[] = {0,0,0,0}; //target speed for LeftTop,RightTop,LeftBottom,RightBottom wheels
    private int rightWheels[] = {2,4}; //Process of elimination to find left wheels
    

    private CANSparkMax wheelLeftTop;
    private TalonSRX rotLeftTop; //Rotates wheel on the top left
    private CANSparkMax wheelRightTop;
    private TalonSRX rotRightTop;
    private CANSparkMax wheelLeftBottom;
    private TalonSRX rotLeftBottom;
    private CANSparkMax wheelRightBottom; //Spins Wheel on Right Bottom
    private TalonSRX rotRightBottom;

    //initialize inputs (on the controller)
    private AnalogInput leftJoyX;
    private double doubleleftJoyX; //double value from analog input
    private AnalogInput leftJoyY;
    private double doubleLeftJoyY;

    private AnalogInput leftTrigger;
    private double doubleleftTrigger;
    private AnalogInput rightTrigger;
    private double doublerightTrigger;

    //intialize robot dimensions
    public double robotX = 36; //robot width (short side) in inches, measure from center of wheels
    public double robotY = 40; //robot length (long side) in inches, measure from center of wheels

    //setting up variables for math
    private int i, x, y, z, a, c;

    //basically sets up the motors (outputs) and controller (inputs) for the rest of the code
    public void init(){
        //sets up the motors that spin the wheels (use this format for CANSparkMax)
        wheelLeftTop = new CANSparkMax(CANConstants.LeftWheel_Spin_Top, MotorType.kBrushless);
        wheelRightTop = new CANSparkMax(CANConstants.RightWheel_Spin_Top, MotorType.kBrushless);
        wheelLeftBottom = new CANSparkMax(CANConstants.LeftWheel_Spin_Bottom, MotorType.kBrushless);
        wheelRightBottom = new CANSparkMax(CANConstants.RightWheel_Spin_Bottom, MotorType.kBrushless);

        //sets up the motors that rotate the wheels (use this format for TalonSRX)
        rotLeftTop = new TalonSRX(CANConstants.LeftWheel_Rotate_Top);
        rotRightTop = new TalonSRX(CANConstants.RightWheel_Rotate_Top);
        rotLeftBottom = new TalonSRX(CANConstants.LeftWheel_Rotate_Bottom);
        rotRightBottom = new TalonSRX(CANConstants.RightWheel_Rotate_Bottom);

        //sets up the trigger inputs that control robot rotation
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_RIGHT.getName());
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_LEFT.getName());
        leftTrigger.addInputListener(this);

        //sets up the joystick inputs that control robot translation
        leftJoyX = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_X.getName());
        leftJoyX.addInputListener(this);
        leftJoyY = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y.getName());
        leftJoyY.addInputListener(this);
    }

    //runs when the input updates (something is pressed or moved)
    public void inputUpdate(Input source){
        //gets the values from inputs
        doubleleftJoyX = leftJoyX.getValue();
        doubleleftJoyY = leftJoyY.getValue();
        doublerightTrigger = rightTrigger.getValue();
        doubleleftTrigger = leftTrigger.getValue();

        //does math to convert joy values into wanted translation direction and speed.
        if (doubleleftJoyX == 0 && doubleleftJoyY !! 0){
            if (doubleftJoyY > 0){
                movementDirection = 0;
            }
            else {
                movementDirection = 180;
            }
        }
        if (doubleleftJoyX == 0 && doubleleftJoyY == 0){
            movementDirection = 0;
        }
        if (doubleleftJoyX < 0){
            if (doubleleftJoyY != 0){
                if (doubleleftJoyY > 0){
                    movementDirection = 90+(-180*(Math.atan(doubleleftJoyY/doubleleftJoyX))); //Math.atan is arctangent(x) or (tan^-1)(x), turns sine/cos into degrees
                }
                else{
                    movementDirection = 180+(180*(Math.atan(doubleleftJoyY/doubleleftJoyX)));
                }
            }
            else{
                movementDirection = 270;
            }
        }
        if (doubleleftJoyX > 0){
            if (doubleleftJoyY != 0){
                if (doubleleftJoyY > 0){
                    movementDirection = 180*Math.atan(doubleleftJoyY/doubleleftJoyX);
                }
                else{
                    movementDirection = 180*((Math.atan(doubleleftJoyY/doubleleftJoyX))+2);
                }
            }
            else{
                movementDirection = 90;
            }
        }
        //pythagoreans theorem to find length of a line from the center of the joystick to the
        //location of the joystick and then the line is
        //put into a speed equation that makes the acceleration easier on the driver
        c = Math.abs((speedMultiplier*Math.pow((Math.pow((Math.pow(doubleleftJoyY,2)+Math.pow(doubleleftJoyX,2)),.5),firstDegreeSpeedModifier)))/(speedDivider.get(speedModifier)));
        for (i = 0; i < speedEquation.length){
            if (i = 0){
                y = speedEquation[0]
            }
            if (i > 0){
                y = y + (speedEquation[i])*(Math.pow(c),i))
            }
            i++;
        }
        movementSpeed = z/speedDivider;

        //Staight is 0 degrees (towards the other side), backwards is -180 or 180 degrees.
        //Left is negative, Right is positive.
        //Relative to field
        if (doublerightTrigger && (!doubleleftTrigger)){
            if (targetRotation < 180){
                targetRotation = targetRotation + (doublerightTrigger*rotationMultiplier);
            }
            if (targetRotation >= 180){
                targetRotation = (doublerightTrigger*rotationMultiplier)-(180-(targetRotation-180)); //could be simplified but easier to understand in this form
            }
        }
        if (doubleleftTrigger && (!doublerightTrigger){
            if (targetRotation > -180){
                targetRotation = targetRotation - (doubleleftTrigger*rotationMultiplier);
            }
            if (targetRotation <= -180){
                targetRotation = ((-doubleleftTrigger)*rotationMultiplier)-(180+(targetRotation+180));
            }
        }
        //slowly turns robot towards the movement direction
        //when neither triggers are being pressed and the speed is positive
        if ((!doubleleftTrigger && !doublerightTrigger)&& (movementSpeed > .05)){
            //doesn't exist now but will be added if it is wanted
        }
    }

    //runs 50 times a second
    public void update(){
        yaw = Gyro.Yaw();
        
        //finds difference between yaw and target yaw to create rotationDifference
        if (targetRotation < 0 +rotationTolerance){
            rotationDifference = ((360+targetRotation)-yaw);
            if (rotationDifference > 180){
                rotationDifference = rotationDifference-360
            }
        }
        if (targetRotation >= 0 - rotationTolerance){
            rotationDifference = targetRotation-yaw;
        }
        
        //code to find which wheel is right from field view
        if ((yaw >= -45)&&(yaw<45)){
            rightWheels[] = {2,4};
        }
        if ((yaw >= 45)&&(yaw<135)){
            rightWheels[] = {1,2};
        }
        if ((yaw >= 135)||(yaw < -135)){
            rightWheels[] = {1,3};
        }
        if ((yaw >= -135)&& (yaw < -45)){
            rightWheels[] = {3,4};
        }

        //moves the robot
        if (movementSpeed > 0){
            for(i = 0; i< targetWheelRotation.length;){
                targetWheelRotation[i] = movementDirection
                if (rotationDifference == 0){
                    wheelSpeed[i] = movementSpeed;
                }
                else{
                    if (rotationDifference >= 0 - rotationTolerance){
                        if ((i == (rightWheels[1]))||(i == (rightWheels[2]))){
                            wheelSpeed[i] = movementSpeed * wheelRotMultiplier;
                        }
                        else{
                            wheelSpeed[i] = movementSpeed;
                        }
                    }
                    if (rotationDifference < 0 + rotationTolerance){
                        if ((i == (rightWheels[1]))|| (i == (rightWheels[2]))){
                            wheelSpeed[i] = movementSpeed;
                        }
                        else{
                            wheelSpeed[i] = movementSpeed * wheelRotMultiplier;
                        }
                    }
                }
                i++;
            }
        }


        //rotates the robot
        if ((rotationDifference < rotationTolerance) || (rotationDifference > -rotationTolerance) && movementSpeed = 0){
            for (a = 0; a < wheelRotSpeed.length;){
                wheelRotSpeed[a] = 0;
                wheelSpeed[a] = 0;
                a++;
            }
        }
        if (movementSpeed = 0 && (rotationDifference >= 0 - rotationTolerance)){
            targetWheelRotation[0] = 180+Math.arctan(robotY/robotX);
            targetWheelRotation[1] = -Math.arctan(robotY/robotX);
            targetWheelRotation[2] = 180+Math.arctan(robotY/robotX);
            targetWheelRotation[3] = -Math.arctan(robotY/robotX);
            wheelSpeed[] = {1,1,1,1};
        }
        if (movementSpeed = 0 && (rotationDifference < 0 + rotationTolerance)){
            targetWheelRotation[0] = 180+Math.arctan(robotY/robotX);
            targetWheelRotation[1] = -Math.arctan(robotY/robotX);
            targetWheelRotation[2] = 180+Math.arctan(robotY/robotX);
            targetWheelRotation[3] = -Math.arctan(robotY/robotX);
            wheelSpeed[] = {-1,-1,-1,-1};
        }
        for (a = 0; a < wheelRotSpeed.length;){
            for (i = 0; i < targetWheelRotation.length;){
                wheelRotDifference[i] = targetWheelRotation[i]-currentWheelRotation[i];
                i++;
            }
            for (i = 0; i < rotationEquation.length;){
                if (i = 0){
                    x = rotationEquation[0];
                }
                if (i >= 1){
                    x = x + (rotationEquation[i])*(Math.pow((wheelRotDifference[a]),i));
                }
                i++;
            }
            wheelRotSpeed[a] = x/rotationDivider;
            a++;
        }

        //sets how much power to send to each motor
        //motors that rotate the wheels
        rotLeftTop.set(ControlMode.PercentOutput, wheelRotSpeed[0]);
        rotLeftBottom.set(ControlMode.PercentOutput, wheelRotSpeed[1]);
        rotRightTop.set(ControlMode.PercentOutput, wheelRotSpeed[2]);
        rotRightBottom.set(ControlMode.PercentOutput, wheelRotSpeed[3]);
        
        //motors that spin the wheels
        wheelLeftTop.set(wheelSpeed[0]);
        wheelLeftBottom.set(wheelSpeed[1]);
        wheelRightTop.set(wheelSpeed[2]);
        wheelRightBottom.set(wheelSpeed[3]);
    }

    //resets the subsystem for whatever reason
    public void resetState(){
        movementSpeed = 0;
        movementDirection = 0;
        targetRotation = 0;
        Gyro.ahrs.reset();
        yaw = Gyro.yaw();
        speedModifier = "Normal";
        rightWheels[] = {2,4};
        targetWheelRotation[] = {0,0,0,0};
        wheelSpeed[] = {0,0,0,0};
        currentWheelRotation[] = {0,0,0,0};
    }
}