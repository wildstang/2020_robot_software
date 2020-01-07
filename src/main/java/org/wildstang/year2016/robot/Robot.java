package org.wildstang.year2016.robot;
/*----------------------------------------------------------------------------*/

/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.auto.AutoManager;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.logger.StateLogger;
import org.wildstang.framework.timer.ProfilingTimer;
import org.wildstang.hardware.crio.RoboRIOInputFactory;
import org.wildstang.hardware.crio.RoboRIOOutputFactory;
import org.wildstang.year2016.auto.programs.CornerShot;
import org.wildstang.year2016.auto.programs.CrossingDefense;
import org.wildstang.year2016.auto.programs.DriveAtp1;
import org.wildstang.year2016.auto.programs.DriveAtp10;
import org.wildstang.year2016.auto.programs.DriveAtp2;
import org.wildstang.year2016.auto.programs.DriveAtp3;
import org.wildstang.year2016.auto.programs.DriveAtp4;
import org.wildstang.year2016.auto.programs.DriveAtp5;
import org.wildstang.year2016.auto.programs.DriveAtp6;
import org.wildstang.year2016.auto.programs.DriveAtp7;
import org.wildstang.year2016.auto.programs.DriveAtp8;
import org.wildstang.year2016.auto.programs.DriveAtp9;
import org.wildstang.year2016.auto.programs.FunctionTest;
import org.wildstang.year2016.auto.programs.LowBarOneBall;
import org.wildstang.year2016.auto.programs.VisionTest;
import org.wildstang.year2016.subsystems.DriveBase;
import org.wildstang.year2016.subsystems.Intake;
import org.wildstang.year2016.subsystems.Shooter;

