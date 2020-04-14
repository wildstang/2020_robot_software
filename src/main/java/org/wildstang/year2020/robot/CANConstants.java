package org.wildstang.year2020.robot;



public final class CANConstants {
    public static final int LEFT_DRIVE_TALON = 31;
    public static final int RIGHT_DRIVE_TALON = 33;

    //For FalconDrive
    public static final int[] LEFT_DRIVE_TALON_FOLLOWER = {32};
    public static final int[] RIGHT_DRIVE_TALON_FOLLOWER = {34};
    //For normal drive (2 talons, 4 victors)
    public static final int[] LEFT_DRIVE_VICTORS = {3,4};
    public static final int[] RIGHT_DRIVE_VICTORS = {5,6};

    // TODO put in correct IDs
    public static final int BALLPATH_FEED = 9;
    public static final int BALLPATH_KICKER = 10;
    public static final int BALLPATH_INTAKE = 11;
    public static final int CLIMB_VICTOR_1 = 7;
    public static final int CLIMB_VICTOR_2 = 8;
    public static final int TURRET_TALON = 12;
    public static final int LAUNCHER_TALON = 5;
    public static final int LAUNCHER_VICTOR = 6;
    public static final int HOOD_MOTOR = 13;
    public static final int BALLPATH_HOPPER = 14;
    public static final int CPDEPLOY_TALON = 15;

    public static final int LeftWheel_Spin_Top = 1;
    public static final int RightWheel_Spin_Top = 2;
    public static final int LeftWheel_Spin_Bottom = 3;
    public static final int RightWheel_Spin_Bottom = 4;
    public static final int LeftWheel_Rotate_Top = 5;
    public static final int RightWheel_Rotate_Top = 21;
    public static final int LeftWheel_Rotate_Bottom = 7;
    public static final int RightWheel_Rotate_Bottom = 17;
    
    
}
