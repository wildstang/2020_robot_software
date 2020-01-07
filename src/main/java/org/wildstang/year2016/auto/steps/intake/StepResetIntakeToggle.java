package org.wildstang.year2016.auto.steps.intake;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;

public class StepResetIntakeToggle extends AutoStep {
    public StepResetIntakeToggle() {
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_7.getName()))
                .setValue(false);
        setFinished(true);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Intake toggle reset";
    }

}