//import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import edu.wpi.first.wpilibj.Watchdog;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

    private static Logger s_log = Logger.getLogger(Robot.class.getName());
    static boolean teleopPerodicCalled = false;

    // private static Image frame;
    private StateLogger m_stateLogger = null;
    private Core m_core = null;

    private boolean exceptionThrown = false;
    private boolean firstRun = true;
    private boolean AutoFirstRun = true;

    CameraServer server;

    @Override
    public void testInit() {

    }

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        startupTimer.startTimingSection();

        m_core = new Core(RoboRIOInputFactory.class, RoboRIOOutputFactory.class);
        m_stateLogger = new StateLogger(Core.getStateTracker());

        // Load the config
        loadConfig();

        // Create application systems
        m_core.createInputs(WSInputs.values());
        // m_core.createInputs(SwerveInputs.values());
        m_core.createOutputs(WSOutputs.values());

        // 1. Add subsystems
        m_core.createSubsystems(WSSubsystems.values());

        // startloggingState();

        // 2. Add Auto programs
        // AutoManager.getInstance().addProgram(new OneBallMoatRampart());
        // AutoManager.getInstance().addProgram(new MotionProfileTest());
        // AutoManager.getInstance().addProgram(new OneBallMoatRampart());
        // AutoManager.getInstance().addProgram(new HarpoonAuto());
        // AutoManager.getInstance().addProgram(new TwoBall());
        AutoManager.getInstance().addProgram(new CrossingDefense());
        // AutoManager.getInstance().addProgram(new SpyShotCross());
        // AutoManager.getInstance().addProgram(new CornerAvoid());
        AutoManager.getInstance().addProgram(new LowBarOneBall());
        // AutoManager.getInstance().addProgram(new LowBarLowGoal());
        AutoManager.getInstance().addProgram(new VisionTest());
        AutoManager.getInstance().addProgram(new CornerShot());
        AutoManager.getInstance().addProgram(new FunctionTest());
        AutoManager.getInstance().addProgram(new DriveAtp1());
        AutoManager.getInstance().addProgram(new DriveAtp2());
        AutoManager.getInstance().addProgram(new DriveAtp3());
        AutoManager.getInstance().addProgram(new DriveAtp4());
        AutoManager.getInstance().addProgram(new DriveAtp5());
        AutoManager.getInstance().addProgram(new DriveAtp6());
        AutoManager.getInstance().addProgram(new DriveAtp7());
        AutoManager.getInstance().addProgram(new DriveAtp8());
        AutoManager.getInstance().addProgram(new DriveAtp9());
        AutoManager.getInstance().addProgram(new DriveAtp10());

        s_log.logp(Level.ALL, this.getClass().getName(), "robotInit", "Startup Completed");
        startupTimer.endTimingSection();

        // server = CameraServer.getInstance();
        // server.setQuality(15);
        // server.setSize(2);
        // server.startAutomaticCapture("cam0");

    }

    private void loadConfig() {
        File configFile;
        String osname = System.getProperty("os.name");
        if (osname.startsWith("Windows")) {
            configFile = new File("./Config/ws_config.txt");
        } else if (osname.startsWith("Mac")) {
            configFile = new File("./Config/ws_config.txt");
        } else {
            configFile = new File("/ws_config.txt");
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(configFile));
            Core.getConfigManager().loadConfig(reader);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    ProfilingTimer durationTimer = new ProfilingTimer("Periodic method duration", 50);
    ProfilingTimer periodTimer = new ProfilingTimer("Periodic method period", 50);
    ProfilingTimer startupTimer = new ProfilingTimer("Startup duration", 1);
    ProfilingTimer initTimer = new ProfilingTimer("Init duration", 1);

    @Override
    public void disabledInit() {
        initTimer.startTimingSection();
        AutoManager.getInstance().clear();

        loadConfig();

        Core.getSubsystemManager().init();

        initTimer.endTimingSection();
        s_log.logp(Level.ALL, this.getClass().getName(), "disabledInit", "Disabled Init Complete");

    }

    @Override
    public void disabledPeriodic() {
        // If we are finished with teleop, finish and close the log file
        ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                .stopStraightMoveWithMotionProfile();
        if (teleopPerodicCalled) {
            m_stateLogger.stop();
        }
        AutoFirstRun = true;
        firstRun = true;
    }

    @Override
    public void autonomousInit() {
        Core.getSubsystemManager().init();

        m_core.setAutoManager(AutoManager.getInstance());
        AutoManager.getInstance().startCurrentProgram();
    }

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
        // Update all inputs, outputs and subsystems

        m_core.executeUpdate();

        if (AutoFirstRun) {
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .resetLeftEncoder();
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .resetRightEncoder();
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .setSuperDriveOverride(true);
            AutoFirstRun = false;
        }
    }

    @Override
    public void teleopInit() {
        // Remove the AutoManager from the Core
        m_core.setAutoManager(null);

        Core.getSubsystemManager().init();

        DriveBase driveBase = ((DriveBase) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVE_BASE.getName()));

        driveBase.stopStraightMoveWithMotionProfile();

        periodTimer.startTimingSection();
    }

    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {
        if (firstRun) {
            ((Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER.getName()))
                    .shooterOverride(false);
            ((Intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE.getName()))
                    .setIntakeOverrideOn(false);
            ((Intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE.getName()))
                    .setShotOverride(false);
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .resetLeftEncoder();
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .resetRightEncoder();
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .setSuperDriveOverride(false);
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .stopStraightMoveWithMotionProfile();
            ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                    .setLeftDrive(0);
            firstRun = false;
        }

        try {
            teleopPerodicCalled = true;

            System.currentTimeMillis();

            // Update all inputs, outputs and subsystems
            m_core.executeUpdate();

            /*
             * try { NIVision.IMAQdxGrab(session, frame, 1);
             * CameraServer.getInstance().setImage(frame); } catch(Exception e){}
             */

            System.currentTimeMillis();
        } catch (Throwable e) {
            SmartDashboard.putString("Exception thrown", e.toString());
            exceptionThrown = true;
            throw e;
        } finally {
            SmartDashboard.putBoolean("ExceptionThrown", exceptionThrown);
        }
    }

    /**
     * This function is called periodically during test mode
     */
    @Override
    public void testPeriodic() {
        // Watchdog.getInstance().feed();
    }
}
