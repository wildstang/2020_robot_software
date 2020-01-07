package org.wildstang.year2019.subsystems.drive;

import java.util.ArrayList;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;

public class PathFollower {

    private boolean m_running = false;

    private Path m_path;
    private TalonSRX m_left;
    private TalonSRX m_right;

    private boolean isForwards;

    private SetValueMotionProfile m_mpEnable = SetValueMotionProfile.Disable;
    private MotionProfileStatus m_leftStatus = new MotionProfileStatus();
    private MotionProfileStatus m_rightStatus = new MotionProfileStatus();

    private Notifier m_notifer = new Notifier(new PeriodicRunnable());

    public PathFollower(Path p_path, boolean isForwards, TalonSRX p_left, TalonSRX p_right) {
        m_path = p_path;
        m_left = p_left;
        m_right = p_right;
        this.isForwards = isForwards;

        m_left.changeMotionControlFramePeriod(20);
        m_right.changeMotionControlFramePeriod(20);

        fillPathBuffers(isForwards);
    }

    public void start() {
        // System.out.println("PathFollower.start() called");
        // Thread t = new Thread(this);
        m_running = true;
        // t.start();

        m_notifer.startPeriodic(0.005);

        m_mpEnable = SetValueMotionProfile.Enable;
        m_left.set(ControlMode.MotionProfile, m_mpEnable.value);
        m_right.set(ControlMode.MotionProfile, m_mpEnable.value);

    }

    public void stop() {

        m_running = false;

        /*
         * Let's clear the buffer just in case user decided to disable in the middle of
         * an MP, and now we have the second half of a profile just sitting in memory.
         */
        m_left.clearMotionProfileTrajectories();
        m_right.clearMotionProfileTrajectories();

        /* When we do re-enter motionProfile control mode, stay disabled. */
        m_mpEnable = SetValueMotionProfile.Disable;

        m_left.set(ControlMode.MotionProfile, m_mpEnable.value);
        m_right.set(ControlMode.MotionProfile, m_mpEnable.value);

        m_notifer.stop();
    }

    public void update() {
        // System.out.println("PathFollower.update() called");

        // while (m_running)
        // {
        // System.out.println("PathFollower.run() running");

        m_left.getMotionProfileStatus(m_leftStatus);
        m_right.getMotionProfileStatus(m_rightStatus);

        if (m_leftStatus.hasUnderrun) {
            //System.out.println("Left Talon has buffer underrun");
        }
        if (m_rightStatus.hasUnderrun) {
            //System.out.println("Right Talon has buffer underrun");
        }

        // FIXME: this needs a comment as to why this condition. If we miss an update() during
        // the final trajectory point, do we never set m_running to false?
        if (m_leftStatus.activePointValid && m_leftStatus.isLast) {
            m_running = false;
        }

        // }

    }

    public boolean isActive() {
        return m_running;
    }

    class PeriodicRunnable implements java.lang.Runnable {
        @Override
        public void run() {
            m_left.processMotionProfileBuffer();
            // System.out.println("Top buffer size: " +
            // m_left.getMotionProfileTopLevelBufferCount());
            m_right.processMotionProfileBuffer();
        }
    }

    private void fillPathBuffers(boolean isForwards) {
        fillPathBuffers(m_path.getLeft().getTalonPoints(), m_path.getRight().getTalonPoints(),
                m_path.getLeft().getTrajectoryPoints().length, isForwards);
    }

    private void fillPathBuffers(ArrayList<TrajectoryPoint> leftPoints,
            ArrayList<TrajectoryPoint> rightPoints, int totalCnt, boolean isForwards) {

        /* create an empty point */
        // System.out.println("PathFollower.fillPathBuffers() called");

        /* did we get an underrun condition since last time we checked ? */
        if (m_leftStatus.hasUnderrun) {
            //DriverStation.reportError("Left drive has underrun", false);
            m_left.clearMotionProfileHasUnderrun();
        }
        if (m_rightStatus.hasUnderrun) {
            //DriverStation.reportError("Right drive has underrun", false);
            m_right.clearMotionProfileHasUnderrun();
        }

        /*
         * just in case we are interrupting another MP and there is still buffer points
         * in memory, clear it.
         */
        m_left.clearMotionProfileTrajectories();
        m_right.clearMotionProfileTrajectories();

        /* This is fast since it's just into our TOP buffer */

        for (int i = 0; i < totalCnt; ++i) 
        {
            
                
                m_left.pushMotionProfileTrajectory(leftPoints.get(i));                
                m_right.pushMotionProfileTrajectory(rightPoints.get(i));
            
               
                
            
        }
        

        // System.out.println("PathFollower.fillPathBuffers(): added " +
        // m_left.getMotionProfileTopLevelBufferCount());

    }

}
