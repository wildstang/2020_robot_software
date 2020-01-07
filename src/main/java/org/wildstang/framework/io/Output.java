package org.wildstang.framework.io;

import org.wildstang.framework.logger.StateTracker;

public interface Output {

    public void update();

    /**
     * Returns the name of the output.
     *
     * @return the name of the output
     */
    public String getName();

    public void setStateTracker(StateTracker p_tracker);

    public void enable();

    public void disable();

    public boolean isEnabled();
}
