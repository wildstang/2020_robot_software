package org.wildstang.framework.io.inputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NullAnalogInput extends AnalogInput {

    private static Logger s_log = Logger.getLogger(NullAnalogInput.class.getName());
    private static final String s_className = "NullAnalogInput";

    public NullAnalogInput(String p_name) {
        super(p_name);
    }

    public NullAnalogInput(String p_name, double p_default) {
        super(p_name, p_default);
    }

    @Override
    protected double readRawValue() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "readRawValue");
        }
        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "readRawValue");
        }

        return 0;

    }

}
