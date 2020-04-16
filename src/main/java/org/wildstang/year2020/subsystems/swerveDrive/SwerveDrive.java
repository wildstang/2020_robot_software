/*Last successful build 8:22, 4/15/2020, 372 lines
Current Build Successful? Yes, Current Build Stable? No
Changes: Changed the method to find right wheels.
    Changed from using yaw to using movementDirection
Things to fix/do:
    Line 216 "z = (speedModify.get(speedModifier));"
        -fixed 4/15 @mccro
*/
package org.wildstang.year2020.subsystems.swerveDrive;

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

import org.wildstang.year2020.subsystems.swerveDrive.Gyro;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;


public abstract class SwerveDrive implements Subsystem{
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
    private Dictionary speedModify = new Hashtable<String,Double>(); //divide speed by amount
    
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
    private double speedDivider= 50;

    //initialize gyro
    private AHRS ahrs = Gyro.ahrs;//@mccro: you never use the ahrs because of the gyro class, so i'd get rid of this line
    private double yaw = Gyro.Yaw();
    private double rotationDifference;
    //@mccro: question on how you want to use the gyro. currently, this doesn't call SetUpGyro(), which is required
    //@mccro: you could call it here, or call it in WsOutputs, or make sure it gets called in robotInit() in the robot class
    //@mccro: you could also have the gyro have a constructor that does what setupgyro does, and creating the subsystem in WsSubsystems would call it for you

    //initialize outputs (motors)
    // 00   11
    // 00   11
    //    c
    // 22   33
    // 22   33
    // in a list declaration, the "[]" goes in the type. Ex: public double[] list = {.....
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
    private double doubleLeftJoyX; //double value from analog input
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
    private double x, y, z, c;
    private int i,a;

