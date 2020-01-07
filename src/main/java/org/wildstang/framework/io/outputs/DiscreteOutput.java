package org.wildstang.framework.io.outputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DiscreteOutput extends AbstractOutput {

    private static Logger s_log = Logger.getLogger(DiscreteOutput.class.getName());
    private static final String s_className = "DiscreteOutput";

    private int m_value;

    public DiscreteOutput(String p_name) {
        super(p_name);
    }

    public DiscreteOutput(String p_name, int p_default) {
        super(p_name);
        m_value = p_default;
    }

    public int getValue() {
        return m_value;
    }

    public void setValue(int p_value) {
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
