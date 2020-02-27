package org.wildstang.year2020.subsystems.led;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsI2COutput;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;
import org.wildstang.year2020.robot.WSSubsystems;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;
import org.wildstang.year2020.subsystems.climb.Climb;
import org.wildstang.year2020.subsystems.launching.Shooter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

    // TODO: switch everything to strings and work on colors in arduino code


public class LED implements Subsystem
{
    // Miscellaneous definitions
    private String name;
    WsI2COutput ledOutput;
    private Climb climb;
    private Ballpath ballpath;
    private Shooter shooter;
    DigitalInput selectButton;
    DigitalInput startButton;
    DigitalInput downButton;

    private SerialPort serialPort;

    // Pattern IDs
    //private static final int OFF_ID = 1;
    //private static final int DISABLED_ID = 2;
    //private static final int AUTO_ID = 3;
    //private static final int ALLIANCE_ID = 4;
    //private static final int CONTROL_PANEL_ID = 5;
    //private static final int LAUNCHER_ID = 6;
    //private static final int CLIMB_ID = 7;
    //private static final int FEEDER_JAM_ID = 8;

    // Data states
    boolean autoDataSent = false; // For auto, one time send
    boolean newDataAvailable = false; // For teleop, constant updates
    boolean disableDataSent = false; // For debugging purposes

    // Robot states
    boolean isRobotEnabled = false;
    boolean isRobotTeleop = false;
    boolean isRobotAuton = false;

    // Mechanism states
    boolean cpSpinning = false;
    boolean launcherReady = false;
    boolean launcherAiming = false;
    boolean launcherShooting = false;
    boolean climbRunning = false;
    boolean climbComplete = false;
    boolean feederJammed = false;

    // Color definitions
    // TODO Update colors
    //public static LedCmd offCmd = new LedCmd(OFF_ID, 0, 0, 0);
    //public static LedCmd disabledCmd = new LedCmd(DISABLED_ID, 255, 255, 255); // White (static)
    //public static LedCmd autoCmd = new LedCmd(AUTO_ID, 255, 255, 0); // Yellow (static)
    //public static LedCmd redAllianceCmd = new LedCmd(ALLIANCE_ID, 255, 0, 0); // Red (static)
    //public static LedCmd blueAllianceCmd = new LedCmd(ALLIANCE_ID, 0, 0, 255); // Blue (static)
    //public static LedCmd purpleAllianceCmd = new LedCmd(ALLIANCE_ID, 255, 0, 255); // Purple (static)

    //public static LedCmd cpSpinningCmd = new LedCmd(CONTROL_PANEL_ID, 0, 0, 0);
    //public static LedCmd launcherAimingCmd = new LedCmd(LAUNCHER_ID, 0, 0, 0);   // When limelight is detecting target and turret is lining up shot
    //public static LedCmd launcherReadyCmd = new LedCmd(LAUNCHER_ID, 0, 255, 0);  // When limelight has finished detecting target and flywheel is ready to get to speed
    //public static LedCmd launcherShootingCmd = new LedCmd(LAUNCHER_ID, 0, 0, 0); // When flywheel is getting to speed and shooting at target
    //public static LedCmd climbRunningCmd = new LedCmd(CLIMB_ID, 255, 255, 0); // Yellow
    //public static LedCmd climbCompleteCmd = new LedCmd(CLIMB_ID, 0, 255, 0); // Green (static)
    //public static LedCmd feederJammedCmd = new LedCmd(FEEDER_JAM_ID, 0, 0, 255);

    public static String offCmd = "OFF_ID";
    public static String disabledCmd = "DISABLED_ID";
    public static String autoCmd = "AUTO_ID";
    public static String blueAllianceCmd = "ALLIANCE_BLUE_ID";
    public static String redAllianceCmd = "ALLIANCE_RED_ID";
    public static String purpleAllianceCmd = "ALLIANCE_PURPLE_ID";

    public static String cpSpinningCmd = "CONTROL_PANEL_ID";
    public static String launcherAimingCmd = "LAUNCHER_AIMING_ID";   // When limelight is detecting target and turret is lining up shot
    public static String launcherReadyCmd = "LAUNCHER_READY_ID";  // When limelight has finished detecting target and flywheel is ready to get to speed
    public static String launcherShootingCmd = "LAUNCHER_SHOOTING_ID"; // When flywheel is getting to speed and shooting at target
    public static String climbRunningCmd = "CLIMB_RUNNING_ID";
    public static String climbCompleteCmd = "CLIMB_COMPLETE_ID";
    public static String feederJammedCmd = "FEEDER_JAM_ID";

