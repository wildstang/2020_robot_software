package org.wildstang.framework.io.outputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class I2COutput extends AbstractOutput {
    private static Logger s_log = Logger.getLogger(I2COutput.class.getName());
    private static final String s_className = "I2COutput";

    private byte[] m_value;

    public I2COutput(String p_name) {
        super(p_name);
    }

    public byte[] getValue() {
        return m_value;
    }

    public void setValue(byte[] p_value) {
        m_value = p_value;
    }

    @Override
    protected void logCurrentState() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "logCurrentState");
        }

        // getStateTracker().addState(getName(), getName(), getValue());

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "logCurrentState");
        }
    }

}
