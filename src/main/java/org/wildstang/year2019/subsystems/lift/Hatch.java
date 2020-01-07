package org.wildstang.year2019.subsystems.lift;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSInputs;
import org.wildstang.year2019.robot.WSOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.hardware.crio.outputs.WsSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoidState;

import org.wildstang.framework.timer.WsTimer;

public class Hatch implements Subsystem {

    //Timer constants TODO: Measure time during testing
    private static final double DEPLOY_WAIT = 0.15;
    private static final double RETRACT_WAIT = 0.1;
    private static final double LOCK_WAIT = 0.2;
    // Local inputs
    private DigitalInput hatchDeploy;
    private DigitalInput hatchCollect;
    private DigitalInput startButton;
    private WsTimer timer = new WsTimer();

    // Local outputs
    private WsSolenoid hatchOut;
    private WsSolenoid hatchLock;

    // Logical variables
    private boolean outPosition;  // true  = Extended
                                  // false = Retracted
    private boolean lockPosition; // true  = Deployed (mechanism can push through hatch panel opening and extends so it cannot
                                  //                      fit back through opening)
                                  // false = Retracted (mechanism can freely move in any direction through hatch panel opening)
    private boolean working;
    private boolean isStartPressed;

    private boolean collectPrev;
    private boolean collectCurrent;
    private boolean isCollect;

    public static final boolean lockVal = false;
    public static final boolean outVal = false;

    //No Longer using
    // private long deployRestartLastMovementTime;
    // private long deployLastMovementTime;
    
    // private long collectRestartLastMovementTime;
    // private long collectLastMovementTime;


    enum commands {
        IDLE, DEPLOY, COLLECT, COLLECT2;
    }
    private int currentCommand; // 0 = Idle
                                // 1 = Deploy restart
                                // 2 = Collect restart
                                // 3 = Deploy
                                // 4 = Collect

    @Override
    public void inputUpdate(Input source) {
        if(source == startButton) {
            isStartPressed = true;
        } else {
            isStartPressed = false;
        }

        if (source == hatchDeploy) {
            
            if (currentCommand == commands.IDLE.ordinal()  && hatchDeploy.getValue() == true) {
                currentCommand = commands.DEPLOY.ordinal();
                
            } 
        }
        
        // if (source == hatchCollect) {
            

        //     if (currentCommand == commands.IDLE.ordinal() && hatchCollect.getValue() == true && !isStartPressed){
        //         currentCommand = commands.COLLECT.ordinal();
        //     } else if (currentCommand == commands.COLLECT.ordinal() && hatchCollect.getValue()==false){
        //         currentCommand = commands.COLLECT2.ordinal();
        //     }
        // }
        if(source == hatchCollect) {
            collectCurrent = hatchCollect.getValue();
            if (collectCurrent && !collectPrev) {
                lockPosition = !lockPosition;
            } 
        collectPrev = collectCurrent;
    }
    }

    @Override
    public void init() {
        // Link digital inputs and outputs to physical controller and robot
        hatchDeploy = (DigitalInput) Core.getInputManager().getInput(WSInputs.HATCH_DEPLOY.getName());
        hatchDeploy.addInputListener(this);

        hatchCollect = (DigitalInput) Core.getInputManager().getInput(WSInputs.HATCH_COLLECT.getName());
        hatchCollect.addInputListener(this);

        startButton = (DigitalInput) Core.getInputManager().getInput(WSInputs.WEDGE_SAFETY_2.getName());
        startButton.addInputListener(this);

        hatchOut = (WsSolenoid) Core.getOutputManager().getOutput(WSOutputs.HATCH_OUT_SOLENOID.getName());
        hatchLock = (WsSolenoid) Core.getOutputManager().getOutput(WSOutputs.HATCH_LOCK_SOLENOID.getName());

        timer.start();
        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        
        if (currentCommand == commands.DEPLOY.ordinal()) {
            if (!working) {
                working = true;
                outPosition = !outVal;
                hatchOut.setValue(outPosition);

                timer.reset();
            } else if (timer.hasPeriodPassed(DEPLOY_WAIT) && !timer.hasPeriodPassed(RETRACT_WAIT + DEPLOY_WAIT)) {
                lockPosition = !lockVal;
                hatchLock.setValue(lockPosition);

            } else if (timer.hasPeriodPassed(DEPLOY_WAIT + RETRACT_WAIT) && !timer.hasPeriodPassed(2*DEPLOY_WAIT + RETRACT_WAIT)) {
                outPosition = outVal;
                hatchOut.setValue(outPosition);
                working = false;
                currentCommand = commands.IDLE.ordinal();
            } 
            //else if (timer.hasPeriodPassed(2*DEPLOY_WAIT + RETRACT_WAIT)) {
            //     lockPosition = lockVal;
            //     hatchLock.setValue(lockPosition);

            //     working = false;

            //     currentCommand = commands.IDLE.ordinal();
            // }
        } 
            hatchLock.setValue(lockPosition);
        
        // } else if (currentCommand == commands.COLLECT.ordinal()) {
        //     if (!working){
        //         lockPosition = !lockVal;
        //         hatchLock.setValue(lockPosition);
        //         //currentCommand = commands.COLLECT2.ordinal();
        //     }
        //     // if (!working) {
        //     //     working = true;
        //     //     outPosition = !outVal;
        //     //     lockPosition= !lockVal;
        //     //     hatchOut.setValue(outPosition);
        //     //     hatchLock.setValue(lockPosition);
        //     //     timer.reset();
        //     //     working=false;
        //     // } //else if (timer.hasPeriodPassed(DEPLOY_WAIT)) {
        //     //     outPosition = outVal;
        //     //     hatchOut.setValue(outPosition);
        //     //     working = false;

        //     //     currentCommand = commands.IDLE.ordinal();
        //     // }
        //  } else if (currentCommand == commands.COLLECT2.ordinal()){
        //      lockPosition = lockVal;
        //      hatchLock.setValue(lockPosition);
        //      currentCommand = commands.IDLE.ordinal();
        // //     if (!working) {
        // //         timer.reset();
        // //         working = true;
        // //     }
        // //     outPosition=outVal;
        // //     lockPosition=lockVal;
        // //     hatchLock.setValue(lockPosition);
        // //     if (timer.hasPeriodPassed(LOCK_WAIT)){
                
        // //         hatchOut.setValue(outPosition);
        // //         working=false;
        // //         currentCommand = commands.IDLE.ordinal();
        //      }
            
        // }
        
        SmartDashboard.putBoolean("Hatch Out", hatchOut.getValue());
        SmartDashboard.putBoolean("Hatch Lock", hatchLock.getValue());
    }

    @Override
    public void resetState() {
        // Reset local variables back to default state
        outPosition = outVal;
        lockPosition = lockVal;
        hatchOut.setValue(outPosition);
        hatchLock.setValue(lockPosition);
        collectPrev = false;
        collectCurrent = false;
        isCollect = false;

        working = false;

        currentCommand = commands.IDLE.ordinal();
    }

    @Override
    public String getName() {
        return "Hatch";
    }
    public boolean deployAuto(){
        // currentCommand = commands.DEPLOY.ordinal();
        lockPosition = true;
        update();
        if (currentCommand == commands.IDLE.ordinal()) return true;
        return false;
    }
    public boolean collectAuto(boolean passed){
        lockPosition = false;
        update();

        if (currentCommand == commands.IDLE.ordinal()) return true;
        return false;
    }
}