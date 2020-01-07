package org.wildstang.hardware.crio.inputs;

import org.wildstang.framework.io.inputs.DiscreteInput;

import edu.wpi.first.wpilibj.I2C.Port;

public class WsLIDAR extends DiscreteInput {

    private LidarSensor lidar;

    public WsLIDAR(String name, Port port, int p_address) {
        super(name);

        lidar = new LidarSensor(port, p_address);
        lidar.start();
    }

    @Override
    protected int readRawValue() {
        return lidar.getSmoothedDistance();
    }
}
