package org.wildstang.year2020.subsystems.launching;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight implements Subsystem {

    // Miscellaneous
    private NetworkTable netTable;

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
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

        netTable = NetworkTableInstance.getDefault().getTable("limelight-stang");

        txEntry = netTable.getEntry("tx");
        tyEntry = netTable.getEntry("ty");
        tsEntry = netTable.getEntry("ts");
        tshortEntry = netTable.getEntry("tshort");
        tlongEntry = netTable.getEntry("tlong");
        tvertEntry = netTable.getEntry("tvert");
        thorEntry = netTable.getEntry("thor");

        ledModeEntry = netTable.getEntry("ledMode");
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Limelight";
    }

    // Gives tx value from Limelight
    // Returns 0.0 exactly if value can't be retrieved
    public double getTXValue() {
        return txEntry.getDouble(0.0);
    }

    // Gives ty value from Limelight
    // Returns 0.0 exactly if value can't be retrieved
    public double getTYValue() {
        return tyEntry.getDouble(0.0);
    }

    // Gives ts value from Limelight
    // Returns 0.0 exactly if value can't be retrieved
    public double getTSValue() {
        return tsEntry.getDouble(0.0);
    }

    // Gives tshort value from Limelight
    // Returns 0.0 exactly if value can't be retrieved
    public double getTShortValue() {
        return tshortEntry.getDouble(0.0);
    }

    // Gives tlong value from Limelight
    // Returns 0.0 exactly if value can't be retrieved
    public double getTLongValue() {
        return tlongEntry.getDouble(0.0);
    }

    // Gives tvert value from Limelight
    // Returns 0.0 exactly if value can't be retrieved
    public double getTVertValue() {
        return tvertEntry.getDouble(0.0);
    }

    // Gives thor value from Limelight
    // Returns 0.0 exactly if value can't be retrieved
    public double getTHorValue() {
        return thorEntry.getDouble(0.0);
    }

    // Switch LEDs to auto mode (mode 0)
    public void enableLEDs() {
        ledModeEntry.setNumber(0);
    }

    // Switch LEDs to forced off mode (mode 1)
    public void disableLEDs() {
        ledModeEntry.setNumber(1);
    }
    
}