/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.framework.auto.steps;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author coder65535
 */
public class AutoParallelStepGroup extends AutoStep {
    // Parallel groups execute all contained steps in the same frame. Be
    // careful!
    // Note: a finished step is immediately removed from the list. update() is
    // not called on any step that finishes.

    final List<AutoStep> steps = new ArrayList<>();
    boolean initialized = false;
    String name = "";

    public AutoParallelStepGroup() {
        name = "";
    }

    public AutoParallelStepGroup(String name) {
        this.name = name;
    }

    @Override
    public void initialize() {
        for (AutoStep step : steps) {
            step.initialize();
        }
        initialized = true;
    }

    @Override
    public void update() {
        List<AutoStep> toRemove = new ArrayList<>();
        for (AutoStep step : steps) {
            step.update();
            if (step.isFinished()) {
                toRemove.add(step);
            }
        }

        for (AutoStep removeStep : toRemove) {
            steps.remove(removeStep);
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
        return "Parallel step group: " + name;
    }
}
