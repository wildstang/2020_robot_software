package org.wildstang.framework.auto.steps;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author coder65535
 */
public class AutoSerialStepGroup extends AutoStep {
    // Serial groups execute all contained steps sequentially

    final List<AutoStep> steps = new ArrayList<>();
    int currentStep = 0;
    boolean initialized = false;
    String name = "";
    private boolean finishedPreviousStep;

    public AutoSerialStepGroup() {
        name = "";
    }

    public AutoSerialStepGroup(String name) {
        this.name = name;
    }

    @Override
    public void initialize() {
        finishedPreviousStep = false;
        currentStep = 0;
        if (!steps.isEmpty()) {
            steps.get(currentStep).initialize();
            System.out.println("Starting step " + steps.get(currentStep).toString());
        }
        initialized = true;
    }

    @Override
    public void update() {
        if (isFinished()) {
            return;
        }
        if (finishedPreviousStep) {
            finishedPreviousStep = false;
            currentStep++;
            if (currentStep >= steps.size()) {
                // We have reached the end of our list of steps, we're finished
                setFinished(true);
                return;
            } else {
                steps.get(currentStep).initialize();
                System.out.println("Starting step " + steps.get(currentStep).toString());
            }
        }
        AutoStep step = steps.get(currentStep);
        step.update();
        if (step.isFinished()) {
            finishedPreviousStep = true;
        }
    }

    public void addStep(AutoStep step) {
        if (!initialized) {
            steps.add(step);
        }
    }

    @Override
    public String toString() {
        return "Serial step group: " + name;
    }

    public AutoStep getCurrentStep() {
        return steps.get(currentStep);
    }

    public AutoStep getNextStep() {
        if (currentStep + 1 < steps.size()) {
            return steps.get(currentStep + 1);
        } else {
            return null;
        }
    }

    public void finishGroup() {
        setFinished(true);
    }
}
