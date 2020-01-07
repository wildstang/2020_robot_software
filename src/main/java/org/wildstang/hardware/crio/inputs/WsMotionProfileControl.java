package org.wildstang.hardware.crio.inputs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.io.inputs.DigitalInput;

public class WsMotionProfileControl extends DigitalInput {
    private static Logger s_log = Logger.getLogger(DigitalInput.class.getName());
    private static final String s_className = "WsMotionProfileInput";
    private boolean profileEnabled = false;
    private boolean resetKinematics = false;

    public WsMotionProfileControl(String p_name) {
        super(p_name);
        profileEnabled = false;
        resetKinematics = false;
    }

    @Override
    protected boolean readRawValue() {
        /* Nothing to do here. */
        return false;
    }

    @Override
    public void readDataFromInput() {
        /* Nothing to read here. */
    }

    public void setProfileEnabled(boolean p_newValue) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "setProfileEnabled");
        }

        profileEnabled = p_newValue;

        logCurrentState();

        notifyListeners();

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "setProfileEnabled");
        }
    }

    public void setResetKinematics(boolean p_newValue) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "setResetKinematics");
        }

        resetKinematics = p_newValue;

        logCurrentState();

        notifyListeners();

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "setResetKinematics");
        }
    }

    public boolean getProfileEnabled() {
        return profileEnabled;
    }

    public boolean getResetKinematics() {
        return resetKinematics;
    }
}
