package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;

public class StepSetAntiTurbo extends AutoStep {
    private boolean antiTurbo;

    public StepSetAntiTurbo(boolean state) {
        this.antiTurbo = state;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        ((DigitalInput) Core.getInputManager().getInput(WSInputs.DRV_BUTTON_8.getName()))
                .setValue(antiTurbo);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Anti turbo state: " + antiTurbo;
    }

}
