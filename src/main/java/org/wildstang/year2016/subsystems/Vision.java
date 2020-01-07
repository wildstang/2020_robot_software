package org.wildstang.year2016.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.RemoteAnalogInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2016.robot.WSInputs;

//import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision implements Subsystem {
    private final int CENTERED_LEFT = 290;
    private final int CENTERED_RIGHT = 330;
    private final int BOTTOM_LOW_MIN = 45;
    private final int BOTTOM_LOW_MAX = 70;
    private final int BOTTOM_HIGH_MIN = 130;
    private final int BOTTOM_HIGH_MAX = 152;

    private int m_centered_left;
    private int m_centered_right;
    private int m_bottom_low_min;
    private int m_bottom_low_max;
    private int m_bottom_high_min;
    private int m_bottom_high_max;

    private int targetBottom = 7;
    private int targetCenter = 8;
    private double robotAngle;
    private double robotDistance;
    private int rotateInt = 6;

    private boolean onTarget = false;
    private boolean goodLongHeight = false;
    private boolean goodShortHeight = false;

    // NetworkTable table;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        if (source.getName().equals(WSInputs.TARGET_BOTTOM.getName())) {
            targetBottom = (int) ((RemoteAnalogInput) source).getValue();
        }
        if (source.getName().equals(WSInputs.TARGET_CENTER.getName())) {
            targetCenter = (int) ((RemoteAnalogInput) source).getValue();
        }
        if (source.getName().equals(WSInputs.VISION_ANGLE.getName())) {
            robotAngle = ((RemoteAnalogInput) source).getValue();
        }
        if (source.getName().equals(WSInputs.VISION_DISTANCE.getName())) {
            robotDistance = ((RemoteAnalogInput) source).getValue();
        }

    }

    @Override
    public void init() {
        // NetworkTable.setIPAddress("10.1.11.2");
        // NetworkTable.setClientMode();
        // table = NetworkTable.getTable("remoteIO");

        Core.getInputManager().getInput(WSInputs.TARGET_BOTTOM.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.TARGET_CENTER.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.VISION_ANGLE.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.VISION_DISTANCE.getName()).addInputListener(this);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
    }

    @Override
    public void update() {
        // targetCenter = (int) table.getNumber("Target Center", 2);
        m_centered_left = (int) SmartDashboard.getNumber("Left Centered Mark", CENTERED_LEFT);
        m_centered_right = (int) SmartDashboard.getNumber("Right Centered Mark", CENTERED_RIGHT);
        m_bottom_low_min = (int) SmartDashboard.getNumber("Target Bottom Low Min", BOTTOM_LOW_MIN);
        m_bottom_low_max = (int) SmartDashboard.getNumber("Target Bottom Low Min", BOTTOM_LOW_MAX);
        m_bottom_high_min = (int) SmartDashboard.getNumber("Target Bottom Low Min",
                BOTTOM_HIGH_MIN);
        m_bottom_high_max = (int) SmartDashboard.getNumber("Target Bottom Low Min",
                BOTTOM_HIGH_MAX);

        m_centered_left = (int) SmartDashboard.getNumber("Left Centered Mark", CENTERED_LEFT);
        m_centered_right = (int) SmartDashboard.getNumber("Right Centered Mark", CENTERED_RIGHT);
        m_bottom_low_min = (int) SmartDashboard.getNumber("Target Bottom Low Min", BOTTOM_LOW_MIN);
        m_bottom_low_max = (int) SmartDashboard.getNumber("Target Bottom Low Min", BOTTOM_LOW_MAX);
        m_bottom_high_min = (int) SmartDashboard.getNumber("Target Bottom Low Min",
                BOTTOM_HIGH_MIN);
        m_bottom_high_max = (int) SmartDashboard.getNumber("Target Bottom Low Min",
                BOTTOM_HIGH_MAX);

        if (targetBottom > m_bottom_low_min && targetBottom < m_bottom_low_max) {
            goodLongHeight = true;
        } else {
            goodLongHeight = false;
        }

        if (targetBottom > m_bottom_high_min && targetBottom < m_bottom_high_max) {
            goodShortHeight = true;
        } else {
            goodShortHeight = false;
        }

        if (targetCenter > m_centered_left && targetCenter < m_centered_right) {
            rotateInt = 0;
        } else if (m_centered_left >= targetCenter) {
            rotateInt = -1;
        } else {
            rotateInt = 1;
        }

        onTarget = ((rotateInt == 0) ? true : false);
        SmartDashboard.putNumber("Rotation Integer", rotateInt);
        SmartDashboard.putNumber("TargetBottom", targetBottom);
        boolean xTarget = rotateInt == 0 ? true : false;
        SmartDashboard.putBoolean("On Target X", xTarget);
        SmartDashboard.putBoolean("On Target Y", goodLongHeight || goodShortHeight);
        SmartDashboard.putNumber("Target Center", targetCenter);

        // SmartDashboard.putNumber("Camera Distance", distanceToTarget);
        // SmartDashboard.putNumber("Camera Angle", angleToRotate);
        // SmartDashboard.putBoolean("On Target?", isOnTarget);

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Vision";
    }

    public int getRotateInt() {
        return rotateInt;
    }

    public double getDistanceToTarget() {
        return robotDistance;
    }

    public double getAngleToTarget() {
        return robotAngle;
    }

    public boolean getOnTarget() {
        return onTarget;
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

}