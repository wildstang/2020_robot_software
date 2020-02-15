package org.wildstang.year2020.robot;

import org.wildstang.framework.auto.AutoManager;

//import com.sun.management.GarbageCollectionNotificationInfo;
//import com.sun.management.internal.GarbageCollectionNotifInfoCompositeData;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.InputManager;
import org.wildstang.framework.io.inputs.RemoteAnalogInput;
import org.wildstang.framework.timer.WsTimer;
import org.wildstang.hardware.crio.RoboRIOInputFactory;
import org.wildstang.hardware.crio.RoboRIOOutputFactory;
import org.wildstang.year2020.auto.programs.ExampleAutoProgram;
import org.wildstang.year2020.subsystems.drive.Drive;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.lang.management.GarbageCollectorMXBean;

import org.wildstang.year2020.auto.programs.Ball10;
import org.wildstang.year2020.auto.programs.TrenchCentered;
import org.wildstang.year2020.auto.programs.Prog120;
import org.wildstang.year2020.auto.programs.Prog144;
import org.wildstang.year2020.auto.programs.Prog168;
import org.wildstang.year2020.auto.programs.Prog192;
import org.wildstang.year2020.auto.programs.Prog216;
import org.wildstang.year2020.auto.programs.Prog240;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
    Core core;
    private boolean AutoFirstRun = true;

    /** Nothing to do in the Robot constructor; real setup happens in robotInit. */
    public Robot() {
        super();
    }

    @Override
    public void robotInit() {
        System.out.println("Initializing robot.");

        core = new Core(RoboRIOInputFactory.class, RoboRIOOutputFactory.class);
        core.createInputs(WSInputs.values());
        core.createOutputs(WSOutputs.values());
        core.createSubsystems(WSSubsystems.values());

        AutoManager.getInstance().addProgram(new Ball10());
        AutoManager.getInstance().addProgram(new TrenchCentered());
        AutoManager.getInstance().addProgram(new Prog120());
        AutoManager.getInstance().addProgram(new Prog144());
        AutoManager.getInstance().addProgram(new Prog168());
        AutoManager.getInstance().addProgram(new Prog192());
        AutoManager.getInstance().addProgram(new Prog216());
        AutoManager.getInstance().addProgram(new Prog240());
        
    }

    @Override
    public void disabledInit() {
        System.out.println("Engaging disabled mode.");
        Drive driveBase = ((Drive) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVEBASE.getName()));
        //FalconDrive falconDrive = ((FalconDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.FALCONDRIVE.getName()));
        driveBase.setBrakeMode(false);
        driveBase.purgePaths();
    }

    @Override
    public void autonomousInit() {
        Core.getSubsystemManager().resetState();

        Drive driveBase = ((Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVEBASE.getName()));
        driveBase.purgePaths();
        //FalconDrive falconDrive = ((FalconDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.FALCONDRIVE.getName()));
        //falconDrive.purgePaths();

        SmartDashboard.putBoolean("Checkpoint 707 yay", true);

        core.setAutoManager(AutoManager.getInstance());
        AutoManager.getInstance().startCurrentProgram();
    }

    @Override
    public void teleopInit() {
        System.out.println("Engaging teleoperation mode.");
        Core.getSubsystemManager().resetState();

        Drive driveBase = ((Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVEBASE.getName()));
        driveBase.purgePaths();
        driveBase.setOpenLoopDrive();
        driveBase.setBrakeMode(false);
        //FalconDrive falconDrive = ((FalconDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.FALCONDRIVE.getName()));
        //falconDrive.purgePaths();
        //falconDrive.setOpenLoopDrive();
        //falconDrive.setBrakeMode(false);
    }

    @Override
    public void testInit() {
        System.out.println("Engaging test mode.");
    }

    @Override
    public void robotPeriodic() {


        core.executeUpdate();

        // Empty the state tracker so we don't OOM out
        // TODO: figure out what this thing is and why
        Core.getStateTracker().getStateList();

        /* This code is used to debug garbage collection. Note that it has its own core.executeUpdate();
        WsTimer timer = new WsTimer();
        timer.start();
        core.executeUpdate();
        timer.stop();
        double time = timer.get();

        System.out.println(time);

        List<GarbageCollectorMXBean> GCs = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gc : GCs) {
            System.out.println(gc.getCollectionCount());
            System.out.println(gc.getCollectionTime());
        }
        */
    }

    @Override
    public void disabledPeriodic() {
        //Drive drive = (Drive) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVEBASE.getName());
        //drive.setFullBrakeMode();
        resetRobotState();

        Drive driveBase = ((Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVEBASE.getName()));
        driveBase.purgePaths();
        //FalconDrive falconDrive = ((FalconDrive) Core.getSubsystemManager().getSubsystem(WSSubsystems.FALCONDRIVE.getName()));
        //falconDrive.purgePaths();
    }
    
    private void resetRobotState() {
        AutoFirstRun = true;
    }

    @Override
    public void autonomousPeriodic() {
        core.executeUpdate();

        //System.out.println("Checkpoint 808 yay");

        double time = System.currentTimeMillis();
        //SmartDashboard.putNumber("Cycle Time", time - oldTime);
        //oldTime = time;

        if (AutoFirstRun) {
            AutoFirstRun = false;
        }
    }

    @Override
    public void teleopPeriodic() {
        try {

            // Update all inputs, outputs and subsystems
            
            long start = System.currentTimeMillis();
            core.executeUpdate();
            long end = System.currentTimeMillis();

            
            SmartDashboard.putNumber("Cycle Time", (end - start));
        } catch (Throwable e) {
            SmartDashboard.putString("Last error", "Exception thrown during teleopPeriodic");
            SmartDashboard.putString("Exception thrown", e.toString());
            //exceptionThrown = true;
            throw e;
        } finally {
            SmartDashboard.putBoolean("ExceptionThrown",true);
        }
    }

    @Override
    public void testPeriodic() {
    }
}