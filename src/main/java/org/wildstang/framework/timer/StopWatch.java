package org.wildstang.framework.timer;

public class StopWatch {
    private double totalTimeInSec;
    private long startTimeInNanoSec;
    private long totalTimeInNanoSec;
    private boolean timerRunning;

    public StopWatch() {
        // Constructor/Initialization Method
        this.Reset();
    }

    public void Reset() {
        startTimeInNanoSec = 0;
        totalTimeInNanoSec = 0;
        totalTimeInSec = 0.0;
        timerRunning = false;
    }

    public void Start() {
        startTimeInNanoSec = System.nanoTime();
        timerRunning = true;
    }

    public void Stop() {
        if (timerRunning == true) {
            long currentTimeInNanoSec = System.nanoTime();
            long updateTimeInNanoSec;

            updateTimeInNanoSec = currentTimeInNanoSec - startTimeInNanoSec;

            totalTimeInNanoSec += updateTimeInNanoSec;

            startTimeInNanoSec = currentTimeInNanoSec;

            timerRunning = false;
        }
    }

    public double GetTimeInSec() {
        if (timerRunning == true) {
            long currentTimeInNanoSec = System.nanoTime();
            long updateTimeInNanoSec;

            updateTimeInNanoSec = currentTimeInNanoSec - startTimeInNanoSec;

            totalTimeInNanoSec += updateTimeInNanoSec;

            startTimeInNanoSec = currentTimeInNanoSec;
        }

        totalTimeInSec = totalTimeInNanoSec / 1000000000.0;

        return totalTimeInSec;
    }
}
