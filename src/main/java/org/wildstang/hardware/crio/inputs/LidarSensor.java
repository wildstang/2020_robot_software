package org.wildstang.hardware.crio.inputs;

import java.util.TimerTask;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.Timer;

public class LidarSensor {
    /* Code to write to config register to begin measuring */
    private static final int INITIATE_MEASUREMENT = 0x04;
    /* Delay to wait for measurement data to come in */
    private static final double MEASUREMENT_DELAY = 0.04;
    /* Delay to wait between measurements to avoid over-polling the LIDAR */
    private static final double INTER_MEASUREMENT_DELAY = 0.005;
    /* Number of samples to keep */
    private static final int DISTANCE_HISTORY_LENGTH = 6;

    private static final int LIDAR_CONFIG_REGISTER = 0x00;
    private static final int LIDAR_DISTANCE_REGISTER = 0x8f;

    private I2C i2c;
    private byte[] distance;
    private java.util.Timer updater;
    private Integer[] recordedDistances = new Integer[DISTANCE_HISTORY_LENGTH];

    private final int LIDAR_ADDR;

    public LidarSensor(Port port, int address) {
        LIDAR_ADDR = address;
        i2c = new I2C(port, LIDAR_ADDR);

        distance = new byte[2];

        updater = new java.util.Timer();
    }

    // Distance in cm
    public int getDistance() {
        return 0; // (int) Integer.toUnsignedLong(distance[0] << 8) +
                  // Byte.toUnsignedInt(distance[1]);
    }

    // Returns average of the last 5 readings
    public int getSmoothedDistance() {
        int accumulator = 0;
        int numValidElements = 0;
        for (int i = 0; i < recordedDistances.length; i++) {
            if (recordedDistances[i] != null) {
                accumulator += recordedDistances[i].intValue();
                numValidElements++;
            }
        }

        // Avoid divide by zero errors
        if (numValidElements > 0) {
            return accumulator / numValidElements;
        } else {
            return 0;
        }
    }

    public double pidGet() {
        return getDistance();
    }

    // Start 10Hz polling
    public void start() {
        updater.scheduleAtFixedRate(new LIDARUpdater(), 0, 100);
    }

    // Start polling for period in milliseconds
    public void start(int period) {
        updater.scheduleAtFixedRate(new LIDARUpdater(), 0, period);
    }

    public void stop() {
        updater.cancel();
        updater = new java.util.Timer();
    }

    // Update distance variable
    public void update() {
        i2c.write(LIDAR_CONFIG_REGISTER, INITIATE_MEASUREMENT);
        Timer.delay(MEASUREMENT_DELAY); // Delay for measurement to be taken
        i2c.read(LIDAR_DISTANCE_REGISTER, 2, distance); // Read in measurement
        Timer.delay(INTER_MEASUREMENT_DELAY); // Delay to prevent over polling

        // Store most recent results in a sliding window
        for (int i = 0; i < recordedDistances.length; i++) {
            if (i < recordedDistances.length - 1) {
                recordedDistances[i] = recordedDistances[i + 1];
            } else {
                recordedDistances[i] = Integer.valueOf(getDistance());
            }
        }
    }

    // Timer task to keep distance updated
    private class LIDARUpdater extends TimerTask {
        @Override
        public void run() {
            while (true) {
                update();
                try {
                    // Why do we sleep here and also delay on line 84? Suspicious... FIXME
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
