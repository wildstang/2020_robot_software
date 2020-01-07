package org.wildstang.year2017.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsSolenoid;
import org.wildstang.year2017.robot.WSInputs;
import org.wildstang.year2017.robot.WSOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gear implements Subsystem {
    // add variables here

    private boolean m_gearPrev;
    private boolean m_gearCurrent;
    private boolean m_receiveGear;

    private boolean m_doorPrev;
    private boolean m_doorCurrent;
    private boolean m_doorOpen;

    private boolean m_receivePrev;
    private boolean m_receiveCurrent;

    private boolean m_deliverPrev;
    private boolean m_deliverCurrent;
    private boolean m_deliverGear;

    private DigitalInput m_tiltButton;
    private DigitalInput m_doorButton;
    private DigitalInput m_receiveButton;
    private DigitalInput m_deliverButton;

    private WsSolenoid m_tiltSolenoid;
    private WsSolenoid m_doorSolenoid;

    private static final boolean DOOR_OPEN = true;
    private static final boolean GEAR_BACK = true;

    private static final boolean DOOR_CLOSED = !DOOR_OPEN;
    private static final boolean GEAR_FORWARD = !GEAR_BACK;

    @Override
    public void resetState() {
        m_doorOpen = DOOR_CLOSED;
        m_receiveGear = GEAR_FORWARD;

        m_deliverCurrent = false;
        m_deliverPrev = false;
        m_deliverGear = false;

        m_gearPrev = false;
        m_gearCurrent = false;
        m_doorPrev = false;
        m_doorCurrent = false;
    }

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return "Gear";
    }

    @Override
    public void init() {
        // Register the sensors that this subsystem wants to be notified about
        m_tiltButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.GEAR_TILT_BUTTON.getName());
        m_tiltButton.addInputListener(this);

        m_doorButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.GEAR_HOLD_BUTTON.getName());
        m_doorButton.addInputListener(this);

        m_receiveButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.GEAR_RECEIVE.getName());
        m_receiveButton.addInputListener(this);

        m_deliverButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.GEAR_DELIVER.getName());
        m_deliverButton.addInputListener(this);

        m_tiltSolenoid = (WsSolenoid) Core.getOutputManager()
                .getOutput(WSOutputs.GEAR_TILT.getName());
        m_doorSolenoid = (WsSolenoid) Core.getOutputManager()
                .getOutput(WSOutputs.GEAR_HOLD.getName());

        resetState();
    }

    @Override
    public void inputUpdate(Input source) {
        // This section reads the input sensors and places them into local variables
        if (source == m_tiltButton) {
            m_gearCurrent = m_tiltButton.getValue();

            if (m_gearCurrent && !m_gearPrev) {
                m_receiveGear = !m_receiveGear;
            }
            m_gearPrev = m_gearCurrent;
            m_deliverGear = false;
        }

        if (source == m_doorButton) {
            m_doorCurrent = m_doorButton.getValue();

            if (m_doorCurrent && !m_doorPrev) {
                m_doorOpen = !m_doorOpen;
            }
            m_doorPrev = m_doorCurrent;
            m_deliverGear = false;
        }
        if (source == m_receiveButton) {
            m_receiveCurrent = m_receiveButton.getValue();

            if (m_receiveCurrent && !m_receivePrev) {
                m_doorOpen = DOOR_OPEN;
                m_receiveGear = GEAR_BACK;
            }
            m_receivePrev = m_receiveCurrent;
            m_deliverGear = false;
        }
        if (source == m_deliverButton) {
            m_deliverCurrent = m_deliverButton.getValue();

            if (m_deliverCurrent && !m_deliverPrev) {
                // Toggle deliver state
                m_deliverGear = !m_deliverGear;
            }

            if (m_deliverGear) {
                deliverGear();
            } else {
                // Close doors
                // closeDoor();
            }
            m_deliverPrev = m_deliverCurrent;

        }
    }

    @Override
    public void update() {
        m_tiltSolenoid.setValue(m_receiveGear);
        m_doorSolenoid.setValue(m_doorOpen);

        SmartDashboard.putBoolean("Gear door open", m_doorOpen);
        SmartDashboard.putBoolean("Gear back", m_receiveGear);
    }

    public void openDoor() {
        m_doorOpen = DOOR_OPEN;
    }

    public void closeDoor() {
        m_doorOpen = DOOR_CLOSED;
    }

    public void tiltGearBack() {
        m_receiveGear = GEAR_BACK;
    }

    public void tiltGearForward() {
        m_receiveGear = GEAR_FORWARD;
    }

    public void deliverGear() {
        m_receiveGear = GEAR_FORWARD;
        m_doorOpen = DOOR_OPEN;
    }
}
