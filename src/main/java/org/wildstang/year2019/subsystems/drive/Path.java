package org.wildstang.year2019.subsystems.drive;

public class Path {

    private Trajectory m_left;
    private Trajectory m_right;

    // Unused for now
    private Trajectory m_smoothPath;

    public Path() {
    }

    public Trajectory getLeft() {
        return m_left;
    }

    public void setLeft(Trajectory p_left) {
        m_left = p_left;
    }

    public Trajectory getRight() {
        return m_right;
    }

    public void setRight(Trajectory p_right) {
        m_right = p_right;
    }

    public Trajectory getSmoothPath() {
        return m_smoothPath;
    }

    public void setSmoothPath(Trajectory p_smoothPath) {
        m_smoothPath = p_smoothPath;
    }

}
