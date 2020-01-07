package org.wildstang.framework.core;

import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.io.outputs.OutputType;

public interface Outputs {

    public String getName();

    public OutputType getType();

    public OutputConfig getConfig();

    public boolean isTrackingState();
}