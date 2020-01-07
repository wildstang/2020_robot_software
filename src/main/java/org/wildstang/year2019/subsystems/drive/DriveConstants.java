package org.wildstang.year2019.subsystems.drive;

public class DriveConstants {
    /** Distance from the centerline of left wheels to centerline of right wheels */
    public static final double WHEELBASE_WIDTH_INCHES = 30;
    /** Diameter of drive wheels */
    public static final double WHEEL_DIAMETER_INCHES = 6;
    /** Number of encoder ticks in one revolution of the wheel */
    public static final double ENCODER_CPR = 4096;
    /** # of ticks in one surface inch of wheel movement */
    public static final double TICKS_PER_INCH = ENCODER_CPR / (WHEEL_DIAMETER_INCHES * Math.PI);
    public static final double TICKS_PER_INCH_MOD = 230.5;

    /** PID deadband in base lock mode */
    public static final int BRAKE_MODE_ALLOWABLE_ERROR = 20;

    /** Speed multiplier during anti-turbo */
    public static final double ANTI_TURBO_FACTOR = 0.5;


    public static final boolean LEFT_DRIVE_INVERTED = true;
    public static final boolean RIGHT_DRIVE_INVERTED = false;

    public static final boolean LEFT_DRIVE_SENSOR_PHASE = true;
    public static final boolean RIGHT_DRIVE_SENSOR_PHASE = true;
}
