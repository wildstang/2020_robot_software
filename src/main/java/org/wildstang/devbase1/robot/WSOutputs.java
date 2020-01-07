package org.wildstang.devbase1.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.io.outputs.OutputType;

/**
 * Enumerate all outputs from the robot. Robot.java uses this to set up all
 * outputs.
 */
public enum WSOutputs implements Outputs {
    ;

    private String name;
    private OutputType type;
    private OutputConfig config;
    private boolean trackingState;

    WSOutputs(String name, OutputType type, OutputConfig config, boolean trackingState) {
        this.name = name;
        this.type = type;
        this.config = config;
        this.trackingState = trackingState;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OutputType getType() {
        return type;
    }

    @Override
    public OutputConfig getConfig() {
        return config;
    }

    @Override
    public boolean isTrackingState() {
        return trackingState;
    }
}
