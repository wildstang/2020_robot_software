package org.wildstang.year2020.robot;

public final class CANConstants {
    public static final int[] LEFT_DRIVE_TALONS = {2, 3};
    public static final int[] RIGHT_DRIVE_TALONS = {4, 13};

    //For FalconDrive
    public static final int LEFT_DRIVE_TALON_FOLLOWER = 1;
    public static final int RIGHT_DRIVE_TALON_FOLLOWER = 4;
    //For normal drive (2 talons, 4 victors)
    public static final int LEFT_DRIVE_VICTOR = 1;
    public static final int RIGHT_DRIVE_VICTOR = 5;

    // TODO put in correct IDs
    public static final int STRAFE_TALON = 12;
    public static final int INTAKE_VICTOR = 13;
    public static final int CARRIAGE_VICTOR = 14;
    public static final int HOPPER_VICTOR1 = 15;
    public static final int HOPPER_VICTOR2 = 11;
    public static final int LIFT_TALON = 10;
    public static final int LIFT_VICTOR = 9;
}
