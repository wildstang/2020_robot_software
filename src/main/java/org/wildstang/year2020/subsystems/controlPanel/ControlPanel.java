package org.wildstang.year2020.subsystems.controlPanel;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.timer.WsTimer;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.Robot;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;


import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;

import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    //controlPanelDeploy
    private int movedeploy;
    private TalonSRX Deploy;
    private TalonSRX CpSpinner;


    //inputs
    private DigitalInput CpDpadUP; 
    private DigitalInput CpDpadDWN;
    private DigitalInput CpDpadL;
    private DigitalInput CpDpadR;
    private DigitalInput CpA;
    private DigitalInput Cpjoy;
    private DigitalInput CpFWDspin;
    private DigitalInput CpBWDspin;
    private DigitalInput CpINTspin;
    private DigitalInput CpENCspin;

    //controlpanelspinner
    private boolean spininput = false;
    private DigitalInput CpSpin;
    private TalonSRX CPSpinner;
    private double Encoder;
    private boolean spinOn = false;
    private double spinMax;


    //intake 

    // private boolean spininput detimines intake
   // private VictorSPX Intake;


    //other booleans/ints
    private int CpDeployOn = 0;
    // 1 = Up, 0 = Off-Down, 2 = Down, 3 = Off-Up
    private boolean IsDown;
    private WsTimer timer = new WsTimer();
    private boolean TimerHasStarted = false;
    private double UPWAIT = 5.25;
    private int Spins = 0;
    private boolean start = false;
    private double CMDspin;
    private boolean off;
    private boolean on;
    private boolean CpUPbool;
    private boolean CpDWNbool;
    private boolean CpFWDbool;
    private boolean CpBWDbool;
    private boolean CpENCbool;
    private boolean CpINTbool;

    @Override
    public String getName(){
        return "controlPanel";
    }
    @Override
    public void inputUpdate(Input source) {
        CpUPbool = CpDpadUP.getValue();
        CpDWNbool = CpDpadDWN.getValue();
        CpFWDbool = CpFWDspin.getValue();
        CpBWDbool = CpBWDspin.getValue();
        CpENCbool = CpENCspin.getValue();
        CpINTbool = CpINTspin.getValue();
        if ((source == CpDpadUP) && CpUPbool){ 
            switch(CpDeployOn){
            case 0:
                CpDeployOn = 1;
                break;
            case 1:
                CpDeployOn = 1;
                break;
            }
        }
        if ((source == CpDpadDWN) && CpDWNbool){
            switch(CpDeployOn){
            case 3:
                CpDeployOn = 2;
                break;
            case 2:
                CpDeployOn = 2;
                break;
            }
         }
        
        //spinner
        
        if (CpFWDbool){
            CMDspin = 1; //if FWD, spin forward
        }
        else{
        if(CpBWDbool){
                CMDspin = -1;    //otherwise, if BWD, spin backwards
         }
         else{
           if (CMDspin != 6) //CMD 6 means encoder is running
         CMDspin = 0; //if nothing pressed and encoder not running, turn motor off 
            }
        }
        //intake controls
        if (CpINTbool){
            start = true;
        }
        else{
            off = true;
        }
        //encoder based spinning
        if ((source == CpENCspin) && CpENCbool){
            Spins = Spins + (1500);
            spinOn = true;
            CMDspin = 6;
            CPSpinner.getSensorCollection().setQuadraturePosition(0, 0);
            
        }
 }
    @Override
    public void init() {

        // InputListeners
        CpDpadDWN = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPDEPLOY_DPAD_DOWN.getName());
        CpDpadDWN.addInputListener(this);
        CpDpadUP = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPDEPLOY_DPAD_UP.getName());
        CpDpadUP.addInputListener(this);
        CpFWDspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPWHEEL_DPAD_LEFT.getName());
        CpFWDspin.addInputListener(this);
        CpBWDspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPWHEEL_DPAD_RIGHT.getName());
        CpBWDspin.addInputListener(this);
        CpENCspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.CONTROL_PANEL_WHEEL.getName());
        CpENCspin.addInputListener(this);
        CpINTspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.INTAKE.getName());
        CpINTspin.addInputListener(this);

        //Motors
        Deploy = new TalonSRX(CANConstants.CPDEPLOY_TALON);        
        CPSpinner = new TalonSRX(CANConstants.INTAKECPWHEEL_TALON);
        
        resetState();
    }
        
    @Override
    public void update() {
        IsDown = Deploy.getSensorCollection().isFwdLimitSwitchClosed();
        Encoder = CPSpinner.getSensorCollection().getQuadraturePosition();;
        if (IsDown == true){
            if (CpDeployOn == 2){
                CpDeployOn = 0;
                Deploy.set(ControlMode.PercentOutput, 0);
            }
        }
        if (!IsDown){
            if (CpDeployOn == 2){
                Deploy.set(ControlMode.PercentOutput, -1);
            }
        }
        if (IsDown == true){
            if (CpDeployOn == 1){
                if(!TimerHasStarted){
                    timer.reset();
                    Deploy.set(ControlMode.PercentOutput, 1);
                    TimerHasStarted = true;
                }
                if(timer.hasPeriodPassed(UPWAIT)){
                    Deploy.set(ControlMode.PercentOutput,0);
                    CpDeployOn = 3;
                    TimerHasStarted = false;
                }
            } 
        }
        //control panel spinner
        if (Encoder >= Spins && spinOn == true){ 
            CPSpinner.set(ControlMode.PercentOutput, 0.0);
            spinOn = false;
            
        }
         if (spinOn == true){
            CPSpinner.set(ControlMode.PercentOutput, 1.0);
            
        }
        if (CMDspin != 6.0){
            CPSpinner.set(ControlMode.PercentOutput, CMDspin);
        }
        //intake commands
        if (off){
        CpSpinner.set(ControlMode.PercentOutput,0);
        off = false;
        }
        if (start){
            CpSpinner.set(ControlMode.PercentOutput,1);
            start = false;
        }
       
    }
    @Override
    public void resetState(){

    }
    @Override
    public void selfTest() {
        // DO NOT IMPLEMENT
        // TODO WHY NOT?
    }

}