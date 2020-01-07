package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.timer.WsTimer;


//this example is for Drive, you can also modify it to use ballpath, climbwedge, hatch, lift, strafeaxis

public class DelayStep extends AutoStep{

    WsTimer timer = new WsTimer();
    double delay;

    public DelayStep(double delay) {
        this.delay = delay;
    }

    public void update() {
        if (timer.get() > delay) {
            setFinished(true);
        }
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "DelayStep";
    }
    public void initialize(){
        timer.start();
    }


}