package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;
import org.wildstang.year2020.subsystems.launching.Turret;
import org.wildstang.year2020.subsystems.launching.Shooter;

public class SetHoodStep extends AutoStep{

    private Shooter shooter;
    private double newTarget;

    public SetHoodStep(double target){
        this.newTarget = target;
    }

    public void update() {
        shooter.setHoodMotorPosition(newTarget);
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Set Hood position";
    }
    public void initialize(){
        shooter = new Shooter();
    }
}