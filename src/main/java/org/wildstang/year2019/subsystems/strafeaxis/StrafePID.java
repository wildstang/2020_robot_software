package org.wildstang.year2019.subsystems.strafeaxis;

import org.wildstang.framework.pid.PIDConstants;

/** Config our pid constants here.
 * 
 * See https://phoenix-documentation.readthedocs.io/en/latest/ch16_ClosedLoop.html for
 * more info on what these numbers mean. */
public enum StrafePID {
    // FIXME: Document the units each of these FPID constants are in.
    // Constants in order F, P, I, D
    HOMING(0, new PIDConstants(0, .1, 0, 0.001)),
    TRACKING(1, new PIDConstants(0, .1, 0, 0.001));
    // only four slots are available on the Talon

    public final PIDConstants k;
    // Todo: move slot to PIDConstants and change this to subclass PIDConstants
    public final int slot;

    StrafePID(int slot, PIDConstants pid) {
        this.slot = slot;
        this.k = pid;
    }
}