    //basically sets up the motors (outputs) and controller (inputs) for the rest of the code
    public void init(){
        speedModify.put("FineTune",5);
        speedModify.put("ReduceBy100",100);
        speedModify.put("Normal",1);  //Example: Reduces speed by 100 in all wheels
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
        doubleLeftJoyX = leftJoyX.getValue();
        doubleLeftJoyY = leftJoyY.getValue();
        doublerightTrigger = rightTrigger.getValue();
        doubleleftTrigger = leftTrigger.getValue();

        //does math to convert joy values into wanted translation direction and speed.
        if ((doubleLeftJoyX == 0) && (doubleLeftJoyY != 0)){
            if (doubleLeftJoyY > 0){
                movementDirection = 0;
            }
            else {
                movementDirection = 180;
            }
        }
        if ((doubleLeftJoyX == 0) && (doubleLeftJoyY == 0)){
            movementDirection = 0;
        }
        if (doubleLeftJoyX < 0){
            if (doubleLeftJoyY != 0){
                if (doubleLeftJoyY > 0){
                    movementDirection = 90+(-180*(Math.atan(doubleLeftJoyY/doubleLeftJoyX))); //Math.atan is arctangent(x) or (tan^-1)(x), turns sine/cos into degrees
                    //@mccro: conversion rad->deg is 180/Math.PI, you're missing the pi
                    //@mccro: this gives you the cartesian angle, but not bearing angle (i.e. north is 0, east is 90, south 180 and west 270)
                    //@mccro: the standard translation is bearing = 90-cartesian, so that makes 90 - (180/Math.PI)Math.atan(y/x) + 180 or 270-(blah)
                }
                else{
                    movementDirection = 180+(180*(Math.atan(doubleLeftJoyY/doubleLeftJoyX)));//@mccro: see ln181->183
                }
            }
            else{
                movementDirection = 270;
            }
        }
        if (doubleLeftJoyX > 0){
            if (doubleLeftJoyY != 0){
                if (doubleLeftJoyY > 0){//@mccro: shouldn't need to check this, one equation can satisfy both conditions
                    movementDirection = 180*Math.atan(doubleLeftJoyY/doubleLeftJoyX);//@mccro: see ln181
                    //@mccro: this gives you the cartesian angle, but not bearing angle (i.e. north is 0, east is 90, south 180 and west 270)
                    //@mccro: the standard translation is bearing = 90-cartesian, so that makes 90 - (180/Math.PI)Math.atan(y/x)
                }
                else{
                    movementDirection = 180*((Math.atan(doubleLeftJoyY/doubleLeftJoyX))+2);//@mccro: see ln181&197->198
                }
            }
            else{
                movementDirection = 90;
            }
        }
        //pythagoreans theorem to find length of a line from the center of the joystick to the
        //location of the joystick and then the line is
        //put into a speed equation that makes the acceleration easier on the driver
        
        z = ((double)speedModify.get(speedModifier));  //doesn't work ATM returns, "Object cannot be converted to double"
        //@mccro: (double) added above. A dictionary returns generic objects because it doesn't know if you're storing ints or doubles or booleans or whatnot
        //@mccro: so you have to define what variable you are trying to get from it, done with the (double) casting
        z = 1;
        c = Math.abs((speedMultiplier*Math.pow((Math.pow((Math.pow(doubleLeftJoyY,2)+Math.pow(doubleLeftJoyX,2)),.5)),(firstDegreeSpeedModifier))/z));
        for (i = 0; i < speedEquation.length;){
            if (i == 0){
                y = speedEquation[0];
            }
            if (i > 0){
                y = y + (speedEquation[i])*(Math.pow(c,i));
            }
            i++;
        }
        movementSpeed = y/speedDivider;
        //@mccro: what's the significance of 50 for the speed divider? shouldn't a c value of 1 return a movementSpeed of 1? 
        //@mccro: right now it gives 6/50

        //@mccro: you could technically pack ln170->230 into two methods, one to return movementDirection and another to return movementSpeed. discussed more ln255

        //Staight is 0 degrees (towards the other side), backwards is -180 or 180 degrees.
        //Left is negative, Right is positive.
        //Relative to field2
        if ((doublerightTrigger != 0) && (doubleleftTrigger == 0)){
            if (targetRotation < 180){
                targetRotation = targetRotation + (doublerightTrigger*rotationMultiplier);
            }
            if (targetRotation >= 180){
                targetRotation = (doublerightTrigger*rotationMultiplier)-(180-(targetRotation-180)); //could be simplified but easier to understand in this form
                //@mccro: above you have movement rotation 0->360, and ow this -180->180. Neither is wrong, but it might help to be consistent
                //@mccro: you could also use the modulus operator to make this a bit cleaner if going from 0 to 360
            }
        }
        if ((doubleleftTrigger != 0) && (doublerightTrigger == 0)){
            if (targetRotation > -180){
                targetRotation = targetRotation - (doubleleftTrigger*rotationMultiplier);
            }
            if (targetRotation <= -180){
                targetRotation = ((-doubleleftTrigger)*rotationMultiplier)-(180+(targetRotation+180));
                //@mccro: i believe you want a plus instead of a minus    ^  there. A good test is to plug in -180 for targetrotation and see what you get
                //@mccro: before the change i got -180-trigger, after the change i got 180-trigger, which i believe is what you want
            }
        }
        //@mccro: you could put all of the above into a method to make inputUpdate easier to read, i.e. targetRotation = getTargetRot(doubleleftTrigger, doublerightTrigger);
        //@mccro: and getTargetRot does all of the above (ln233->252) and returns targetRotation. Not required, but helps keep the code packed into easier-to-read segments
        //slowly turns robot towards the movement direction
        //when neither triggers are being pressed and the speed is positive
        if (((doubleleftTrigger == 0) && (doublerightTrigger == 0))&& (movementSpeed > .05)){
            //doesn't exist now but will be added if it is wanted
            //@mccro: i doubt we will want this, we likely don't want to change the rotation without a button being pressed
        }
    }

