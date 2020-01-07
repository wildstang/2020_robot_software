package org.wildstang.year2016.subsystems;

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

    public Monitor() {
        m_name = "Monitor";
    }

    @Override
    public void init() {
        pdp = new PowerDistributionPanel();

        // Add the monitored inputs
        Core.getStateTracker().addIOInfo("Current 0", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 1", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 2", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 3", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 4", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 5", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 6", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 7", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 8", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 9", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 10", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 11", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 12", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 13", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 14", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Current 15", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Total Current", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Voltage", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Temperature", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Enabled", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Teleop", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Auto", "Monitor", "Input", null);
        Core.getStateTracker().addIOInfo("Memory in use", "Monitor", "Input", null);
    }

    @Override
    public void update() {

        // TODO: Change to use state tracker
        StateTracker tracker = Core.getStateTracker();

        for (int i = 0; i < 16; i++) {
            double current = pdp.getCurrent(i);
            tracker.addState("Current " + i, "PDP", current);
        }

        double totalCurrent = pdp.getTotalCurrent();
        tracker.addState("Total Current", "PDP", totalCurrent);
        SmartDashboard.putNumber("Current", totalCurrent);

        double voltage = pdp.getVoltage();
        tracker.addState("Voltage", "PDP", voltage);
        SmartDashboard.putNumber("Voltage", voltage);

        double pdpTemp = pdp.getTemperature();
        tracker.addState("Temperature", "PDP", pdpTemp);
        SmartDashboard.putNumber("Temperature", pdpTemp);

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

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

}
