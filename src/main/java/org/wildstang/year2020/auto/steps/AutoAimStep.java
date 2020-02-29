package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2020.robot.WSSubsystems;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;
import org.wildstang.year2020.subsystems.launching.Turret;
import org.wildstang.year2020.subsystems.launching.Limelight;
import org.wildstang.year2020.subsystems.launching.Shooter;

public class AutoAimStep extends AutoStep{

    private Shooter shooter;
    private Turret turret;
    private boolean activity;
    private Limelight limelight;

    public AutoAimStep(boolean active){
        this.activity = active;
    }

    public void update() {
        shooter.setAim(activity);
        //turret.autoAim(activity);
        if (activity){
            limelight.enableLEDs();
        } else {
            limelight.disableLEDs();
        }
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "AutoAim turret and shooter";
    }
    public void initialize(){
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName());
        //turret = (Turret) Core.getSubsystemManager().getSubsystem(WSSubsystems.TURRET.getName());
        limelight = (Limelight) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIMELIGHT.getName());
    }
}