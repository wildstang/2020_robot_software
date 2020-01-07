package org.wildstang.year2016.subsystems;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;

public class EncoderTest implements Subsystem {
    // private static Encoder leftDriveEncoder;
    // private static Encoder rightDriveEncoder;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        // leftDriveEncoder = new Encoder(4, 5, true, EncodingType.k4X);
        //
        // leftDriveEncoder.setDistancePerPulse(5);
        // leftDriveEncoder.setSamplesToAverage(7);

    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // double leftDist = leftDriveEncoder.getDistance();
        // SmartDashboard.putNumber("left encoder value", getLeftEncoderValue());
        // SmartDashboard.putNumber("left distance", leftDist);
        // SmartDashboard.putNumber("other left distance", getLeftDistance());
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
    //
    // public double getLeftEncoderValue()
    // {
    // return leftDriveEncoder.get();
    // }
    //
    // public double getLeftDistance()
    // {
    // double distance = 0.0;
    // distance = (leftDriveEncoder.get() / (256 *
    // 7.5)) * 2.0 * Math.PI * (6 / 2.0);
    // return distance;
    // }
    //
    // public void resetLeftEncoder()
    // {
    // leftDriveEncoder.reset();
    // }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

}
