package org.wildstang.year2016.auto.steps.shooter;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;

public class StepResetShooterPositionToggle extends AutoStep {

    public StepResetShooterPositionToggle() {
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_6.getName()))
                .setValue(false);
        setFinished(true);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Shooter toggle reset";
    }

}
