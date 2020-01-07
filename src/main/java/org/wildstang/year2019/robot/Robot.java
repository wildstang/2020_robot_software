package org.wildstang.year2019.robot;

import org.wildstang.framework.auto.AutoManager;

//import com.sun.management.GarbageCollectionNotificationInfo;
//import com.sun.management.internal.GarbageCollectionNotifInfoCompositeData;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.InputManager;
import org.wildstang.framework.io.inputs.RemoteAnalogInput;
import org.wildstang.framework.timer.WsTimer;
import org.wildstang.hardware.crio.RoboRIOInputFactory;
import org.wildstang.hardware.crio.RoboRIOOutputFactory;
import org.wildstang.year2019.auto.programs.AllTheWayThrough;
import org.wildstang.year2019.auto.programs.ExampleAutoProgram;
import org.wildstang.year2019.auto.programs.CargoShipLeft;
import org.wildstang.year2019.auto.programs.RocketLeft;
import org.wildstang.year2019.subsystems.drive.Drive;
import org.wildstang.year2019.auto.programs.TestPathReader;
import org.wildstang.year2019.auto.programs.Left2056L1;
import org.wildstang.year2019.auto.programs.NewL2056A;
import org.wildstang.year2019.auto.programs.NewL2056B;
import org.wildstang.year2019.auto.programs.NewCSL;
import org.wildstang.year2019.auto.programs.NewR2056B;
import org.wildstang.year2019.auto.programs.NewCSR;
import org.wildstang.year2019.auto.programs.Sandstorm;
import org.wildstang.year2019.auto.programs.Left2056steps.step2;
import org.wildstang.year2019.auto.programs.Left2056steps.step3;
import org.wildstang.year2019.auto.programs.Left2056steps.step4;
import org.wildstang.year2019.auto.programs.Left2056steps.step5;
import org.wildstang.year2019.auto.programs.Left2056steps.step6;
import org.wildstang.year2019.auto.programs.Left2056steps.step1;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.lang.management.GarbageCollectorMXBean;

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

        //AutoManager.getInstance().addProgram(new ExampleAutoProgram());
        //AutoManager.getInstance().addProgram(new AllTheWayThrough());
        //AutoManager.getInstance().addProgram(new TestPathReader());
        //AutoManager.getInstance().addProgram(new CargoShipLeft());
        //AutoManager.getInstance().addProgram(new Left2056L1());
        //AutoManager.getInstance().addProgram(new ExampleAutoProgram());
        AutoManager.getInstance().addProgram(new NewCSL());
        AutoManager.getInstance().addProgram(new NewCSR());
        //AutoManager.getInstance().addProgram(new NewL2056A());
        AutoManager.getInstance().addProgram(new NewL2056B());
        AutoManager.getInstance().addProgram(new NewR2056B());
        AutoManager.getInstance().addProgram(new Sandstorm());
        AutoManager.getInstance().addProgram(new step1());
        AutoManager.getInstance().addProgram(new step2());
        AutoManager.getInstance().addProgram(new step3());
        AutoManager.getInstance().addProgram(new step4());
        AutoManager.getInstance().addProgram(new step5());
        AutoManager.getInstance().addProgram(new step6());
        //AutoManager.getInstance().addProgram(new RocketLeft());
    }

    @Override
    public void disabledInit() {
        System.out.println("Engaging disabled mode.");
        Drive driveBase = ((Drive) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVEBASE.getName()));
        driveBase.setBrakeMode(false);
        driveBase.purgePaths();
    }

    @Override
    public void autonomousInit() {
        Core.getSubsystemManager().resetState();

        Drive driveBase = ((Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVEBASE.getName()));
        driveBase.purgePaths();

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