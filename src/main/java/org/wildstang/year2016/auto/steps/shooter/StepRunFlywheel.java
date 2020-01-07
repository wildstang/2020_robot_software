package org.wildstang.year2016.auto.steps.shooter;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.Shooter;

public class StepRunFlywheel extends AutoStep {
    private int speed;

    public StepRunFlywheel(int speed) {
        this.speed = speed;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        if (speed == 0 && ((Shooter) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.SHOOTER.getName())).isOn() == true) {
            ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_3.getName()))
                    .setValue(true);
            // ((DigitalInput)Core.getInputManager().getInput(WSInputs.MAN_BUTTON_3.getName())).setValue(false);
        }

        else if (((Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName()))
                .isOn() == false && (speed != 0)) {
            ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_3.getName()))
                    .setValue(true);
            // ((DigitalInput)Core.getInputManager().getInput(WSInputs.MAN_BUTTON_3.getName())).setValue(false);
        }

        ((Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName()))
                .setFlySpeed(speed);

        setFinished(true);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Running flywheel at " + speed;
    }

}
