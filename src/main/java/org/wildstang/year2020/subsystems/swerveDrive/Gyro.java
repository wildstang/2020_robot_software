package org.wildstang.year2020.subsystems.swerveDrive;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.Quaternion;

import edu.wpi.first.wpilibj.SPI;

import org.wildstang.framework.subsystems.Subsystem;

public abstract class Gyro implements Subsystem{
    public String getName(){
        return "Gyro";
    }
    public static AHRS ahrs;
    public static void SetUpGyro(){
        ahrs = new AHRS (SPI.Port.kMXP);
    }
    public static void CallibrateGyro(){
            ahrs.calibrate();
        }
    public static double Yaw(){
        return ahrs.getYaw();
    }
    public static double Pitch(){
        return ahrs.getPitch();
    }
    public static double Roll(){
        return ahrs.getRoll();
    }
    public static void Zero(){
        ahrs.zeroYaw();
    }
    public static void Reset(){
        ahrs.reset();
    }
    public void resetState(){
        //nothing
    }
}