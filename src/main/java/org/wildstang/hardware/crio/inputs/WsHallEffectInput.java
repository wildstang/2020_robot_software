package org.wildstang.hardware.crio.inputs;

import java.util.TimerTask;

import org.wildstang.framework.io.inputs.DiscreteInput;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class WsHallEffectInput extends DiscreteInput {
    private static final int defaultUpdateInterval = 20;

    I2C i2c;

    private int lastHallEffectSensor = -1;
    private int selectedHallEffectSensor = -1;

    private java.util.Timer updater;

    private Object lock = new Object();

    public WsHallEffectInput(String name, Port port, int address) {
        super(name);

        i2c = new I2C(port, address);

        updater = new java.util.Timer();

        // Update at 50Hz
        start(defaultUpdateInterval);
    }

    @Override
    protected int readRawValue() {
        // The selected sensor is set by the background thread update. Simply return the
        // last value we know of
        return selectedHallEffectSensor;
    }

    // Update the currently activated hall effect sensor
    private void updateSensor() {
        byte[] buffer = new byte[1];
        i2c.readOnly(buffer, 1);
        try {
            synchronized (lock) {
                selectedHallEffectSensor = buffer[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (selectedHallEffectSensor != lastHallEffectSensor) {
            System.out.println("READ HALL EFFECT: " + selectedHallEffectSensor);
        }
        lastHallEffectSensor = selectedHallEffectSensor;
    }

    // Start 10Hz polling
    /*
     * public void start() { updater.scheduleAtFixedRate(new HallEffectUpdater(), 0,
     * 100); }
     */

    // Start polling for period in milliseconds
    public void start(int period) {
        updater.scheduleAtFixedRate(new HallEffectUpdater(), 0, period);
    }

    // Timer task to keep distance updated
    private class HallEffectUpdater extends TimerTask {
        @Override
        public void run() {
            updateSensor();
        }
    }

}