    @Override
    public void init() {
        resetState();
        ledOutput = (WsI2COutput) Core.getOutputManager().getOutput(WSOutputs.LED.getName());
        serialPort = new SerialPort(9600, SerialPort.Port.kUSB);

        climb = (Climb) Core.getSubsystemManager().getSubsystem(WSSubsystems.CLIMB.getName());
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName());

        IInputManager inputManager = Core.getInputManager();
        // Climb buttons
        selectButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_SELECT.getName());
        selectButton.addInputListener(this);
        startButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_START.getName());
        startButton.addInputListener(this);
        downButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_DPAD_DOWN.getName());
        downButton.addInputListener(this);
        // TODO Add listeners for launcher and control panel subsystem inputs
    }

    @Override
    public void update() {
        // Change robot state booleans to the current robot state
        boolean isRobotEnabled = DriverStation.getInstance().isEnabled();
        boolean isRobotTeleop = DriverStation.getInstance().isOperatorControl();
        boolean isRobotAuton = DriverStation.getInstance().isAutonomous();

        if (isRobotEnabled) {
            if (isRobotTeleop) {
                //LedCmd command = offCmd; // Turns off LEDs everytime before updating
                String command = offCmd; // Turns off LEDs everytime before updating
                if (newDataAvailable) {
                    // The lower the logic gate in the list is, the higher priority it has
                    if (cpSpinning) {
                        command = cpSpinningCmd;
                    }
                    if (launcherReady) {
                        command = launcherReadyCmd;
                    }
                    if (launcherAiming) {
                        command = launcherAimingCmd;
                    }
                    if (launcherShooting) {
                        command = launcherShootingCmd;
                    }
                    if (feederJammed) {
                        command = feederJammedCmd;
                    }
                    if (climbRunning) {
                        command = climbRunningCmd;
                    }
                    if (climbComplete) {
                        command = climbCompleteCmd;
                    }
                    //ledOutput.setValue(command.getBytes());
                    serialPort.writeString(command);
                    SmartDashboard.putString("LedCmd", command);
                }
                newDataAvailable = false;
            } else if (isRobotAuton) {
                if (!autoDataSent) {
                    //ledOutput.setValue(autoCmd.getBytes());
                    autoDataSent = true;
                }
            }
        }
    }

    @Override
    public void inputUpdate(Input source) {
        // TODO properly update all mechanism states with their corresponding input
        //launcherReady is intakeReady?
        if (source.getName().equals(WSInputs.MANIPULATOR_FACE_DOWN.getName())) {
            launcherReady = true;
        // TODO Launcher
        } else if (source.getName().equals(WSInputs.MANIPULATOR_TRIGGER_RIGHT.getName())) {
            launcherShooting = true;
        // TODO Control Panel
        } else if (source.getName().equals(WSInputs.MANIPULATOR_LEFT_JOYSTICK_Y.getName())) {
            cpSpinning = ((DigitalInput)source).getValue();
        } else if (source.getName().equals(WSInputs.MANIPULATOR_LEFT_JOYSTICK_X.getName())) {
            cpSpinning = ((DigitalInput)source).getValue();
        // Climb
        } else if (source == selectButton) {
            climbRunning = ((DigitalInput)source).getValue();
        } else if (source == startButton) {
            climbRunning = ((DigitalInput)source).getValue();
        }
        newDataAvailable = true;
    }
    
    //public void sendCommand(LedCmd p_command) {
    //    currentCmd = p_command;
    //    newDataAvailable = true;
    //}

    public void sendFeederJammed() {
        feederJammed = true;
        newDataAvailable = true;
    }

//    public static class LedCmd {
//        byte[] dataBytes = new byte[4];
//        public int command;
//        public LedCmd(int command, int red, int green, int blue) {
//            dataBytes[0] = (byte) command;
//            dataBytes[1] = (byte) red;
//            dataBytes[2] = (byte) green;
//            dataBytes[3] = (byte) blue;
//
//            this.command = command;
//        }
//        public byte[] getBytes() {
//            return dataBytes;
//        }
//    }

    @Override
    public void resetState() {
        autoDataSent = false;
        newDataAvailable = false;
        disableDataSent = false;
    }

    public LED() {
        name = "LED";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void selfTest() {
        // For debugging purposes
    }
}
