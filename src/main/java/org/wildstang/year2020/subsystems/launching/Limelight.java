package org.wildstang.year2020.subsystems.launching;

import java.util.ArrayList;
import java.util.List;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Limelight implements Subsystem {

    // Inputs
    private AnalogInput aimModeTrigger;

    // Constants (angles in degrees; distances in inches)
    public static final double MOUNT_VERTICAL_ANGLE_OFFSET = 0.0;
    public static final double MOUNT_HEIGHT = 0.0;
    public static final double VISION_TARGET_HEIGHT = 81.25;

    // Logic
    private NetworkTable netTable;

    private NetworkTableEntry taEntry;
    private NetworkTableEntry tvEntry;
    private NetworkTableEntry txEntry;
    private NetworkTableEntry tyEntry;
    private NetworkTableEntry tsEntry;
    private NetworkTableEntry tshortEntry;
    private NetworkTableEntry tlongEntry;
    private NetworkTableEntry tvertEntry;
    private NetworkTableEntry thorEntry;

    private NetworkTableEntry ledModeEntry;

    private List<Double> trailingVerticalAngleOffsets;
    private long lastValueAddedTimestamp;

    @Override
    // Initializes the subsystem (inputs, outputs and logical variables)
    public void init() {
        initInputs();
        initOutputs();
        resetState();
    }

    // Initializes inputs
    private void initInputs() {
        aimModeTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_LEFT);
        aimModeTrigger.addInputListener(this);

        netTable = NetworkTableInstance.getDefault().getTable("limelight-stang");

        taEntry = netTable.getEntry("ta");
        tvEntry = netTable.getEntry("tv");
        txEntry = netTable.getEntry("tx");
        tyEntry = netTable.getEntry("ty");
        tsEntry = netTable.getEntry("ts");
        tshortEntry = netTable.getEntry("tshort");
        tlongEntry = netTable.getEntry("tlong");
        tvertEntry = netTable.getEntry("tvert");
        thorEntry = netTable.getEntry("thor");
    }

    // Initializes outputs
    private void initOutputs() {
        ledModeEntry = netTable.getEntry("ledMode");

        ledModeEntry.setNumber(0); // FOR TESTING PURPOSES: LEDs should always be on
    }

    @Override
    // Responds to updates from inputs
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
    // Returns the subsystem's name
    public String getName() {
        return "Limelight";
    }

    public double getTAValue() {
        return taEntry.getDouble(0.0);
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
        ledModeEntry.setNumber(0); // FOR TESTING PURPOSES: LEDs should always be on
    }

    // Calculates horizontal distance to target using the ty value and robot and field constants
    public double getDistanceToTarget() {
        // double verticalAngleOffsetSum = 0.0;

        // for (int i = 0; i < trailingVerticalAngleOffsets.size(); i++) {
        //     verticalAngleOffsetSum += trailingVerticalAngleOffsets.get(i);
        // }

        // double netVerticalAngleOffset = Math.toRadians(verticalAngleOffsetSum / (double) trailingVerticalAngleOffsets.size());
        // double targetHeightAboveCamera = VISION_TARGET_HEIGHT - MOUNT_HEIGHT;
        // double distanceToTarget = targetHeightAboveCamera / Math.tan(netVerticalAngleOffset);

        double distance = (75.5 / Math.sin(Math.toRadians(33.25 + getTYValue()))) / 12.0;

        return distance;
    }

    // Calculates estimated distance to inner goal (right now, we're saying it's target distance + 30 inches)
    public double getDistanceToInnerGoal() {
        return getDistanceToTarget() + 2.5;
    }

    @Override
    // Tests the subsystem (unimplemented right now)
    public void selfTest() {}

    @Override
    // Updates the subsystem everytime the framework updates (every ~0.02 seconds)
    public void update() {
        double verticalAngleOffset = getTYValue() + MOUNT_VERTICAL_ANGLE_OFFSET;
        if (System.currentTimeMillis() > lastValueAddedTimestamp + 25L) {
            if (trailingVerticalAngleOffsets.size() == 20) {
                trailingVerticalAngleOffsets.remove(0);
            }

            trailingVerticalAngleOffsets.add(verticalAngleOffset);
            lastValueAddedTimestamp = System.currentTimeMillis();
        }
        SmartDashboard.putNumber("Target Distance", getDistanceToTarget());
    }

    @Override
    // Resets all variables to the default state (unimplemented right now)
    public void resetState() {
        trailingVerticalAngleOffsets = new ArrayList<Double>();
        lastValueAddedTimestamp = 0L;
    }
}
