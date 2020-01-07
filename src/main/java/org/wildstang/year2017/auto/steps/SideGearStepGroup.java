package org.wildstang.year2017.auto.steps;

import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.config.Config;
import org.wildstang.framework.core.Core;

public class SideGearStepGroup extends AutoSerialStepGroup {

    public SideGearStepGroup(int p_turnAngle, int p_initialDriveDist) {
        Config config = Core.getConfigManager().getConfig();

        int waitTime = config.getInt(this.getClass().getName() + ".deliverWaitTime", 500);

        // Use high gear
        addStep(new SetHighGearStep(true));

        // For this step, turn off brake mode so we can transition smoothly to vision
        addStep(new SetBrakeModeStep(false));
        addStep(new CloseGearHolderStep());

        // Drive forward and turn 60 degrees towards peg
        addStep(new MotionMagicStraightLine(p_initialDriveDist));
        addStep(new AutoStepDelay(200));
        if (p_turnAngle < 0) {
            addStep(new TurnByNDegreesStep(p_turnAngle, 0.3));
        } else {
            addStep(new TurnByNDegreesStep(p_turnAngle, 0.3));
        }
        addStep(new AutoStepDelay(200));
        addStep(new GearBackStep());
        addStep(new MotionMagicStraightLine(36));
        // Track the vision target
        addStep(new AutoStepDelay(500));
        addStep(new TrackVisionToGearStep());

        addStep(new DeliverGearStep());
        // Wait to let it settle
        addStep(new AutoStepDelay(waitTime));

    }
}
