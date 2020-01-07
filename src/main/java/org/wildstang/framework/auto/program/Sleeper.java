/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.framework.auto.program;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepStopAutonomous;

/**
 *
 * @author coder65535
 */
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
public class Sleeper extends AutoProgram {

    @Override
    public void defineSteps() {
        addStep(new AutoStepStopAutonomous());
    }

    @Override
    public String toString() {
        return "Sleeper";
    }
}
