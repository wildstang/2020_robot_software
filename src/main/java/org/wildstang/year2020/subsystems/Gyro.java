package year2020.subsystems;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.Quaternion;

public class Gyro{
        AHRS A = new AHRS();
public static void CallibrateGyro(){
        A.calibrate();
    }
    public static double Yaw(){
        return A.getAngle();
    }
    public static void Zero(){
        A.zeroYaw();
    }
    public static void Reset(){
        A.reset();
    }
 }
