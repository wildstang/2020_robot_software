package org.wildstang.year2020.subsystems.controlpanel;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

    //controlPanelDeploy
    private int movedeploy;
    private TalonSRX Deploy;

    //inputs
    private DigitalInput DpadUP; 
    private DigitalInput DpadDWN;
    private DigitalInput FWDspin;
    private DigitalInput BWDspin;
    private DigitalInput INTspin;
    private DigitalInput ENCspin;

    //controlPanelSpinner
    private boolean spininput = false;
    private DigitalInput Spin;
    private TalonSRX Spinner;
    private double Encoder;
    private boolean spinOn = false;
    private double spinMax;

    //intake 
    // private boolean spininput detimines intake
    // private VictorSPX Intake    

    //int/double
    private double CMDspin;
    private double UPWAIT = 5.25;
    private int Spins = 0;
    private int DeployOn;
    //bools
    
    private boolean IsDown;
    private boolean start = false;
    private boolean off;
    private boolean on;
    private boolean UPbool;
    private boolean DWNbool;
    private boolean FWDbool;
    private boolean BWDbool;
    private boolean ENCbool;
    private boolean INTbool;

    @Override
    public String getName(){
        return "ControlPanel";
    }
    @Override
    public void inputUpdate(Input source) {
        UPbool = DpadUP.getValue();
        DWNbool = DpadDWN.getValue();
        FWDbool = FWDspin.getValue();
        BWDbool = BWDspin.getValue();
        ENCbool = ENCspin.getValue();
        INTbool = INTspin.getValue();
        if ((source == DpadUP) && UPbool){ 
            switch(DeployOn){
            case 0:
            case 1:
            case 4:
                DeployOn = 1;
                break;
            }
            else (!UPbool){
                DeployOn = 4;
            }
        }
        if ((source == DpadDWN) && DWNbool){
            switch(DeployOn){
            case 3:
            case 2:
            case 4:
                DeployOn = 2;
                break;
            }
            else (!UPbool){
                DeployON = 4;
            }
         }
         
        //spinner
        if (FWDbool){
            CMDspin = 1; //if FWD, spin forward
        }
        else{
        if(BWDbool){
                CMDspin = -1;    //otherwise, if BWD, spin backwards
         }
         else{
           if (CMDspin != 6) //CMD 6 means encoder is running
         CMDspin = 0; //if nothing pressed and encoder not running, turn motor off 
            }
        }
        //more spiner
        if ((source == ENCspin) && ENCbool){
            Spins += (1500);
            spinOn = true;
            CMDspin = 6;
            Spinner.getSensorCollection().setQuadraturePosition(0, 0);
        //intake controls
        if (INTbool){
            start = true;
        }
        else{
            off = true;
        }
    }
 }
    @Override
    public void init() {

        // InputListeners
        DpadDWN = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPDEPLOY_DPAD_DOWN.getName());
        DpadDWN.addInputListener(this);
        DpadUP = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPDEPLOY_DPAD_UP.getName());
        DpadUP.addInputListener(this);
        FWDspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPWHEEL_DPAD_LEFT.getName());
        FWDspin.addInputListener(this);
        BWDspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.CPWHEEL_DPAD_RIGHT.getName());
        BWDspin.addInputListener(this);
        ENCspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.CONTROL_PANEL_WHEEL.getName());
        ENCspin.addInputListener(this);
        INTspin = (DigitalInput) Core.getInputManager().getInput(WSInputs.INTAKE.getName());
        INTspin.addInputListener(this);
        //Motors
        Deploy = new TalonSRX(CANConstants.CPDEPLOY_TALON);        
        Spinner = new TalonSRX(CANConstants.INTAKECPWHEEL_TALON);
        resetState();
    }
        
    @Override
    public void update() {
        Encoder = Spinner.getSensorCollection().getQuadraturePosition();
        //control panel spinner
        if (Encoder >= Spins && spinOn == true){ 
            Spinner.set(ControlMode.PercentOutput, 0.0);
            spinOn = false;
        }
         if (spinOn == true){
            Spinner.set(ControlMode.PercentOutput, 1.0);
        }
        if (CMDspin != 6.0){
            Spinner.set(ControlMode.PercentOutput, CMDspin);
        }
        //intake commands
        if (off){
        Spinner.set(ControlMode.PercentOutput,0);
        off = false;
        }
        if (start){
            Spinner.set(ControlMode.PercentOutput,1);
            start = false;
        }
    }
    @Override
    public void resetState(){
    }
    
    @Override
    public void selfTest() {
    }
}