package org.wildstang.framework.io.outputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NullDigitalOutput extends DigitalOutput {

    private static Logger s_log = Logger.getLogger(NullDigitalOutput.class.getName());
    private static final String s_className = "NullDigitalOutput";

    public NullDigitalOutput(String p_name) {
        super(p_name);
    }

    public NullDigitalOutput(String p_name, boolean p_default) {
        super(p_name, p_default);
    }

    @Override
    protected void sendDataToOutput() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "sendDataToOutput");
        }

        // Do nothing

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "sendDataToOutput");
        }
    }

}
