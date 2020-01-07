package org.wildstang.year2019.subsystems.climbwedge;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSInputs;
import org.wildstang.year2019.robot.WSOutputs;
import org.wildstang.year2019.robot.Robot;
import org.wildstang.hardware.crio.outputs.WsSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoidState;
import org.wildstang.framework.timer.WsTimer;

/** This subsystem controls the wedge that will allow us to climb at game end. *

Sensors: none

Actuators:
<ul>
<li> Wedge deploy piston solenoid
</ul>

*/

public class ClimbWedge implements Subsystem {

    private boolean wedgeButton1Status;
    private boolean wedgeButton2Status;
    private boolean deployWedgeStatus;
    private boolean timerStatus;
    
    private DigitalInput wedgeButton1;
    private DigitalInput wedgeButton2;

    private WsSolenoid deployWedge;

    private WsTimer timer = new WsTimer();
    
    private final double solenoidDelay = 0.5; //Constant stores delay length in seconds

    @Override
    public void inputUpdate(Input source) {
        //Stores the status of wedge buttons into local variables
        if (source == wedgeButton1) {
            wedgeButton1Status = wedgeButton1.getValue();
        }

        if (source == wedgeButton2) {
            wedgeButton2Status = wedgeButton2.getValue();
        }

    }

    @Override
    public void init() {
        //Links digital inputs and outputs to the physical controller and robot
        wedgeButton1 = (DigitalInput) Core.getInputManager().getInput(WSInputs.WEDGE_SAFETY_1.getName());
        wedgeButton1.addInputListener(this);

        wedgeButton2 = (DigitalInput) Core.getInputManager().getInput(WSInputs.WEDGE_SAFETY_2.getName());
        wedgeButton2.addInputListener(this);

        deployWedge = (WsSolenoid) Core.getOutputManager().getOutput(WSOutputs.WEDGE_SOLENOID.getName());

        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        //Checks if both buttons assigned to wedge have been pressed down
        if (wedgeButton1Status && wedgeButton2Status) {
            if (timerStatus) {
                if (timer.hasPeriodPassed(solenoidDelay)) {
                    deployWedgeStatus = true;
                }
            } else if (!timerStatus) {
                timer.reset();
                timer.start();
                timerStatus = true;
            }
        } else {
            timer.stop();
            timerStatus = false;
        }
        if (deployWedgeStatus) deployWedge.setValue(false);
    }

    @Override
    public void resetState() {
        //Reset local variables back to default state
        wedgeButton1Status = false;
        wedgeButton2Status = false;
        deployWedgeStatus = true;
        timerStatus = false;
    }

    @Override
    public String getName() {
        return "ClimbWedge";
    }
}