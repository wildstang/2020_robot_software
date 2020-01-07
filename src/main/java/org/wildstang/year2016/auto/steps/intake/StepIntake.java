package org.wildstang.year2016.auto.steps.intake;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.robot.WSSubsystems;
import org.wildstang.year2016.subsystems.Intake;

public class StepIntake extends AutoStep {
    private int speed;

    public StepIntake(int runningSpeed) {
        this.speed = runningSpeed;
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        ((Intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE.getName()))
                .setIntakeOverrideOn(true);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        // ((AnalogInput)Core.getInputManager().getInput(WSInputs.MAN_LEFT_JOYSTICK_Y.getName())).setValue(speed);
        ((Intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE.getName()))
                .IntakeValue(speed);
        setFinished(true);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Running intake at: " + speed;
    }

}
