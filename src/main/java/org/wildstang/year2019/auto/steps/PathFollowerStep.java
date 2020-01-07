package org.wildstang.year2019.auto.steps;

import java.io.File;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2019.robot.WSSubsystems;
import org.wildstang.year2019.subsystems.drive.Drive;
import org.wildstang.year2019.subsystems.drive.Path;
import org.wildstang.year2019.subsystems.drive.PathFollower;
import org.wildstang.year2019.subsystems.drive.PathReader;
import org.wildstang.year2019.subsystems.drive.Trajectory;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PathFollowerStep extends AutoStep {

    private String m_filePath;
    private Path m_path;
    private Drive m_drive;
    private PathFollower m_pathFollower;
    private boolean isForwards;
    private boolean isLeft;

    private boolean m_started = false;

    public PathFollowerStep(String p_path, boolean isLeft, boolean isForwards) {
        SmartDashboard.putBoolean("Checkpoint 1001 yay", true);
        SmartDashboard.putString("Testing path loading",Filesystem.getDeployDirectory().toString() + "/output/"+p_path);

        m_filePath = Filesystem.getDeployDirectory().toString() + "/output/" + p_path;
        this.isForwards = !isForwards;
        this.isLeft = isLeft;
    }

    @Override
    public void initialize() {

        SmartDashboard.putBoolean("Checkpoint 2002 yay", true);
        m_path = new Path();
        
        File leftFile = new File(m_filePath + "_left.csv");
        File rightFile = new File(m_filePath + "_right.csv");
        if (!isForwards || !isLeft){ // TODO In the event of flipped robot movement, switch which file each side reads
            File holder = rightFile;
            rightFile = leftFile;
            leftFile = holder;
        } 

        Trajectory leftTrajectory;
        Trajectory rightTrajectory;
        leftTrajectory = PathReader.readTrajectory(leftFile,isForwards);
        rightTrajectory = PathReader.readTrajectory(rightFile,isForwards);

        m_path.setLeft(leftTrajectory);
        m_path.setRight(rightTrajectory);

        m_drive = (Drive) Core.getSubsystemManager()
                .getSubsystem(WSSubsystems.DRIVEBASE.getName());

        m_drive.setBrakeMode(true);
        
    }

    @Override
    public void update() {
        if (Drive.autoEStopActivated == true) {
            setFinished(true);
            m_drive.pathCleanup();
        }

        SmartDashboard.putBoolean("Checkpoint 505 yay", true);
        if (!isFinished()) {

            SmartDashboard.putBoolean("Checkpoint 404 yay", true);
            if (!m_started) {
                // TODO: Can next 3 lines be moved to init() ??
                m_drive.setPathFollowingMode();
                m_drive.setPath(m_path, isForwards);
                m_pathFollower = m_drive.getPathFollower();

               SmartDashboard.putBoolean("Checkpoint 101 yay", true);

                m_drive.startFollowingPath();
                m_drive.resetEncoders();
                m_started = true;

                //m_pathFollower.start();
            } else {
                if (m_pathFollower.isActive()) {
                    m_pathFollower.update();

                    SmartDashboard.putBoolean("Checkpoint 202 yay", true);
                } else {
                    m_drive.pathCleanup();
                    setFinished(true);

                    SmartDashboard.putBoolean("Checkpoint 303 yay", true);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Path Follower";
    }

}
