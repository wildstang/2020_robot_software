package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;
import org.wildstang.year2020.subsystems.launching.Shooter;

public class IntakeOnStep extends AutoStep{

    private Ballpath intake;
    private Shooter flywheel;

    public void update() {
        intake.turnOnIntake();
        flywheel.autoOn();
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Intake On";
    }
    public void initialize(){
        intake = new Ballpath();
        flywheel = new Shooter();
    }
}