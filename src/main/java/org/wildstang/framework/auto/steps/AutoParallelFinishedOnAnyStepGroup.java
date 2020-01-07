/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.framework.auto.steps;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joey
 */
public class AutoParallelFinishedOnAnyStepGroup extends AutoStep {

    private String name;
    private boolean initialized = false;
    private final List<AutoStep> steps = new ArrayList<>();

    public AutoParallelFinishedOnAnyStepGroup() {
        name = "";
    }

    public AutoParallelFinishedOnAnyStepGroup(String name) {
        this.name = name;
    }

    @Override
    public void initialize() {
        initialized = true;
        for (AutoStep step : steps) {
            step.initialize();
        }
    }

    @Override
    public void update() {
        for (AutoStep step : steps) {
            step.update();
            if (step.isFinished()) {
                steps.clear();
                break;
            }
        }
        if (steps.isEmpty()) {
            setFinished(true);
        }
    }

    public void addStep(AutoStep step) {
        if (!initialized) {
            steps.add(step);
        }
    }

    @Override
    public String toString() {
        return "Parallel finished on any step group: " + name;
    }
}
