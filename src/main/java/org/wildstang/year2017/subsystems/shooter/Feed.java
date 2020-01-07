package org.wildstang.year2017.subsystems.shooter;

import org.wildstang.hardware.crio.outputs.WsVictor;
import org.wildstang.year2017.subsystems.Shooter;

public class Feed extends Shooter {

    private WsVictor m_victor;
    private double feedSpeed;

    private double limit = 50;
    private boolean jammed = false;

    private boolean ballReady;

    private double m_currentSpeed = 0;
    private static final double RAMP_AMOUNT = 0.1;
    private static final int RAMP_RATE = 3;
    private int m_rampCycles = 0;

    // Creating a feeder object so that both feeder belts can be declared in the
    // Shooter subclass
    // as well as mutated accordingly with the functions below

    public Feed(WsVictor p_victor, double p_speed) {
        m_victor = p_victor;
        feedSpeed = p_speed;
    }

    // This function is setup in the shooter class to determine whether or not
    // the belt is jammed testing if the voltage out to that port is higher than
    // is usual voltage pull
    public boolean isJammed(double p_current) {
        if (p_current > limit) {
            jammed = true;
        } else {
            jammed = false;
        }

        return jammed;
    }

    // This function makes the motors on the belts run in a positive rotation

    public void runForward() {
        if (m_currentSpeed > -feedSpeed) {
            if (m_rampCycles % RAMP_RATE == 0) {
                m_currentSpeed -= RAMP_AMOUNT;
                m_currentSpeed = Math.min(m_currentSpeed, -feedSpeed);
            }
            m_rampCycles++;
        }

        m_victor.setValue(m_currentSpeed);
    }

    // Basically does the same as the function above, but in reverse

    public void runBackwards() {
        if (m_currentSpeed < feedSpeed) {
            if (m_rampCycles % RAMP_RATE == 0) {
                m_currentSpeed += RAMP_AMOUNT;
                m_currentSpeed = Math.max(m_currentSpeed, feedSpeed);
            }
            m_rampCycles++;
        }

        m_victor.setValue(m_currentSpeed);
    }

    // This function turns the motors off

    public void stop() {
        m_currentSpeed = 0;
        m_victor.setValue(m_currentSpeed);
        m_rampCycles = 0;
    }

    // This function may or may not be used. Either way, the purpose of this
    // function is
    // to determine whether or not a ball is ready based on possible sensors
    // (emphasis on possible)

    public boolean isBallReady(boolean p_digitalInput) {
        if (p_digitalInput) {
            ballReady = true;
        } else {
            ballReady = false;
        }
        return ballReady;
    }

    // gets speed for testing.
    public double getSpeed() {
        return m_victor.getValue();
    }

}
