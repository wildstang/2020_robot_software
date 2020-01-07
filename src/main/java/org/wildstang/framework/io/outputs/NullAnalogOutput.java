package org.wildstang.framework.io.outputs;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NullAnalogOutput extends AnalogOutput {

    private static Logger s_log = Logger.getLogger(NullAnalogOutput.class.getName());
    private static final String s_className = "NullAnalogOutput";

    public NullAnalogOutput(String p_name) {
        super(p_name);
    }

    public NullAnalogOutput(String p_name, double p_default) {
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
