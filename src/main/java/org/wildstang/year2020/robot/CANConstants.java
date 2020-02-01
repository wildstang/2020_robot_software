package org.wildstang.year2020.robot;
import com.revrobotics.CANEncoder;
import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
package frc.robot;

public final class CANConstants {
    public static final int LEFT_DRIVE_TALON = 1;
    public static final int RIGHT_DRIVE_TALON = 2;

    //For FalconDrive
    public static final int LEFT_DRIVE_TALON_FOLLOWER = 3;
    public static final int RIGHT_DRIVE_TALON_FOLLOWER = 4;
    //For normal drive (2 talons, 4 victors)
    public static final int[] LEFT_DRIVE_VICTORS = {3,4};
    public static final int[] RIGHT_DRIVE_VICTORS = {5,6};

    // TODO put in correct IDs
    public static final int BALLPATH_FEED = 9;
    public static final int BALLPATH_KICKER = 10;
    public static final int BALLPATH_INTAKE = 11;
    public static final int CLIMB_VICTOR_1 = new CANSparkMax(7, MotorType.kBrushless); //is this what I was supposed to do?
    public static final int CLIMB_VICTOR_2 = new CANSparkMax(8, MotorType.kBrushless);
    public static final int TURRET_TALON = 12;
    public static final int LAUNCHER_TALON = 5;
    public static final int LAUNCHER_VICTOR = 6;
    public static final int HOOD_MOTOR = 13;
    public static final int INTAKECPWHEEL_TALON = 14;
    public static final int CPDEPLOY_TALON = 15;
}
