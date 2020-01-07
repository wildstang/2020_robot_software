package org.wildstang.framework.io.inputs;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AnalogInput extends AbstractInput {

    private static Logger s_log = Logger.getLogger(AnalogInput.class.getName());
    private static final String s_className = "AnalogInput";

    private static final DecimalFormat s_format = new DecimalFormat("#.###");

    private double m_currentValue = 0.0d;

    public AnalogInput(String p_name) {
        super(p_name);
    }

    public AnalogInput(String p_name, double p_default) {
        super(p_name);
        m_currentValue = p_default;
    }

    @Override
    protected void readDataFromInput() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "readDataFromInput");
        }

        double newValue = readRawValue();

        setNewValue(newValue);

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "readDataFromInput");
        }
    }

    public void setValue(double p_newValue) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "setValue");
        }

        setNewValue(p_newValue);

        logCurrentState();

        notifyListeners();

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "setValue");
        }
    }

    private void setNewValue(double p_newValue) {
        // Only update if the value has changed
        // NOTE: For analog inputs, it is possible to change often due to noise
        // or sensitive sensors. May want to implement a tolerance/sensitivity
        // on value changes
        if (s_log.isLoggable(Level.FINEST)) {
            s_log.finest("Current value = " + m_currentValue + " : New value = " + p_newValue);
        }

        if (p_newValue != m_currentValue) {
            m_currentValue = p_newValue;
            setValueChanged(true);
        } else {
            setValueChanged(false);
        }
    }

    /**
     * This method reads the raw value from the underlying hardware. This should be
     * implemented by each individual input subclass.
     *
     * @return
     */
    protected abstract double readRawValue();

    public double getValue() {
        return m_currentValue;
    }

    @Override
    protected void logCurrentStateInternal() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "logCurrentState");
        }

        getStateTracker().addState(getName(), getName(), s_format.format(getValue()));
        // getStateTracker().addState(getName(), getParent() == null ? getName() :
        // getParent().getName(), getValue());

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "logCurrentState");
        }
    }

}