    //runs 50 times a second
    public void update(){
        yaw = Gyro.Yaw();
        
        //finds difference between yaw and target yaw to create rotationDifference
        if (targetRotation < 0 +rotationTolerance){ //@mccro: not sure what rotation tolerance does here. ln273 or 281 still apply regardless
            rotationDifference = ((360+targetRotation)-yaw);
            if (rotationDifference > 180){
                rotationDifference = rotationDifference-360;
            }
        }
        if (targetRotation >= 0 - rotationTolerance){
            rotationDifference = targetRotation-yaw;//@mccro: check this for rotationdifference < -180?
        }
        
        //code to find which wheel is right from movement direction
        if ((movementDirection >= 315)&&(movementDirection<45)){
            rightWheels = new int[]{2,4};
        }
        if ((movementDirection >= 45)&&(movementDirection<135)){
            rightWheels = new int[]{1,2};
        }
        if ((movementDirection >= 135)||(movementDirection < 225)){
            rightWheels = new int[]{1,3};
        }
        if ((movementDirection >= 225)&& (movementDirection < 315)){
            rightWheels = new int[]{3,4};
        }

        //moves the robot
        if (movementSpeed > 0){ //"targetWheelRotation.length()", not ".length;". length is a function
                                //@mccro: length is a field for an array, not a function; array.length is correct
            for(i = 0; i< targetWheelRotation.length;){ //I got lost somewhere. Up to this point, I knew what stuff did. Now I am confused.
                targetWheelRotation[i] = movementDirection;//@mccro: this is true only for the translation, you need to also account for rotation while the robot is moving (maybe, see ln325)
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
            //@mccro: I might be wrong, but it looks like you're trying to rotate while moving by slowing down two of the wheels.
            //@mccro: this is a pretty novel solution, but we're going to want to do this by having the wheel rotation change for each wheel
            //@mccro: in order to avoid "wheel scrub" (where one wheel is moving in a direction it isn't pointing) and to maximize the driving
            //@mccro: speed we can get out of the robot. 
        }


        //rotates the robot
        if (((rotationDifference < rotationTolerance) || (rotationDifference > -rotationTolerance)) && movementSpeed == 0){//@mccro: the || should be an &&
            //@mccro: otherwise you have num<3 or num>-3, so the first makes [-inf->3] true while the second makes [-3->inf] true, so combined they make all real numbers true
            for (a = 0; a < wheelRotSpeed.length;){
                wheelRotSpeed[a] = 0;
                wheelSpeed[a] = 0;
                a++;
            }
        }
        if (movementSpeed == 0 && (rotationDifference >= 0 - rotationTolerance)){//@mccro: same thing in this whole section with needing to divide by pi to convert to deg
            targetWheelRotation[0] = 180+Math.atan(robotY/robotX);//@mccro: given that robotY and robotX are constants, you can just create another constant equal to 
                                                                    //@mccro: Math.atan(robotY/robotX) to simplify what's seen here
            targetWheelRotation[1] = -Math.atan(robotY/robotX);//@mccro: it's kinda odd that one value is above 180, and the other below 0. the two typical ranges are [-180,180]
                                                                //@mccro: and [0,360], so we should try to keep both values in one of those two ranges, and keep the expected
                                                                //@mccro: ranges for the whole code in one or the other, i.e. nothing under 0 or nothing over 180 (like ln244)
            targetWheelRotation[2] = 180+Math.atan(robotY/robotX);//@mccro: i think you want the values for [2] and [3] switched - in pure rotation, opposite corner wheels should
            targetWheelRotation[3] = -Math.atan(robotY/robotX);//@mccro: be facing in the same direction, like a diamond
            //@mccro: /  \  is what the wheels should look like(but perpendicular to a line drawn between them and the center of the robot)
            //@mccro: \  /
            wheelSpeed = new double[]{1,1,1,1};
        }
        if (movementSpeed == 0 && (rotationDifference < 0 + rotationTolerance)){
            targetWheelRotation[0] = 180+Math.atan(robotY/robotX);
            targetWheelRotation[1] = -Math.atan(robotY/robotX);
            targetWheelRotation[2] = 180+Math.atan(robotY/robotX);//@mccro: see ln342->351
            targetWheelRotation[3] = -Math.atan(robotY/robotX);
            wheelSpeed = new double[]{-1,-1,-1,-1};
        }
        //@mccro: i'd recommend looking at using the ControlMode.position for the rotation motors. you're already finding the ideal target angle,
        //@mccro: so you could just convert that into encoder ticks and go rotLeftTop.set(ControlMode.Position, degreesToTicks(targetWheelRotation[0])))
        //@mccro: where degreesToTicks would take a number in degrees and return it in ticks. this year's turret code has a similar layout using this method
        for (a = 0; a < wheelRotSpeed.length;){
            for (i = 0; i < targetWheelRotation.length;){
                wheelRotDifference[i] = targetWheelRotation[i]-currentWheelRotation[i];
                i++;
            }
            for (i = 0; i < rotationEquation.length;){
                if (i == 0){
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
        //@mccro: just want to say, the overall organization and layout of this is awesome. almost every number has a variable or logical reason,
        //@mccro: the arrays really help tidy everything up between the four wheels, and the overall approach is correct and implemented well.
        //@mccro: this is a fantastic start, keep up the good work
    }

    //resets the subsystem for whatever reason
    public void resetState(){
        movementSpeed = 0;
        movementDirection = 0;
        targetRotation = 0;
        Gyro.ahrs.reset();
        yaw = Gyro.Yaw();
        speedModifier = "Normal";
        rightWheels = new int[]{2,4};
        targetWheelRotation = new double[]{0,0,0,0};
        wheelSpeed = new double[]{0,0,0,0};
        currentWheelRotation = new double[]{0,0,0,0};
    }
}
