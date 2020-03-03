package org.wildstang.year2020.subsystems.led;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

// TODO: display control panel colors

public class LED implements Subsystem
{
    // General definitions
    private String name;
    private SerialPort serialPort;
    private Climb climb;
    private Ballpath ballpath;
    private Shooter shooter;
    DigitalInput selectButton;
    DigitalInput startButton;
    DigitalInput downButton;

    // Data states
    boolean autoDataSent = false;
    boolean newDataAvailable = false;
    boolean disableDataSent = false;

    // Robot states
    boolean isRobotEnabled = false;
    boolean isRobotTeleop = false;
    boolean isRobotAuton = false;
    String alliance;
    String gameData;

    // Mechanism data
    int cpColor;
    boolean launcherReady = false;
    boolean launcherAiming = false;
    boolean launcherShooting = false;
    boolean innerPortAim = false;
    boolean climbRunning = false;
    boolean climbComplete = false;
    boolean feederJammed = false;

    // LED commands
    public static String offCmd = "OFF_ID";
    public static String disabledCmd = "DISABLED_ID";
    public static String idleCmd = "IDLE_ID";
    public static String autoCmd = "AUTO_ID";
    public static String allianceBlueCmd = "ALLIANCE_BLUE_ID";
    public static String allianceRedCmd = "ALLIANCE_RED_ID";
    public static String allianceRainbowCmd = "ALLIANCE_RAINBOW_ID";
    public static String cpRedCmd = "CONTROL_PANEL_RED_ID";
    public static String cpYellowCmd = "CONTROL_PANEL_YELLOW_ID";
    public static String cpGreenCmd = "CONTROL_PANEL_GREEN_ID";
    public static String cpBlueCmd = "CONTROL_PANEL_BLUE_ID";
    public static String launcherAimingCmd = "LAUNCHER_AIMING_ID";   // When hood is aiming
    public static String launcherReadyCmd = "LAUNCHER_READY_ID";  // When hood is aimed
    public static String launcherShootingCmd = "LAUNCHER_SHOOTING_ID"; // When flywheel is shooting at target
    public static String innerPortCmd = "INNER_PORT_ID"; // When aim allows for inner port shots
    public static String climbRunningCmd = "CLIMB_RUNNING_ID";
    public static String climbCompleteCmd = "CLIMB_COMPLETE_ID";
    public static String feederJammedCmd = "FEEDER_JAM_ID";

    @Override
    public void init() {
        resetState();

        // Initialize new virtual serial port over USB with baud rate of 9600
        serialPort = new SerialPort(9600, SerialPort.Port.kUSB);

        // Initialize subsystems
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName());
        climb = (Climb) Core.getSubsystemManager().getSubsystem(WSSubsystems.CLIMB.getName());

        // Initialize inputs
        IInputManager inputManager = Core.getInputManager();
        // Launcher
        rightTrigger = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_TRIGGER_RIGHT.getName());
        // Climb
        selectButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_SELECT.getName());
        selectButton.addInputListener(this);
        startButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_START.getName());
        startButton.addInputListener(this);
        downButton = (DigitalInput) inputManager.getInput(WSInputs.MANIPULATOR_DPAD_DOWN.getName());
        downButton.addInputListener(this);
    }

    @Override
    public void update() {
        // Change robot state booleans to the current robot state
        isRobotEnabled = DriverStation.getInstance().isEnabled();
        isRobotTeleop = DriverStation.getInstance().isOperatorControl();
        isRobotAuton = DriverStation.getInstance().isAutonomous();
        //alliance = DriverStation.getInstance().getAlliance().name();
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        
        // Change mechanism data to current mechanism state
        launcherReady = shooter.isHoodAimed();
        innerPortAim = shooter.willAimToInnerGoal();

        // Identify control panel position color from FMS and store it in a variable
        if (gameData.length() > 0) {
            switch (gameData.charAt(0)) {
                case 'R' :
                    cpColor = 0;
                    break;
                case 'Y' :
                    cpColor = 1;
                    break;
                case 'G' :
                    cpColor = 2;
                    break;
                case 'B' :
                    cpColor = 3;
                    break;
                default :
                    // This is corrupt data
                    break;
            }
        }

        if (isRobotEnabled) {
            if (isRobotTeleop) {
                String command = idleCmd; // What command should the LEDs run every time the robot is enabled?
                if (newDataAvailable) {
                    // The lower the logic gate in the list is, the higher priority it has
                    if (cpColor == 0) {
                        command = cpRedCmd;
                    }
                    if (cpColor == 1) {
                        command = cpYellowCmd;
                    }
                    if (cpColor == 2) {
                        command = cpGreenCmd;
                    }
                    if (cpColor == 3) {
                        command = cpBlueCmd;
                    }
                    if (launcherReady) {
                        command = launcherReadyCmd;
                        if (innerPortAim) {
                            command = innerPortCmd;
                        }
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
                    serialPort.writeString(command + "\n");
                    SmartDashboard.putString("LedCmd", command);
                    newDataAvailable = false;
                }
            } else if (isRobotAuton) {
                if (!autoDataSent) {
                    autoDataSent = true;
                }
            }
        }
    }

    @Override
    public void inputUpdate(Input source) {
        // Launcher
        if (source == rightTrigger) {
            launcherShooting = ((DigitalInput)source).getValue();
        }
        // Climb
        if (selectButton.getValue() && startButton.getValue()) {
            climbRunning = true;
        } else {
            climbRunning = false;
        }

        newDataAvailable = true;
    }

    public void sendFeederJammed() {
        feederJammed = true;
        newDataAvailable = true;
    }

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
