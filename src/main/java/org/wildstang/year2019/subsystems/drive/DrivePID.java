package org.wildstang.year2019.subsystems.drive;

import org.wildstang.framework.pid.PIDConstants;

/** Config our pid constants here.
 * 
 * See https://phoenix-documentation.readthedocs.io/en/latest/ch16_ClosedLoop.html for
 * more info on what these numbers mean. */
public enum DrivePID {
    // FIXME: Document the units each of these FPID constants are in.
    // Constants in order F, P, I, D
    PATH(0, new PIDConstants(0.6, 1.0, 0.0, 0.0)),
    BASE_LOCK(1, new PIDConstants(0.0, .8, 0.001, 10)),
    MM_QUICK(2, new PIDConstants(0.55, .8, 0.001, 10)),
    MM_DRIVE(3, new PIDConstants(0.0, .2, 0.001, 2));
    // only four slots are available on the Talon

    public final PIDConstants k;
    // Todo: move slot to PIDConstants and change this to subclass PIDConstants
    public final int slot;

    DrivePID(int slot, PIDConstants pid) {
        this.slot = slot;
        this.k = pid;
    }
}