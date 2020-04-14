package year2020.subsystems;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.Quaternion;

public class Gyro extends Subsystem{
    public String getName(){
        return "Gyro";
    }
    public AHRS ahrs;
    public static void SetUpGyro(){
        ahrs = new AHRS (SPI.Port.kMPX);
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
        return ahrs.zeroYaw();
    }
    public static void Reset(){
        ahrs.reset();
    }
}