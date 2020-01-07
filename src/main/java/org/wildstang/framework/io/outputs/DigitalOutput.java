package org.wildstang.framework.io.outputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DigitalOutput extends AbstractOutput {

    private static Logger s_log = Logger.getLogger(DigitalOutput.class.getName());
    private static final String s_className = "DigitalOutput";

    private boolean m_value;

    public DigitalOutput(String p_name) {
        super(p_name);
    }

    public DigitalOutput(String p_name, boolean p_default) {
        super(p_name);
        m_value = p_default;
    }

    public boolean getValue() {
        return m_value;
    }

    public void setValue(boolean p_value) {
        m_value = p_value;
    }

    @Override
    protected void logCurrentState() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "logCurrentState");
        }

        getStateTracker().addState(getName(), getName(), getValue());

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "logCurrentState");
        }
    }

}
