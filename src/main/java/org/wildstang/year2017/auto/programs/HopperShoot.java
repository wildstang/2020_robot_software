package org.wildstang.year2017.auto.programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.auto.steps.PathFollowerStep;
import org.wildstang.year2017.auto.steps.SetBrakeModeStep;
import org.wildstang.year2017.auto.steps.SetHighGearStep;
import org.wildstang.year2017.auto.steps.ShootStep;
import org.wildstang.year2017.auto.steps.ShooterOnAndReady;
import org.wildstang.year2017.auto.steps.StopShooting;

public class HopperShoot extends AutoProgram {

    private int hopperWaitTime;
    private int delayWhileShooting;

    @Override
    public void initialize() {
        super.initialize();

        // Read config values
        // 10000 = ten seconds
        hopperWaitTime = Core.getConfigManager().getConfig()
                .getInt(this.getClass().getName() + ".hopperWaitTime", 3000);
        delayWhileShooting = Core.getConfigManager().getConfig()
                .getInt(this.getClass().getName() + ".delayWhileShooting", 5000);
    }

    @Override
    protected void defineSteps() {
        // Set high gear state
        addStep(new SetHighGearStep(true));
        addStep(new SetBrakeModeStep(true));

        // Drive from the wall to the hopper
        addStep(new PathFollowerStep(PathNameConstants.WALL_TO_HOPPER));

        addStep(new AutoStepDelay(hopperWaitTime));

        // Backup from the hopper
        addStep(new PathFollowerStep(PathNameConstants.BACKUP_FROM_HOPPER));

        // Turn on feed and wait for balls

        addStep(new PathFollowerStep(PathNameConstants.HOPPER_TO_BOILER));

        // Turn on shooter and shoot
        addStep(new ShooterOnAndReady());
        addStep(new ShootStep());

        addStep(new AutoStepDelay(delayWhileShooting));
        addStep(new StopShooting());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Hopper Shoot";
    }

}
