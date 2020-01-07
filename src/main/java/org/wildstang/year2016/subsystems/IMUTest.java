package org.wildstang.year2016.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.inputs.WsI2CInput;
import org.wildstang.year2016.robot.WSInputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IMUTest implements Subsystem {

    private byte[] HeadingBytes = new byte[2];
    private double CompassHeading;

    WsI2CInput m_IMUInput;

    public IMUTest() {
    }

    @Override
    public void inputUpdate(Input p_source) {
        // TODO Auto-generated method stub
        if (p_source.getName().equals(WSInputs.IMU.getName())) {
            // HeadingBytes = ((WsI2CInput) p_source).getValue();
            HeadingBytes = m_IMUInput.getValue();
        }
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        Core.getInputManager().getInput(WSInputs.IMU.getName()).addInputListener(this);

        m_IMUInput = (WsI2CInput) Core.getInputManager().getInput(WSInputs.IMU.getName());
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        CompassHeading = (HeadingBytes[0] * 100) + HeadingBytes[1];
        SmartDashboard.putNumber("Low Byte", HeadingBytes[1]);
        SmartDashboard.putNumber("High Byte", (HeadingBytes[0] * 100));
        SmartDashboard.putNumber("IMU Heading", CompassHeading);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "IMU Test";
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

}
