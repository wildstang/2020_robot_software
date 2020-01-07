package org.wildstang.framework.io.outputs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.io.Output;
import org.wildstang.framework.logger.StateTracker;

public abstract class AbstractOutput implements Output {

    private static Logger s_log = Logger.getLogger(AbstractOutput.class.getName());
    private static final String s_className = "AbstractOutput";

    private String m_name = "Undefined";
    private StateTracker m_stateTracker;
    private boolean m_enabled = true;

    public AbstractOutput(String p_name) {
        CoreUtils.checkNotNull(p_name, "p_name is null");

        m_name = p_name;
    }

    @Override
    public void update() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "update");
        }

        // Log the output current state
        if (m_stateTracker != null) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Logging current state");
            }
            logCurrentState();
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.finer("Sending data to output");
        }
        sendDataToOutput();

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "update");
        }
    }

    @Override
    public String getName() {
        return m_name;
    }

    protected abstract void logCurrentState();

    protected abstract void sendDataToOutput();

    @Override
    public int hashCode() {
        // TODO
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object p_obj) {
        // TODO
        return super.equals(p_obj);
    }

    @Override
    public void setStateTracker(StateTracker p_tracker) {
        m_stateTracker = p_tracker;
    }

    public StateTracker getStateTracker() {
        return m_stateTracker;
    }

    @Override
    public void enable() {
        m_enabled = true;
    }

    @Override
    public void disable() {
        m_enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return m_enabled;
    }

}
