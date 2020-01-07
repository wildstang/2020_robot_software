package org.wildstang.hardware.crio.inputs;

import org.wildstang.framework.io.inputs.AnalogInput;

import edu.wpi.first.wpilibj.AnalogGyro;

public class WsAnalogGyro extends AnalogInput {

    private static final double DRIFT_PER_NANO_FIXED = .903; // GOOD DEFAULT VALUE TO USE

    private static final int DRIFT_MEASUREMENT_TIME_MS = 500;

    private AnalogGyro m_gyro;
    private boolean m_driftCompensation;
    private long m_startTime;
    private double m_driftPerNanosecond = DRIFT_PER_NANO_FIXED;

    public WsAnalogGyro(String p_name, int p_channel, boolean p_driftCompensation) {
        super(p_name);
        m_gyro = new AnalogGyro(p_channel);
        m_driftCompensation = p_driftCompensation;

        //m_gyro.calibrate();

        m_startTime = System.nanoTime();
        double angle1 = m_gyro.getAngle();

        try {
            Thread.sleep(DRIFT_MEASUREMENT_TIME_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        double angle2 = m_gyro.getAngle();

        m_driftPerNanosecond = (angle2 - angle1) / (endTime - m_startTime);

    }

    @Override
    public double readRawValue() {
        if (m_driftCompensation) {
            return m_gyro.getAngle() - ((System.nanoTime() - m_startTime) * m_driftPerNanosecond);
        } else {
            return m_gyro.getAngle();
        }
    }

    public void calibrate() {
        m_gyro.calibrate();
    }
}
