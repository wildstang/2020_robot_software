package org.wildstang.year2020.subsystems.controlPanel;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.timer.WsTimer;


public class ControlPanel implements Subsystem{

    //controlPanelDeploy
    private int movedeploy;
    private DigitalInput CpDeploy;
    private VictorSPX Deploy;


    //controlpanelspinner
    private boolean spininput = false;
    private DigitalInput CpSpin;
    private TalonSPX CPSpinner;
    private double Encoder;
    private boolean spinOn;
    private double spinMax;


    //intake 

    // private boolean spininput detimines intake
    private VictorSPX Intake;


    //other booleans/ints
    private int CpDeployOn = 0;
    // 1 = Up, 0 = Off-Down, 2 = Down, 3 = Off-Up
    private boolean CpSpeedOn == false;
    private boolean IsDown;
    private WsTimer timer = new WsTimer();
    private boolean TimerHasStarted == false;
    private double UPWAIT = 5.25;
    private boolean spinOn = false;
    private int Spins = 0;



    @Override
    public void inputUpdate(Input source) {
        
        if (CpDeploy){
            if (CpDeployOn = 0){
                CpDeployOn = 1;
                
            }
            if (CpDeployOn = 1){
                CpDeployOn = 1;
            }
            if (CpDeployOn =3){
                CpDeployOn = 2;
            }
            if (CpDeployOn = 2){
                CpDeployOn = 2;
            }
        }
        if (CpSpeed){
            if (CpSpeedOn == true){
                CpSpeedOn == false;
            }
            if (CpSpeedOn == false){
                CpSpeedOn == true;
            }
        }
        //spinner
        if (source == CpSpin){
            Spins = Spins+(6000*.25);
            spinOn == true;
        }
 }
    @Override
    public void init() {

        // InputListeners
        CpDeploy = (DigitalInput) Core.getInputManager().getInput(WSInputs.CpSpeed.getName());
        CpDeploy.addInputListener(this);
        CpSpin = (DigitalInput) Core.getInputManager().getInput(WsInputs.CpSpeed.getName());
        CpSpin.addInputListener(this);
        
        //Motors
        Deploy = new VictorSPX(CANConstants.DEPLOY);        
        CPSpinner = new TalonSPX(CANConstants.SPINNER);
        Intake = new VictorSPX(CANConstants.SPINNER);
        resetState();
    }
        
    @Override
    public void update() {
        IsDown = motor.getSensorCollection().isFwdLimitSwitchClosed());
        Encoder = CPSpinner.getSensorCollection().get();
        if (IsDown == true){
            if (CpDeployOn = 2){
                CpDeployOn = 0;
                Deploy.set(ControlMode.PercentOutput, 0);
            }
        }
        if (!IsDown){
            if (CpDeployOn = 2){
                Deploy.set(ControlMode.PercentOutput, -1);
            }
        }
        if (IsDown == true){
            if (CpDeployOn = 1){
                if(TimerHasStarted == false){
                    timer.reset();
                    Deploy.set(ControlMode.PercentOutput, 1);
                    TimerHasStarted = true;
                }
                if(timer.hasPeriodPassed(UPWAIT)){
                    Deploy.set(ControlMode.PencentOutput,0);
                    CpDeployOn = 3;
                    TimerHasStarted == false;
                }
            } 
        }
        //rotation control
        if (Encoder >= Spins){
            CPSpinner.set(ControlMode.PercentOutput, 0.0);
            spinOn == false;
        }
        if (spinOn == true){
            CPSpinner.set(ControlMode.PercentOutput, 1.0);
        }
    }
}