package org.wildstang.year2020.auto.steps;

import org.wildstang.year2020.robot.WSSubsystems;
import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;
import org.wildstang.year2020.subsystems.launching.Shooter;

public class IntakeOnStep extends AutoStep{

    private Ballpath intake;
    private Shooter flywheel;
    private boolean modifier;

    public IntakeOnStep(boolean isOn){
        this.modifier = isOn;
    }
    public void update() {
        if (modifier) intake.turnOnIntake();
        else intake.turnOffIntake();
        flywheel.autoOn();
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Intake On";
    }
    public void initialize(){
        intake = (Ballpath) Core.getSubsystemManager().getSubsystem(WSSubsystems.BALLPATH.getName());
        flywheel = new Shooter();
    }
}