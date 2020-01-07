/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;

/**
 *
 * @author Billy
 */
public class StepResetShifterToggle extends AutoStep {

    public StepResetShifterToggle() {
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        ((DigitalInput) Core.getInputManager().getInput(WSInputs.DRV_BUTTON_7.getName()))
                .setValue(false);
    }

    @Override
    public String toString() {
        return "Shifter toggle reset";
    }

}
