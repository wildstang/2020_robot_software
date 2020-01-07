package org.wildstang.year2016.auto.steps.shooter;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.Shooter;

public class StepSetShooterPosition extends AutoStep {
    private boolean state;

    public StepSetShooterPosition(boolean position) {
        this.state = position;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        if (((Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName()))
                .hoodPos() != state) {
            ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_6.getName()))
                    .setValue(true);
        }
        setFinished(true);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Shooter deployed: " + state;
    }

}
