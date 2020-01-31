package org.wildstang.year2020.subsystems.controlpanel;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import javax.lang.model.util.ElementScanner6;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
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
    String gameData;
    
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
            if (booldeployUp&&(!booldeployDown)){
                deploySpeed = 1; //deploy goes up
            }
            if (booldeployDown&&(!booldeployUp)){
                deploySpeed = -1; //deploy goes down
            }
            if ((!booldeployDown)&&(!booldeployUp)){
                deploySpeed = 0;
            }
        }
        //moving spinner to operator requested direction
        if ((source == forwardSpin)||(source ==backwardSpin)){
            if (boolforwardSpin&&(!boolbackwardSpin)){
                spinSpeed = 1;
                presetSpins = 0;
            }
            if (boolbackwardSpin&&(!boolforwardSpin)){
                spinSpeed = -1;
                presetSpins = 0;
            }
            if ((!boolbackwardSpin)&&(!boolforwardSpin)){
                spinSpeed = 0;
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
        
    }
    @Override
    public void init(){
        // InputListeners
        deployDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.deployDown.getName());
        deployDown.addInputListener(this);//dpadDown
        deployUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.deployUp.getName());
        deployUp.addInputListener(this);//dpadUp
        forwardSpin = (DigitalInput) Core.getInputManager().getInput(WSInputs.forwardSpin.getName());
        forwardSpin.addInputListener(this);//dpadLeft
        backwardSpin = (DigitalInput) Core.getInputManager().getInput(WSInputs.backwardSpin.getName());
        backwardSpin.addInputListener(this);//dpadRight
        presetSpin = (DigitalInput) Core.getInputManager().getInput(WSInputs.presetSpin.getName());
        presetSpin.addInputListener(this);//left joystick button
        intake = (DigitalInput) Core.getInputManager().getInput(WSInputs.intake.getName());
        intake.addInputListener(this);//A button
        //Motors
        deploy = new TalonSRX(CANConstants.deploy);
        spinner = new TalonSRX(CANConstants.spinner);
        resetState();
    }
    @Override
    public void update(){
        //Initialize Motor Attachments
        isDown = deploy.getSensorCollection().isFwdLimitSwitchClosed();
        isUp = deploy.getSensorCollection().isRevLimitSwitchClosed();
        encoder = spinner.getSensorCollection().getQuadraturePosition();
        //Get color from field
        gameData = DriverStation.getInstance().getGameSpecificMessage();
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
        if ((spinSpeed == 0) && (presetSpins == 0)){//if manual and presetspins is off
            spinner.set(ControlMode.PercentOutput,spinSpeed);
        }
        //Spinner preset spins.
        if ((presetSpins != 0) && (encoder > (presetSpins*ticksToSpin))){
            spinner.set(ControlMode.PercentOutput,1);
        }
        if ((presetSpins != 0) && (encoder <= (presetSpins*ticksToSpin))){
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
}