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

//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

    // TODO: display control panel colors


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
        String alliance = DriverStation.getInstance().getAlliance().name();
        // TODO Add FMS control panel position color

        if (isRobotEnabled) {
            if (isRobotTeleop) {
                String command = idleCmd; // What should the LEDs do every time once the robot is enabled?
                if (newDataAvailable) {
                    // The lower the logic gate in the list is, the higher priority it has
                    if (cpRed) {
                        command = cpRedCmd;
                    }
                    if (cpYellow) {
                        command = cpYellowCmd;
                    }
                    if (cpGreen) {
                        command = cpGreenCmd;
                    }
                    if (cpBlue) {
                        command = cpBlueCmd;
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
        // TODO properly update all mechanism states with their corresponding input
        //launcherReady is intakeReady?
        if (source.getName().equals(WSInputs.MANIPULATOR_FACE_DOWN.getName())) {
            launcherReady = true;
        // TODO Launcher
        } else if (source.getName().equals(WSInputs.MANIPULATOR_TRIGGER_RIGHT.getName())) {
            launcherShooting = true;
        // TODO DO NOT USE CONTROL PANEL CONTROLS, SET THE COLOR BASED ON THE FMS
        //} else if (source.getName().equals(WSInputs.MANIPULATOR_LEFT_JOYSTICK_Y.getName())) {
            //cpSpinning = ((DigitalInput)source).getValue();
        //} else if (source.getName().equals(WSInputs.MANIPULATOR_LEFT_JOYSTICK_X.getName())) {
            //cpSpinning = ((DigitalInput)source).getValue();
        // Climb
        } else if (source == selectButton) {
            climbRunning = ((DigitalInput)source).getValue();
        } else if (source == startButton) {
            climbRunning = ((DigitalInput)source).getValue();
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
