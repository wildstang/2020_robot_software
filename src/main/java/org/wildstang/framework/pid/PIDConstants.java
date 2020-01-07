package org.wildstang.framework.pid;

/** A tuple of F, P, I, D constants */
public class PIDConstants {
    public final double f;
    public final double p;
    public final double i;
    public final double d;

    public PIDConstants(double f, double p, double i, double d) {
        this.f = f;
        this.p = p;
        this.i = i;
        this.d = d;
    }
}
