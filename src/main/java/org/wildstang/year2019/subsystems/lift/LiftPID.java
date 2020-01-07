package org.wildstang.year2019.subsystems.lift;

import org.wildstang.framework.pid.PIDConstants;

/** Config our pid constants here.
 * 
 * See https://phoenix-documentation.readthedocs.io/en/latest/ch16_ClosedLoop.html for
 * more info on what these numbers mean. */
public enum LiftPID {
    // FIXME: Document the units each of these FPID constants are in.
    // Constants in order F, P, I, D
    HOMING(0, new PIDConstants(0, 0.2, 0.0, 7.0)),
    TRACKING(1, new PIDConstants(0, 7.0, 0.0, 0.0)),
    DOWNTRACK(2, new PIDConstants(0.0,0.05,0.0,7.0));
    
    // only four slots are available on the Talon

    public final PIDConstants k;
    // Todo: move slot to PIDConstants and change this to subclass PIDConstants
    public final int slot;

    LiftPID(int slot, PIDConstants pid) {
        this.slot = slot;
        this.k = pid;
    }
}