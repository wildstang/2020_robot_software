package org.wildstang.year2020.subsystems;

import org.wildstang.framework.io.Input;
import org.wildstang.framework.subsystems.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Limelight implements Subsystem {

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        // TODO Auto-generated method 
        
        
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

        NetworkTable netTable = NetworkTableInstance.getDefault().getTable("limelight-stang");
        netTable.getEntry("ledMode").setNumber(0);
        netTable.getEntry("camMode").setNumber(0);
        netTable.getEntry("pipeline").setNumber(0);
        netTable.getEntry("stream").setNumber(0);
        netTable.getEntry("snapshot").setNumber(0);


        NetworkTableEntry tx = netTable.getEntry("tx");
        NetworkTableEntry ty = netTable.getEntry("ty");
        NetworkTableEntry ta = netTable.getEntry("ta");
        NetworkTableEntry tv = netTable.getEntry("tv");
        NetworkTableEntry ts = netTable.getEntry("ts");
        NetworkTableEntry tl = netTable.getEntry("tl");
        NetworkTableEntry tshort = netTable.getEntry("tshort");
        NetworkTableEntry tlong = netTable.getEntry("tlong");
        NetworkTableEntry thor = netTable.getEntry("thor");
        NetworkTableEntry tvert = netTable.getEntry("tvert");
        NetworkTableEntry getpipe = netTable.getEntry("getpipe");
        NetworkTableEntry camtran = netTable.getEntry("camtran");


        SmartDashboard.putNumber("tx", tx.getDouble(3.0));
        SmartDashboard.putNumber("ty", ty.getDouble(3.0));
        SmartDashboard.putNumber("ta", ta.getDouble(3.0));
        SmartDashboard.putNumber("tv", tv.getDouble(3.0));
        SmartDashboard.putNumber("ts", ts.getDouble(3.0));
        SmartDashboard.putNumber("tl", tl.getDouble(3.0));
        SmartDashboard.putNumber("tshort", tshort.getDouble(3.0));
        SmartDashboard.putNumber("tlong", tlong.getDouble(3.0));
        SmartDashboard.putNumber("thor", thor.getDouble(3.0));
        SmartDashboard.putNumber("tvert", tvert.getDouble(3.0));
        SmartDashboard.putNumber("getpipe", getpipe.getDouble(3.0));
        // SmartDashboard.putNumber("camtran", camtran.getDouble(3.0));

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
    
}