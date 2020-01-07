package org.wildstang.framework.io.outputs;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AnalogOutput extends AbstractOutput {
    private static Logger s_log = Logger.getLogger(AnalogOutput.class.getName());
    private static final String s_className = "AnalogOutput";

    private static final DecimalFormat s_format = new DecimalFormat("#.###");

    private double m_value;

    public AnalogOutput(String p_name) {
        super(p_name);
    }

    public AnalogOutput(String p_name, double p_default) {
        super(p_name);
        m_value = p_default;
    }

    public double getValue() {
        return m_value;
    }

    public void setValue(double p_value) {
        m_value = p_value;
    }

    @Override
    protected void logCurrentState() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "logCurrentState");
        }

        getStateTracker().addState(getName(), getName(), s_format.format(getValue()));

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "logCurrentState");
        }
    }

}
