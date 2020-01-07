package org.wildstang.year2017.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsVictor;
import org.wildstang.year2017.robot.WSInputs;
import org.wildstang.year2017.robot.WSOutputs;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake implements Subsystem {
    // add variables here
    private DigitalInput m_intakeButton;
    private WsVictor m_intakeMotor;

    // State that needs to be reset
    private double m_motorSpeed;
    private boolean m_intakeOn = false;
    private boolean m_intakeCurrent = false;
    private boolean m_intakePrev = false;

    private boolean jammed = false;
    private double limit = 50;

    private PowerDistributionPanel pdp;

    @Override
    public void resetState() {
        m_motorSpeed = 1; // Core.getConfigManager().getConfig().getDouble(this.getClass().getName()
        // + ".IntakeMotor", 1);
        m_intakeOn = false;
        m_intakeCurrent = false;
        m_intakePrev = false;
    }

    @Override
    public void selfTest() {
    }

    @Override
    public String getName() {
        return "Intake";
    }

    @Override
    public void init() {
        // Setup any local variables with intial values
        resetState();

        m_intakeMotor = (WsVictor) Core.getOutputManager().getOutput(WSOutputs.INTAKE.getName());

        m_intakeButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.INTAKE_ON.getName());
        m_intakeButton.addInputListener(this);

        pdp = new PowerDistributionPanel();

    }

    @Override
    public void inputUpdate(Input p_source) {
        // Toggle for intake
        if (p_source == m_intakeButton) {
            m_intakeCurrent = m_intakeButton.getValue();

            if (m_intakeCurrent && !m_intakePrev) {
                m_intakeOn = !m_intakeOn;
            }
            m_intakePrev = m_intakeCurrent;
        }
    }

    @Override
    public void update() {

        if (m_intakeOn) {
            double current = pdp.getCurrent(11);

            if (current < 50) {
                m_intakeMotor.setValue(m_motorSpeed);
                SmartDashboard.putBoolean("Intake Jammed", false);
            } else {
                SmartDashboard.putBoolean("Intake Jammed", true);
                m_intakeMotor.setValue(0);
            }

        } else {
            m_intakeMotor.setValue(0);
        }

        SmartDashboard.putBoolean("Intake on", m_intakeOn);
    }

    public boolean intakeState() {
        return m_intakeOn;
    }

}
