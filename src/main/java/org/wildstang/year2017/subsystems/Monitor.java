package org.wildstang.year2017.subsystems;

import java.util.HashMap;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.logger.StateTracker;
import org.wildstang.framework.subsystems.Subsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Monitor implements Subsystem {

    PowerDistributionPanel pdp;
    String m_name;

    private HashMap<Integer, String> m_nameMap = new HashMap<Integer, String>();

    private static final String NAME_0 = "Current 0 (Drive right master)";
    private static final String NAME_1 = "Current 1 (Drive right follower)";
    private static final String NAME_2 = "Current 2 (Shooter right)";
    private static final String NAME_3 = "Current 3 (Climber)";
    private static final String NAME_4 = "Current 4 (Feed right)";
    private static final String NAME_5 = "Current 5 (PCM)";
    private static final String NAME_6 = "Current 6 (MXP)";
    private static final String NAME_7 = "Current 7";
    private static final String NAME_8 = "Current 8";
    private static final String NAME_9 = "Current 9";
    private static final String NAME_10 = "Current 10 (Intake)";
    private static final String NAME_11 = "Current 11 (Feed left)";
    private static final String NAME_12 = "Current 12";
    private static final String NAME_13 = "Current 13 (Shooter left)";
    private static final String NAME_14 = "Current 14 (Drive left master)";
    private static final String NAME_15 = "Current 15 (Drive left follower)";

    public Monitor() {
        m_name = "Monitor";
    }

    @Override
    public void init() {
        m_nameMap.put(0, NAME_0);
        m_nameMap.put(1, NAME_1);
        m_nameMap.put(2, NAME_2);
        m_nameMap.put(3, NAME_3);
        m_nameMap.put(4, NAME_4);
        m_nameMap.put(5, NAME_5);
        m_nameMap.put(6, NAME_6);
        m_nameMap.put(7, NAME_7);
        m_nameMap.put(8, NAME_8);
        m_nameMap.put(9, NAME_9);
        m_nameMap.put(10, NAME_10);
        m_nameMap.put(11, NAME_11);
        m_nameMap.put(12, NAME_12);
        m_nameMap.put(13, NAME_13);
        m_nameMap.put(14, NAME_14);
        m_nameMap.put(15, NAME_15);

        pdp = new PowerDistributionPanel();

        // Add the monitored inputs
        Core.getStateTracker().addIOInfo(NAME_0, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_1, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_2, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_3, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_4, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_5, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_6, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_7, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_8, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_9, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_10, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_11, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_12, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_13, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_14, "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo(NAME_15, "Monitor", "Input", null);

        Core.getStateTracker().addIOInfo("Total Current", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Voltage", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Temperature", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Enabled", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Teleop", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Auto", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Memory in use", "Monitor", "Input", null);
    }

    @Override
    public void resetState() {
    }

    @Override
    public void update() {

        // TODO: Change to use state tracker
        StateTracker tracker = Core.getStateTracker();

        for (int i = 0; i < 16; i++) {
            double current = pdp.getCurrent(i);
            tracker.addState(m_nameMap.get(i), "PDP", current);
        }

        double totalCurrent = pdp.getTotalCurrent();
        tracker.addState("Total Current", "PDP", totalCurrent);

        double voltage = pdp.getVoltage();
        tracker.addState("Voltage", "PDP", voltage);

        double pdpTemp = pdp.getTemperature();
        tracker.addState("Temperature", "PDP", pdpTemp);

        boolean isRobotEnabled = DriverStation.getInstance().isEnabled();
        boolean isRobotTeleop = DriverStation.getInstance().isOperatorControl();
        boolean isRobotAuton = DriverStation.getInstance().isAutonomous();

        tracker.addState("Enabled", "Robot state", isRobotEnabled);
        tracker.addState("Teleop", "Robot state", isRobotTeleop);
        tracker.addState("Auto", "Robot state", isRobotAuton);

        Runtime rt = Runtime.getRuntime();
        tracker.addState("Memory in use", "PDP", rt.totalMemory() - rt.freeMemory());
    }

    @Override
    public void inputUpdate(Input source) {
        // Nothing to do for monitor
    }

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return m_name;
    }

}
