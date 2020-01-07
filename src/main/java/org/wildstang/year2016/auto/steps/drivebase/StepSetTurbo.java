package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;

public class StepSetTurbo extends AutoStep {
    private boolean turbo;

    public StepSetTurbo(boolean state) {
        this.turbo = state;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        ((DigitalInput) Core.getInputManager().getInput(WSInputs.DRV_BUTTON_5.getName()))
                .setValue(turbo);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Turbo state: " + turbo;
    }

}
