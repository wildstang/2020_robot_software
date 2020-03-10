package org.wildstang.year2020.subsystems.controlpanel;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import java.lang.Math;
import javax.lang.model.util.ElementScanner6;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.Robot;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;

/*
Subsystem Controls: (for XBOX Controller)    
DPAD Left - run the control panel wheel at full power
DPAD Right - run the control panel wheel full reverse
DPAD up - (while button is held) run the control panel deploy upwards until it hits a limit switch
DPAD down - (while button is held) run the control panel deploy downwards until it hits a limit switch
Left Joystick Button - (press once) run the wheel for a set amount of encoder ticks equal to just over three revolutions of the control panel (aim for ~110 inches)
A - runs the intake(control panel wheel) at full power // why not just intake?
Outputs:
intake/control panel wheel - talon
control panel deploy - victor
Sensors:
1 encoder, attached to intake/control panel wheel talon
2 limit switches, limiting the upwards and downwards motion of the control panel deploy
*/

public class ControlPanel implements Subsystem{
    //INTAKE IS LINKED TO SPIN
    //variables, booleans, doubles, motors, etc.
    //motors/inputs
    private TalonSRX deploy;
    private TalonSRX spinner;
    
    private DigitalInput deployUp;
    private DigitalInput deployDown;
    private DigitalInput forwardSpin;
    private DigitalInput backwardSpin;
    private DigitalInput intake;
    private DigitalInput presetSpin;
    
    private AnalogInput colorSelectX;
    private AnalogInput colorSelectY;
    //left joystick axis values
    private double x;
    private double y;
    private double half;
    //converting inputsignal to boolean (add bool to start of input name to reference)
    private boolean booldeployUp;
    private boolean booldeployDown;
    private boolean boolforwardSpin;
    private boolean boolbackwardSpin;
    private boolean boolintake;
    private boolean boolpresetSpin;
    //motor speed doubles
    private double spinSpeed;
    private double deploySpeed;
    //presetSpin encoder setup
    private int presetSpins = 0;
    private int ticksToSpin = 50; //change value to amount of ticks to turn by one color on wheel, set to 3 full rotations at the moment 8 colors per full rotation
    //Motor Attachments like switches and encoder
    private boolean isDown; //deploy is down
    private boolean isUp; //deploy is up
    private double encoder; //double for spinner encoder
    //Allow preset spin to color
    private String gameData;
    private boolean colorSpin;
    private String gotoColor;
    private String currentColor;
    private double angle;
    private String color;
    private double red;
    private double green;
    private double blue;
    private double yellow;
    public int ColorInt = 0;
    private void getAngle(double x, double y){ 
        if (x != 0.0){
            half = Math.abs(x)/x; //-1 if on left side, 1 if on right
            angle = Math.toDegrees(Math.atan(y/x)); //angle from x-axis
        }
    }
    private String getColor(double angle,double half){
        if (half == 1){
            if ((0<angle)&&(angle<(45))){
                color = "green";
            }
            if (((45)<angle)&&(angle<(90))){
                color = "blue";
            }
            if ((0>angle)&&(angle>-(45))){
                color = "red";
            }
            if ((-(45)>angle)&&(angle>-(90))){
                color = "yellow";
            }
        }
        else{
            if ((0<angle)&&(angle<(45))){
                color = "red";
            }
            if (((45)<angle)&&(angle<(90))){
                color = "yellow";
            }
            if ((0>angle)&&(angle>-(45))){
                color = "green";
            }
            if ((-(45)>angle)&&(angle>-(90))){
                color = "blue";
            }
        }
        return color;
    }
    
    @Override
    public String getName(){
        return "ControlPanel";
    }

