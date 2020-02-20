package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;

public class IntakeOnStep extends AutoStep{

    private Ballpath intake;

    public void update() {
        intake.turnOnIntake();
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Intake On";
    }
    public void initialize(){
        intake = new Ballpath();
    }
}