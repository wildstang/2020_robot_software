package org.wildstang.year2020.subsystems.controlPanel;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;

public class cpintake implements Subsystem {

    //controlPanelDeploy
    private int movedeploy;
    private DigitalInput CpDeploy;
    private VictorSPX Deploy;
    private bool up;
    private bool down;
    private bool DeployChangeState;
     
    //controlpanelspinner
    private boolean spininput;
    private DigitalInput CpSpeed;
    private TalonSPX CPSpinner;

    private double spinMAX; //add value to this
    private bool Startspin;
    



    //intake 
    // private boolean spininput detimines intake
    private VictorSPX Intake;
     
    

    @Override
    public void inputUpdate(Input source) {
        //if buttons are pressed

        //button 1
       if (CpSpeed){
           if(!spininput){
               CPSpinner.getSensorCollection().setQuadraturePosition();
               Startspin = true;
           }
           RunIntake = true;
       }
       else{
           RunIntake = false;
       }
        //button 2
        if (CpDeploy){
            DeployChangeState = true;
        }
    }

    @Override
    public void init() {
        
        // InputListeners
        
        CpDeploy = (DigitalInput) Core.getInputManager().getInput(WSInputs.CpSpeed.getName());
        CpDeploy.addInputListener(this);
        CpSpeed = (DigitalInput) Core.getInputManager().getInput(WsInputs.CpSpeed.getName());
        CpSpeed.addInputListener(this);
        

        //Motors
        Deploy = new VictorSPX(CANConstants.DEPLOY);        
        CPSpinner = new TalonSPX(CANConstants.SPINNER);
        Intake = new VictorSPX(CANConstants.SPINNER);
        resetState();

    @Override
    public void update() {

        //if moter is active
    Encoder = CPSpinner.getSensorCollection().getQuadraturePosition();
    if (spininput && Encoder >= spinMAX){
        CPSpinner.set(ControlMode.PercentOutput,0.0);
        spininput = false;
    }
    if (Startspin){
        CPSpinner.set(ControlMode.PercentOutput,1.0);
        Startspin = false;
    }
    if (RunIntake){
        Intake.set(ControlMode.PercentOutput,1.0);
    
    }
    else{
        Intake.set(ControlMode.PercentOutput,0.0)
    }

    up = getSensorCollection().getForwardSwitch();
    down = getSensorCollection().getBackwardSwitch();
    if (up || down){
        Deploy.set(ControlMode.PercentOutput,-1.0);
    }

    if (DeployChangeState){
    if (up){
        Deploy.set(ControlMode.PercentOutput,-1.0);
    }
    else{
        if(down){
            Deploy.set(ControlMode.PercentOutput,1.0);
        }
    }
    DeployChangeState = false;
    }   
    }

    @Override
    public void resetState() {
        Startspin = false;
        RunIntake = false;
        spininput = false;
        DeployChangeState = false;
        
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "controlpanel";
    }






}
