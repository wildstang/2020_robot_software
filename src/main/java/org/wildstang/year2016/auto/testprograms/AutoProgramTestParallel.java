/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.year2016.auto.testprograms;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.year2016.auto.steps.drivebase.StepDriveManual;

/**
 *
 * @author coder65535
 */
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
public class AutoProgramTestParallel extends AutoProgram {

    @Override
    public void defineSteps() {
        AutoParallelStepGroup parallelGroup = new AutoParallelStepGroup("Test parallel step group");
        parallelGroup.addStep(new StepDriveManual(StepDriveManual.KEEP_PREVIOUS_STATE, 1.0));
        parallelGroup.addStep(new AutoStepDelay(250));
        parallelGroup.addStep(new StepDriveManual(1.0, StepDriveManual.KEEP_PREVIOUS_STATE));
        addStep(parallelGroup);
        addStep(new StepDriveManual(0.0, 0.0));

    }

    @Override
    public String toString() {
        return "Test Parallel Groups";
    }
}
