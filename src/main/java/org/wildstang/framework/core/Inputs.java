package org.wildstang.framework.core;

import org.wildstang.framework.hardware.InputConfig;
import org.wildstang.framework.io.inputs.InputType;

/* FIXME rename all this */
public interface Inputs {

    public String getName();

    public InputType getType();

    public InputConfig getConfig();

    public boolean isTrackingState();
}