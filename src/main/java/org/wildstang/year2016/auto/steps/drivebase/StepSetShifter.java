/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.year2016.auto.steps.drivebase;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.DriveBase;

/**
 *
 * @author Joey
 */
public class StepSetShifter extends AutoStep {
    protected boolean highGear;

    public StepSetShifter(boolean highGear) {
        this.highGear = highGear;
    }

    @Override
    public void initialize() {
        ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                .setShifter(highGear);
        setFinished(true);
    }

    @Override
    public void update() {
        // if(((DriveBase)Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName())).shifterState()
        // != highGear)
        // {
        // ((DigitalInput)Core.getInputManager().getInput(WSInputs.DRV_BUTTON_7.getName())).setValue(true);
        // }
    }

    @Override
    public String toString() {
        return "Set Shifter State" + highGear;
    }

}
