package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;
import org.wildstang.year2020.subsystems.launching.Turret;
import org.wildstang.year2020.subsystems.launching.Shooter;

public class SetTurretStep extends AutoStep{

    private Turret turret;
    private double newTarget;

    public SetTurretStep(double target){
        this.newTarget = target;
    }

    public void update() {
        turret.setTarget(newTarget);
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Set Turret Rotation";
    }
    public void initialize(){
        turret = new Turret();
    }
}