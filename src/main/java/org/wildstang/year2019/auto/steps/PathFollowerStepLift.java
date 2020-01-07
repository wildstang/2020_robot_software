// package org.wildstang.year2019.auto.steps;

// import java.io.File;

// import org.wildstang.framework.auto.steps.AutoStep;
// import org.wildstang.framework.core.Core;
// import org.wildstang.year2019.robot.WSSubsystems;
// import org.wildstang.year2019.subsystems.drive.Drive;
// import org.wildstang.year2019.subsystems.lift.superlift;
// import org.wildstang.year2019.subsystems.drive.Path;
// import org.wildstang.year2019.subsystems.drive.PathFollower;
// import org.wildstang.year2019.subsystems.drive.PathReader;
// import org.wildstang.year2019.subsystems.drive.Trajectory;

// import edu.wpi.first.wpilibj.DriverStation;
// import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

// import org.wildstang.framework.timer.WsTimer;;

// public class PathFollowerStepLift extends AutoStep {

//     private String m_filePath;
//     private Path m_path;
//     private Drive m_drive;
//     private superlift lift;
//     private PathFollower m_pathFollower;
//     private boolean isForwards;
//     private double delay;
//     private int liftLevel;
//     private boolean pathFinished = false;
//     private boolean liftFinished = false;
//     private WsTimer timer = new WsTimer();

//     private boolean m_started = false;

//     public PathFollowerStepLift(String p_path, boolean isForwards, double delay, int liftLevel) {
//         m_filePath = p_path;
//         this.isForwards = isForwards;
//         this.delay = delay;
//         this.liftLevel = liftLevel;
//     }

//     @Override
//     public void initialize() {
//         m_path = new Path();
//         File leftFile = new File(m_filePath + ".left");
//         File rightFile = new File(m_filePath + ".right");
//         Trajectory leftTrajectory;
//         Trajectory rightTrajectory;

//         leftTrajectory = PathReader.readTrajectory(leftFile);
//         rightTrajectory = PathReader.readTrajectory(rightFile);

//         m_path.setLeft(leftTrajectory);
//         m_path.setRight(rightTrajectory);

//         m_drive = (Drive) Core.getSubsystemManager()
//                 .getSubsystem(WSSubsystems.DRIVEBASE.getName());
//         lift = (superlift) Core.getSubsystemManager().getSubsystem(WSSubsystems.LIFT.getName());

//         timer.start();
        
//     }

//     @Override
//     public void update() {
//         if (timer.hasPeriodPassed(delay)){
//             liftFinished = lift.autoLift(liftLevel);
//         }
//         if (!isFinished()) {
//             if (!m_started) {
//                 // TODO: Can next 3 lines be moved to init() ??
//                 m_drive.setPathFollowingMode();
//                 m_drive.setPath(m_path, isForwards);
//                 m_pathFollower = m_drive.getPathFollower();

//                 m_drive.startFollowingPath();
//                 m_drive.resetEncoders();
//                 m_started = true;
//             } else {
//                 if (m_pathFollower.isActive()) {
//                     m_pathFollower.update();
//                 } else {
//                     m_drive.pathCleanup();
//                     pathFinished = true;;
//                 }
//             }
//         }
//         if (pathFinished && liftFinished) setFinished(true);
//     }

//     @Override
//     public String toString() {
//         return "Path Follower";
//     }

// }
