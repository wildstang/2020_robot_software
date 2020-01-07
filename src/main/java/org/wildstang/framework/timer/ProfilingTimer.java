package org.wildstang.framework.timer;

import edu.wpi.first.wpilibj.Timer;

public class ProfilingTimer {

    double startingTime;
    double endingTime;
    int iterations;
    String name;

    public ProfilingTimer(String name, int iterations) {
        this.iterations = iterations;
        this.name = name;
    }

    public void startTimingSection() {
        startingTime = Timer.getFPGATimestamp();
    }

    public double endTimingSection() {
        double spentTime = 0;
        endingTime = Timer.getFPGATimestamp();
        spentTime = endingTime - startingTime;
        return spentTime;
    }
}