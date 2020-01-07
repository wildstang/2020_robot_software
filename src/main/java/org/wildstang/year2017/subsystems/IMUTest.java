package org.wildstang.year2017.subsystems;

import javax.swing.Timer;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.inputs.WsI2CInput;
import org.wildstang.year2017.robot.WSInputs;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IMUTest implements Subsystem {

    private byte[] HeadingBytes = new byte[2];
    private double CompassHeading;
    // Gyro m_gyro = new AnalogGyro(0);
    // double angle = 0;
    // double startTime;
    // private final double DRIFT_PER_NANO_FIXED = .903; //GOOD DEFAULT VALUE TO USE
    // private double DRIFT_PER_NANO = DRIFT_PER_NANO_FIXED;
    // boolean firstRun = true;

    private String m_name;

    WsI2CInput m_IMUInput;

    public IMUTest() {
        m_name = "IMUTest";
    }

    @Override
    public void inputUpdate(Input p_source) {
        // TODO Auto-generated method stub
        // if (p_source.getName().equals(WSInputs.IMU.getName()))
        {
            // HeadingBytes = ((WsI2CInput) p_source).getValue();
            HeadingBytes = m_IMUInput.getValue();
        }
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        // Core.getInputManager().getInput(WSInputs.IMU.getName()).addInputListener(this);

        // m_IMUInput = (WsI2CInput)
        // Core.getInputManager().getInput(WSInputs.IMU.getName());

        // m_gyro.calibrate();
        // startTime = System.nanoTime();
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // if (firstRun) {
        // DRIFT_PER_NANO = m_gyro.getAngle() / (System.nanoTime() - startTime);
        // firstRun = false;
        // }
        // TODO Auto-generated method stub
        CompassHeading = (double) HeadingBytes[0] * 2;
        // angle = m_gyro.getAngle() - getAdjustment();
        SmartDashboard.putNumber("IMU Test Heading", CompassHeading);
        // SmartDashboard.putNumber("Gyro Heading", m_gyro.getAngle());
        // SmartDashboard.putNumber("Robot Angle", angle);
    }

    // public double getAdjustment() {
    // return (System.nanoTime() - startTime) * DRIFT_PER_NANO;
    // }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "IMU Test";
    }

}
