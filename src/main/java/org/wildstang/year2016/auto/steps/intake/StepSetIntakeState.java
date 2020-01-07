package org.wildstang.year2016.auto.steps.intake;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.Intake;

public class StepSetIntakeState extends AutoStep {
    private boolean deployed;

    public StepSetIntakeState(boolean setState) {
        this.deployed = setState;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        if (((Intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE.getName()))
                .isDeployed() != deployed) {
            ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_7.getName()))
                    .setValue(true);
        }
        setFinished(true);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Intake deployed: " + deployed;
    }

}
