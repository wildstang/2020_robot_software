package org.wildstang.year2017.subsystems.shooter;

import org.wildstang.hardware.crio.outputs.WsVictor;

public class Blender {
    private WsVictor m_victor;
    private double m_speed;

    public Blender(WsVictor p_victor) {
        m_victor = p_victor;
        m_speed = -.80;
    }

    public void runIn() {
        m_victor.setValue(m_speed);
    }

    public void runOut() {
        m_victor.setValue(-m_speed);
    }

    public void turnOff() {
        m_victor.setValue(0);
    }

}
