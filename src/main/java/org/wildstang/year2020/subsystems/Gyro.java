package year2020.gyro;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.Quaternion;

public class Gyro{
public void CallibrateGyro(){
        AHRS.calibrate();
    }
    public double Yaw(){
        return AHRS.getAngle();
    }
    public void Zero(){
        AHRS.zeroYaw();
    }
    public void Reset(){
        AHRS.reset();
    }
 }
