package org.wildstang.year2016.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsI2COutput;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSOutputs;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class LED implements Subsystem {

    // Sent states
    boolean autoDataSent = false;
    boolean m_newDataAvailable = false;
    boolean disableDataSent = false;

    private String m_name;

    WsI2COutput m_ledOutput;

    boolean m_antiTurbo;
    boolean m_turbo;
    boolean m_normal;
    boolean m_shooter;
    boolean m_intake;

    /*
     * | Function | Cmd | PL 1 | PL 2 | -------------------------------------- |
     * Shoot | 0x03 | 0x21 | 0x12 | | Climb | 0x06 | 0x11 | 0x12 | not currently in
     * code 4 arduino | Autonomous | 0x02 | 0x13 | 0x14 | | Red Alliance | 0x04 |
     * 0x52 | 0x01 | | Blue Alliance | 0x47 | 0x34 | 0x26 | | Intake | 0x11 | 0x57 |
     * 0x49 | | Turbo | 0x05 | 0x20 | 0x32 | | Anti-Turbo | 0x06 | 0x09 | 0x08 |
     *
     * Send sequence once, no spamming the Arduino.
     */

    // Reused commands from year to year
    LedCmd autoCmd = new LedCmd(0x02, 0x13, 0x14);
    LedCmd redCmd = new LedCmd(0x04, 0x52, 0x01);
    LedCmd blueCmd = new LedCmd(0x47, 0x34, 0x26);
    LedCmd shooter = new LedCmd(0x03, 0x21, 0x12);
    LedCmd intake = new LedCmd(0x11, 0x57, 0x49);
    // *Defaults to alliance* LedCmd disabled = new LedCmd(0x33, 0x55, 0x59);

    LedCmd turboCmd = new LedCmd(0x05, 0x20, 0x32);
    LedCmd antiturboCmd = new LedCmd(0x06, 0x09, 0x08);

    public LED() {
        m_name = "LED";
    }

    @Override
    public void init() {
        autoDataSent = false;
        disableDataSent = false;
        m_ledOutput = (WsI2COutput) Core.getOutputManager().getOutput(WSOutputs.LED.getName());

        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_5.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_8.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_6.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_7.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_8.getName()).addInputListener(this);
    }

    @Override
    public void update() {
        // Get all inputs relevant to the LEDs
        boolean isRobotEnabled = DriverStation.getInstance().isEnabled();
        boolean isRobotTeleop = DriverStation.getInstance().isOperatorControl();
        boolean isRobotAuton = DriverStation.getInstance().isAutonomous();

        DriverStation.Alliance alliance = DriverStation.getInstance().getAlliance();

        m_normal = !(m_antiTurbo || m_turbo);

        if (isRobotEnabled) {
            if (isRobotTeleop) {
                if (m_newDataAvailable) {
                    if (m_antiTurbo) {
                        m_ledOutput.setValue(antiturboCmd.getBytes());
                    } else if (m_turbo) {
                        m_ledOutput.setValue(turboCmd.getBytes());
                    } else if (m_normal) {
                        switch (alliance) {
                        case Red: {
                            if (!disableDataSent) {
                                m_ledOutput.setValue(redCmd.getBytes());
                                disableDataSent = true;
                            }
                        }
                        break;

                        case Blue: {
                            if (!disableDataSent) {
                                m_ledOutput.setValue(blueCmd.getBytes());
                                disableDataSent = true;
                            }
                        }
                        break;

                        default: {
                            disableDataSent = false;
                        }
                        break;
                        }

                        if (m_shooter) {
                            m_ledOutput.setValue(shooter.getBytes());
                        }

                        if (m_intake) {
                            m_ledOutput.setValue(intake.getBytes());
                        }

                    }
                    m_newDataAvailable = false;
                }
                SmartDashboard.putBoolean("Turbo", m_turbo);
                SmartDashboard.putBoolean("Antiturbo", m_antiTurbo);
                SmartDashboard.putBoolean("Shooter", m_shooter);
                SmartDashboard.putBoolean("Intake", m_intake);
            } else if (isRobotAuton) {
                if (!autoDataSent) {
                    m_ledOutput.setValue(autoCmd.getBytes());
                    autoDataSent = true;
                }
            } else {
                switch (alliance) {
                case Red: {
                    if (!disableDataSent) {
                        m_ledOutput.setValue(redCmd.getBytes());
                        disableDataSent = true;
                    }
                }
                break;

                case Blue: {
                    if (!disableDataSent) {
                        m_ledOutput.setValue(blueCmd.getBytes());
                        disableDataSent = true;
                    }
                }
                break;

                default: {
                    disableDataSent = false;
                }
                break;
                }
            }
        }
    }

    @Override
    public void inputUpdate(Input source) {
        if (source.getName().equals(WSInputs.DRV_BUTTON_5.getName())) {
            m_antiTurbo = ((DigitalInput) source).getValue();
        } else if (source.getName().equals(WSInputs.DRV_BUTTON_8.getName())) {
            m_turbo = ((DigitalInput) source).getValue();
        }

        if (source.getName().equals(WSInputs.MAN_BUTTON_6.getName())) {
            m_shooter = ((DigitalInput) source).getValue();
        }

        if (source.getName().equals(WSInputs.MAN_BUTTON_7.getName())) {
            m_intake = ((DigitalInput) source).getValue();
        }

        if (source.getName().equals(WSInputs.MAN_BUTTON_8.getName())) {
            m_shooter = ((DigitalInput) source).getValue();
        }

        m_newDataAvailable = true;
    }

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return m_name;
    }

    public static class LedCmd {

        byte[] dataBytes = new byte[5];

        public LedCmd(int command, int payloadByteOne, int payloadByteTwo) {

            dataBytes[0] = (byte) command;
            dataBytes[1] = (byte) payloadByteOne;
            dataBytes[2] = (byte) payloadByteTwo;
            // Extremely fast and cheap data confirmation algorithm
            dataBytes[3] = (byte) (~dataBytes[1]);
            dataBytes[4] = (byte) (~dataBytes[2]);
        }

        byte[] getBytes() {
            return dataBytes;
        }
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }
}
