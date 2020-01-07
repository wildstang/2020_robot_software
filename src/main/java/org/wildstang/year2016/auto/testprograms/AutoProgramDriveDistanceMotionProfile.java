/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.year2016.auto.testprograms;

import org.wildstang.framework.auto.AutoManager;
import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.Core;
import org.wildstang.year2016.auto.steps.drivebase.StepSetShifter;
import org.wildstang.year2016.auto.steps.drivebase.StepStartDriveUsingMotionProfileAndHeading;
import org.wildstang.year2016.auto.steps.drivebase.StepStopDriveUsingMotionProfile;
import org.wildstang.year2016.auto.steps.drivebase.StepWaitForDriveMotionProfile;

/**
 *
 * @author Nathan
 */
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
public class AutoProgramDriveDistanceMotionProfile extends AutoProgram {

    private double distance;
    private double heading;

    @Override
    public void defineSteps() {
        distance = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + "."
                + AutoManager.getInstance().getStartPosition().toConfigString() + ".distance",
                10.0);
        heading = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + "."
                + AutoManager.getInstance().getStartPosition().toConfigString() + ".heading", 0.0);
        addStep(new StepSetShifter(true));
        addStep(new StepStartDriveUsingMotionProfileAndHeading(distance, 0.0, heading));
        addStep(new StepWaitForDriveMotionProfile());
        addStep(new StepStopDriveUsingMotionProfile());

        // programSteps[3] = new AutonomousStepEnableDriveDistancePid();
        // programSteps[4] = new
        // AutonomousStepSetDriveDistancePidSetpoint(distance.getValue());
        // programSteps[5] = new AutonomousStepWaitForDriveDistancePid();
        // programSteps[6] = new
        // AutonomousStepStartDriveUsingMotionProfile(distance.getValue(), 0.0);
        // programSteps[7] = new AutonomousStepWaitForDriveMotionProfile();
        // programSteps[8] = new AutonomousStepStopDriveUsingMotionProfile();
    }

    @Override
    public String toString() {
        return "TEST Motion profile distance";
    }
}
