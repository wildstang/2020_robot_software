/* An autostep to follow a target line into the hatch. */

package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.timer.WsTimer;

/** The idea here is to drive forward, keeping the white line centered under the line sensor, until we stall out against the wall. That will mean we're squarely lined up for hatching. */
public class LineFollowStep {

    /** The length of time we need to spend stalled against the wall before stopping. */
    private static double STALL_TIMEOUT = 0.5;

    WsTimer timer = new WsTimer();
    double delay;
    boolean isStalled;

    public LineFollowStep() {
    }

    public void update() {
    }

    public String toString(){
        //put a reasonable name for this step inside the string
        return "DelayStep";
    }

    public void initialize(){
        timer.start();
    }
}