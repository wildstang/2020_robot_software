package org.wildstang.framework.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.auto.steps.control.AutoStepStopAutonomous;
import org.wildstang.framework.core.Core;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Nathan
 */
public abstract class AutoProgram {

    private static Logger s_log = Logger.getLogger(AutoProgram.class.getName());

    protected final List<AutoStep> programSteps = new ArrayList<>();
    protected int currentStep;
    protected boolean finishedPreviousStep, finished;

    protected abstract void defineSteps(); // Use this method to set the steps
    // for this program. Programs
    // execute the steps in the array
    // programSteps serially.
    // Remember to clear everything before all of your steps are finished,
    // because once they are, it immediately drops into Sleeper.

    public void initialize() {
        defineSteps();
        loadStopPosition();
        currentStep = 0;
        finishedPreviousStep = false;
        finished = false;
        programSteps.get(0).initialize();
        s_log.logp(Level.FINE, "Auton", "Step Starting", programSteps.get(0).toString());
    }

    public void cleanup() {
        programSteps.clear();
    }

    public void update() {
        if (finished) {
            return;
        }
        if (finishedPreviousStep) {
            finishedPreviousStep = false;
            currentStep++;
            if (currentStep >= programSteps.size()) {
                finished = true;
                return;
            } else {
                s_log.logp(Level.FINE, "Auton", "Step Start",
                        programSteps.get(currentStep).toString());
                programSteps.get(currentStep).initialize();
            }
        }
        AutoStep step = programSteps.get(currentStep); // Prevent errors caused by mistyping.
        SmartDashboard.putString("Current auto step", step.toString());
        step.update();
        if (step.isFinished()) {
            s_log.logp(Level.FINE, "Auton", "Step Finished", step.toString());
            finishedPreviousStep = true;
        }
    }

    public AutoStep getCurrentStep() {
        return programSteps.get(currentStep);
    }

    public AutoStep getNextStep() {
        if (currentStep + 1 < programSteps.size()) {
            return programSteps.get(currentStep + 1);
        } else {
            return null;
        }
    }

    protected void loadStopPosition() {
        int forceStopAtStep = Core.getConfigManager().getConfig()
                .getInt(this.getClass().getName() + ".ForceStopAtStep", 0);
        if (forceStopAtStep != 0) {
            int forceStop = forceStopAtStep;
            if ((forceStop <= programSteps.size()) && (forceStop > 0)) {
                programSteps.set(forceStop, new AutoStepStopAutonomous());
                s_log.logp(Level.ALL, "Auton", "Force Stop",
                        "Program is forced to stop at Step " + forceStop);
            } else {
                s_log.logp(Level.SEVERE, "Auton", "Force Stop",
                        "Force stop value is outside of bounds. (0 to "
                                + (programSteps.size() - 1));
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    protected void addStep(AutoStep newStep) {
        programSteps.add(newStep);
    }

    @Override
    public abstract String toString();
}
