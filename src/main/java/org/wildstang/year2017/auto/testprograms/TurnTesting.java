package org.wildstang.year2017.auto.testprograms;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.year2017.auto.steps.TurnByNDegreesStep;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnTesting extends AutoProgram {
    public TurnTesting() {
        // SmartDashboard.putNumber("Test Turn Angle", 0);
    }

    @Override
    protected void defineSteps() {
        // addStep(new PathFollowerStep(PathNameConstants.GEAR_AUTO_FORWARD));
        // addStep(new AutoStepDelay(200));
        addStep(new TurnByNDegreesStep(60, 0.38));
        // addStep(new AutoStepDelay(200));
        // addStep(new TrackVisionToGearStep());
    }

    @Override
    public String toString() {
        return "Turn Tester";
    }

}
