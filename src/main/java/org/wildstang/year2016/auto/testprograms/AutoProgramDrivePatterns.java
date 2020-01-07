/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.year2016.auto.testprograms;

import org.wildstang.framework.auto.AutoManager;
import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.auto.steps.drivebase.StepQuickTurn;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;

/**
 *
 * @author Joey
 */
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
public class AutoProgramDrivePatterns extends AutoProgram {

    private double firstAngle, secondAngle, firstDriveDistance, firstDriveVelocity,
            secondDriveDistance, secondDriveVelocity;

    @Override
    public void defineSteps() {
        firstAngle = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + "."
                        + AutoManager.getInstance().getStartPosition().toConfigString()
                        + ".FirstRelativeAngle", 45);
        secondAngle = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + "."
                        + AutoManager.getInstance().getStartPosition().toConfigString()
                        + ".SecondRelativeAngle", 45);
        firstDriveDistance = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + "."
                        + AutoManager.getInstance().getStartPosition().toConfigString()
                        + ".FirstDriveDistance", -100);
        firstDriveVelocity = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + "."
                        + AutoManager.getInstance().getStartPosition().toConfigString()
                        + ".FirstDriveVelocity", 0.0);
        secondDriveDistance = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + "."
                        + AutoManager.getInstance().getStartPosition().toConfigString()
                        + ".SecondDriveDistance", -30);
        secondDriveVelocity = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + "."
                        + AutoManager.getInstance().getStartPosition().toConfigString()
                        + ".SecondDriveVelocity", 0.0);

        addStep(new StepStartDriveUsingMotionProfile(firstDriveDistance, firstDriveVelocity));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        addStep(new StepQuickTurn(firstAngle));
        addStep(new StepStartDriveUsingMotionProfile(secondDriveDistance, secondDriveVelocity));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());
        addStep(new StepQuickTurn(secondAngle));
    }

    @Override
    public String toString() {
        return "Testing drive patterns for after shoot 5";
    }
}