    @Override
    public void inputUpdate(Input source) {
        //setting input booleans to input statuses
        booldeployUp = deployUp.getValue();
        booldeployDown = deployDown.getValue();
        boolforwardSpin = forwardSpin.getValue();
        boolbackwardSpin = backwardSpin.getValue();
        boolintake = intake.getValue();
        boolpresetSpin = presetSpin.getValue();
        //setting deploy speed
        if ((source == deployUp)||(source ==deployDown)){
            if ((booldeployDown&&booldeployUp)||(!booldeployDown&&!booldeployUp)){
                deploySpeed = 0;
            }
            else if (booldeployUp){
                deploySpeed = 1; //deploy goes up
            }
            else {
                deploySpeed = 1; //deploy goes down, but it's actually the flywheel so it doesn't change direction
            }
        }
        //moving spinner to operator requested direction
        if ((source == forwardSpin)||(source ==backwardSpin)){
            if ((!boolbackwardSpin&&!boolforwardSpin)||(boolbackwardSpin&&boolforwardSpin)){
                spinSpeed = 0;
            }
            else if (boolforwardSpin){
                spinSpeed = 1;
                presetSpins = 0;
            }
            else {
                spinSpeed = -1;
                presetSpins = 0;
            }
        }
        if (source == presetSpin){
            if (boolpresetSpin){
                presetSpins = 1; //set to one at moment but can be changed to +1 later
                spinner.getSensorCollection().setQuadraturePosition(0, 0);
            }
        }
        if (source == intake){
            if (boolintake){
                spinSpeed = 1;
            }
            else{
                spinSpeed = 0;
            }
        }
        if ((source == colorSelectX)||(source == colorSelectY)){
            if ((colorSelectY.getValue() < -0.1)||(colorSelectY.getValue() > 0.1)||(colorSelectX.getValue() > 0.1)||(colorSelectX.getValue() < -0.1) ){
                getAngle(colorSelectX.getValue(),colorSelectY.getValue());
                gotoColor = getColor(angle,half);
                colorSpin = true;
            }
        }
    }
    @Override
    public void init(){
        //Inputs
        deployDown = (DigitalInput) Core.getInputManager().getInput(WSInputs. MANIPULATOR_DPAD_DOWN.getName());
        deployDown.addInputListener(this);//dpadDown
        deployUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP.getName());
        deployUp.addInputListener(this);//dpadUp
        forwardSpin = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_RIGHT.getName());
        forwardSpin.addInputListener(this);//dpadLeft
        backwardSpin = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_LEFT.getName());
        backwardSpin.addInputListener(this);//dpadRight
        presetSpin = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_JOYSTICK_BUTTON.getName());
        presetSpin.addInputListener(this);//left joystick button
        intake = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_DOWN.getName());
        intake.addInputListener(this);//A button
        colorSelectX = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_JOYSTICK_X.getName());
        colorSelectX.addInputListener(this);//left joystick X-Axis
        colorSelectY = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_JOYSTICK_Y.getName());
        colorSelectY.addInputListener(this);//left joystick Y-Axis
        //Outputs
        deploy = new TalonSRX(CANConstants.deploy);
        spinner = new TalonSRX(CANConstants.spinner);
        resetState();
        
    }
    @Override
    public void update(){
        //get colors from limelight
        
        //Initialize Motor Attachments
        isDown = deploy.getSensorCollection().isFwdLimitSwitchClosed();
        isUp = deploy.getSensorCollection().isRevLimitSwitchClosed();
        encoder = spinner.getSensorCollection().getQuadraturePosition();
        //Get color from field
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        if(gameData.length()>0){
            switch(gameData.charAt(0)){
                case'B':
                gotoColor = "blue";
                break;
                case'G':
                gotoColor = "green";
                break;
                case'R':
                gotoColor = "red";
                break;
                case'Y':
                gotoColor = "yellow";
                break;
                default:
                gotoColor = "default";
                break;
            }
        }
        
        //Move until color
        if ((colorSpin)&&(currentColor != gotoColor)){
            switch(gotoColor){
                case"yellow":
                    if(currentColor == "blue"){
                    
                    spinner.set(ControlMode.PercentOutput,-1);
                    }
                    else{
                        spinner.set(ControlMode.PercentOutput,1);
                    }
                break;
                case"red":
                if(currentColor == "yellow"){
                    spinner.set(ControlMode.PercentOutput,-1);
                    }
                    else{
                        spinner.set(ControlMode.PercentOutput,1);
                    }
                break;
                case"blue":
                if(currentColor == "green"){
                    spinner.set(ControlMode.PercentOutput,-1);
                    }
                    else{
                        spinner.set(ControlMode.PercentOutput,1);
                    }
                    break;
                case"green":
               if(currentColor == "red"){
                    spinner.set(ControlMode.PercentOutput,-1);
                    }
                    else{
                        spinner.set(ControlMode.PercentOutput,1);
                    }
                break;
            }
        }
        if ((colorSpin)&&(currentColor == gotoColor)){
            colorSpin = false;
        }
        //Deploy
        if ((deploySpeed) == 1||(deploySpeed == -1)){
            if((!isDown)&&(!isUp)){//if not fully down or up move deploy to operator speed
                deploy.set(ControlMode.PercentOutput,deploySpeed);
            }
            if((isDown)&&(deploySpeed == 1)){//if down but operator speed is up, move up
                deploy.set(ControlMode.PercentOutput,deploySpeed);
            }
            if((isUp)&&(deploySpeed == -1)){//if up but operator speed is down, move down
                deploy.set(ControlMode.PercentOutput,deploySpeed);
            }
        }
        if (deploySpeed == 0){//if operator stops, stop
            deploy.set(ControlMode.PercentOutput,deploySpeed);
        }
        //Spinner (Linked to Intake)
        //Spinner Manual Spin
        if ((spinSpeed == 1) || (spinSpeed == -1)){//if manual is on
            spinner.set(ControlMode.PercentOutput,spinSpeed);
        }
        if ((spinSpeed == 0) && (presetSpins == 0) && !colorSpin){//if manual and presetspins is off
            spinner.set(ControlMode.PercentOutput,spinSpeed);
        }
        //Spinner preset spins.
        if ((presetSpins != 0) && (encoder < (presetSpins*ticksToSpin))){
            spinner.set(ControlMode.PercentOutput,1);
        }
        if ((presetSpins != 0) && (encoder >= (presetSpins*ticksToSpin))){
            spinner.set(ControlMode.PercentOutput,0);
            presetSpins = 0;
        }
    }
    
    @Override
    public void resetState(){
        deploy.set(ControlMode.PercentOutput,0);
        spinner.set(ControlMode.PercentOutput,0);
        spinSpeed = 0;
        deploySpeed = 0;
        presetSpins = 0;
    }
    
    @Override
    public void selfTest(){
    }
    private void DoColorStuff(){
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Red");
		NetworkTableEntry tv = table.getEntry("tv");
		red = tv.getDouble(0.0);
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Green");
		NetworkTableEntry tv = table.getEntry("tv");
		green = tv.getDouble(0.0);
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Blue");
		NetworkTableEntry tv = table.getEntry("tv");
		blue = tv.getDouble(0.0);
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Yellow");
		NetworkTableEntry tv = table.getEntry("tv");
		yellow = tv.getDouble(0.0);
        //Color Int states: 0- none 1- red 2- yellow 3- green 4- blue
        if ((yellow==1) && (ColorInt !=2)){
            ColorInt = -1;
        }else{
            if ((yellow==1) && (ColorInt ==0)){
                ColorInt = 2;
            }
        }
        if ((red==1) && (ColorInt !=1)){
            ColorInt = -1;
        }else{
            if ((red==1) && (ColorInt ==0)){
                ColorInt = 1;
            }
        }
        if ((green==1) && (ColorInt !=3)){
            ColorInt = -1;
        }else{
            if ((green==1) && (ColorInt ==0)){
                ColorInt = 3;
            }
        }
        if ((blue==1) && (ColorInt != 4)){
            ColorInt = -1;
        } else{
            if ((blue==1) && (ColorInt ==0)){
                ColorInt = 4;
            }
        }
        if (ColorInt == -1){
            ColorInt = 0;
        }
        switch (ColorInt){
            case(1):
               currentColor = "red";
                break; 
                case(2):
               currentColor = "yellow";
                break; 
                case(3):
               currentColor = "green";
                break; 
                case(4):
               currentColor = "blue";
                break; 
                case(0):
                break;
            
        }
    }
}   
