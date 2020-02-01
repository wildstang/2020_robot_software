package org.wildstang.year2020.subsystems.launching;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight implements Subsystem {

    // Inputs
    private AnalogInput aimModeTrigger;

    // Constants (angles in degrees; distances in inches)
    public static final double MOUNT_VERTICAL_ANGLE_OFFSET = 0.0;
    public static final double MOUNT_HEIGHT = 0.0;
    public static final double VISION_TARGET_HEIGHT = 81.25;

    // Logic
    private NetworkTable netTable;

    private NetworkTableEntry tvEntry;
    private NetworkTableEntry txEntry;
    private NetworkTableEntry tyEntry;
    private NetworkTableEntry tsEntry;
    private NetworkTableEntry tshortEntry;
    private NetworkTableEntry tlongEntry;
    private NetworkTableEntry tvertEntry;
    private NetworkTableEntry thorEntry;

    private NetworkTableEntry ledModeEntry;

    @Override
    public void inputUpdate(Input source) {
        if (source == aimModeTrigger) {
            if (aimModeTrigger.getValue() > 0.75) {
                enableLEDs();
            } else {
                disableLEDs();
            }
        }
    }

    @Override
    public void init() {
        aimModeTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.TURRET_AIM_MODE_TRIGGER);
        aimModeTrigger.addInputListener(this);

        netTable = NetworkTableInstance.getDefault().getTable("limelight-stang");

        tvEntry = netTable.getEntry("tv");
        txEntry = netTable.getEntry("tx");
        tyEntry = netTable.getEntry("ty");
        tsEntry = netTable.getEntry("ts");
        tshortEntry = netTable.getEntry("tshort");
        tlongEntry = netTable.getEntry("tlong");
        tvertEntry = netTable.getEntry("tvert");
        thorEntry = netTable.getEntry("thor");

        ledModeEntry = netTable.getEntry("ledMode");

        ledModeEntry.setNumber(0);
    }

    @Override
    public String getName() {
        return "Limelight";
    }

    // Gives tv value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTVValue() {
        return tvEntry.getDouble(0.0);
    }

    // Gives tx value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTXValue() {
        return txEntry.getDouble(0.0);
    }

    // Gives ty value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTYValue() {
        return tyEntry.getDouble(0.0);
    }

    // Gives ts value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTSValue() {
        return tsEntry.getDouble(0.0);
    }

    // Gives tshort value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTShortValue() {
        return tshortEntry.getDouble(0.0);
    }

    // Gives tlong value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTLongValue() {
        return tlongEntry.getDouble(0.0);
    }

    // Gives tvert value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTVertValue() {
        return tvertEntry.getDouble(0.0);
    }

    // Gives thor value from Limelight
    // Returns 0.0 if value can't be retrieved
    public double getTHorValue() {
        return thorEntry.getDouble(0.0);
    }

    // Switch LEDs to auto mode (mode 0)
    public void enableLEDs() {
        ledModeEntry.setNumber(0);
    }

    // Switch LEDs to forced off mode (mode 1)
    public void disableLEDs() {
        ledModeEntry.setNumber(0);
    }

    // Calculates horizontal distance to target using the ty value and robot and field constants
    public double getDistanceToTarget() {
        double totalVerticalAngleOffset = Math.toRadians(getTYValue() + MOUNT_VERTICAL_ANGLE_OFFSET);
        double targetHeightAboveCamera = VISION_TARGET_HEIGHT - MOUNT_HEIGHT;
        
        double distanceToTarget = targetHeightAboveCamera / Math.tan(totalVerticalAngleOffset);

        return distanceToTarget;
    }

    @Override
    public void selfTest() {}

    @Override
    public void update() {}

    @Override
    public void resetState() {}
}
