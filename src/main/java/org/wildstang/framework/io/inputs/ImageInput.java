package org.wildstang.framework.io.inputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ImageInput extends AbstractInput {

    private static Logger s_log = Logger.getLogger(ImageInput.class.getName());
    private static final String s_className = "ImageInput";

    private Object m_currentValue = null;

    public ImageInput(String p_name) {
        super(p_name);
    }

    @Override
    public void readDataFromInput() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "readDataFromInput");
        }

        Object newValue = readRawValue();

        // Only update if the value has changed
        if (s_log.isLoggable(Level.FINEST)) {
            s_log.finest("Current value = " + m_currentValue + " : New value = " + newValue);
        }

        if (!newValue.equals(m_currentValue)) {
            setCurrentValue(newValue);
            setValueChanged(true);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "readDataFromInput");
        }
    }

    protected void setCurrentValue(Object p_value) {
        m_currentValue = p_value;
    }

    protected abstract Object readRawValue();

    public Object getValue() {
        return m_currentValue;
    }

    @Override
    protected void logCurrentStateInternal() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "logCurrentState");
        }

        // TODO What value could be logged here?
        // getStateTracker().addState(getName(), getName(), getValue());

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "logCurrentState");
        }
    }

}
