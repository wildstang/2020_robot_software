package org.wildstang.framework.io.inputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NullDigitalInput extends DigitalInput {

    private static Logger s_log = Logger.getLogger(NullDigitalInput.class.getName());
    private static final String s_className = "NullDigitalInput";

    public NullDigitalInput(String p_name) {
        super(p_name);
    }

    public NullDigitalInput(String p_name, boolean p_default) {
        super(p_name, p_default);
    }

    @Override
    protected boolean readRawValue() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "readRawValue");
        }
        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "readRawValue");
        }

        return false;
    }

}
