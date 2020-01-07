/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.framework.auto.steps.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.timer.WsTimer;

/**
 *
 * @author coder65535
 */
public class AutoStepDelay
        extends AutoStep /*
                          * This step delays testing for the specified number of cycles. Note: If
                          * used in a parallel step group, it insures that the group waits for at
                          * least the specified number of cycles, instead.
                          */ {
    private static Logger s_log = Logger.getLogger(AutoStepDelay.class.getName());

    protected final double delay;
    protected WsTimer timer;

    public AutoStepDelay(int msDelay) {
        this.delay = msDelay / 1000.0;
        this.timer = new WsTimer();
        if (msDelay < 0) {
            s_log.logp(Level.FINE, this.getClass().getName(), "AutonomousStepDelay",
                    "Delay must be greater than 0");
        }
    }

    @Override
    public void initialize() {
        timer.reset();
        timer.start();
        System.out.println("Timer in init " + timer.get());
    }

    @Override
    public void update() {
        if (timer.hasPeriodPassed(delay)) {
            timer.stop();
            setFinished(true);
            System.out.println("Timer at finished " + timer.get());
        }
    }

    @Override
    public String toString() {
        return "Delay for " + (delay * 1000) + "  ms";
    }
}
