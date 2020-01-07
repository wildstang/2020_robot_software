/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.framework.auto.steps.control;

import org.wildstang.framework.auto.steps.AutoStep;

/**
 *
 * @author coder65535
 */
public class AutoStepStopAutonomous extends AutoStep {

    public AutoStepStopAutonomous() {
        // Do nothing. This step does nothing, and never finishes, effectively
        // halting autonomous operations.
        // Note: If included in a parallel step group, it only halts operations
        // after all other steps in the group finish.
    }

    @Override
    public void initialize() {
        // Do nothing.
    }

    @Override
    public void update() {
        // Do nothing.
    }

    @Override
    public String toString() {
        return "Stop auto-op";
    }
}
