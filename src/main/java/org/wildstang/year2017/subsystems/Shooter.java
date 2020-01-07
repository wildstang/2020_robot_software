package org.wildstang.year2017.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsVictor;
import org.wildstang.year2017.robot.CANConstants;
import org.wildstang.year2017.robot.RobotTemplate;
import org.wildstang.year2017.robot.WSInputs;
import org.wildstang.year2017.robot.WSOutputs;
import org.wildstang.year2017.subsystems.shooter.Blender;
import org.wildstang.year2017.subsystems.shooter.Feed;
import org.wildstang.year2017.subsystems.shooter.Flywheel;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter implements Subsystem {
    // Send signals directly from this subsystem
    private LED m_led;

    // Flywheels
    private TalonSRX m_CANFlywheelLeft;
    private TalonSRX m_CANFlywheelRight;

    private Flywheel m_leftFlywheel;
    private Flywheel m_rightFlywheel;

    private double m_targetSpeedLeft;
    private double m_targetSpeedRight;
    private double m_lowLimitSpeed;
    private double m_highLimitSpeed;

    // Feeds
    private WsVictor m_leftFeedVictor;
    private WsVictor m_rightFeedVictor;

    private Feed m_leftFeed;
    private Feed m_rightFeed;

    private WsVictor m_blenderVictor;
    private Blender m_blender;

    // Deadband so nothing happens if joystick is bumped on accident
    private double m_feedDeadBand;
    private double m_feedSpeed;

    // PDP for checking if Feeds are jammed
    private PowerDistributionPanel pdp;

    // Inputs
    private DigitalInput m_flywheelButton;
    private DigitalInput m_overrideButton;
    private AnalogInput m_leftBeltJoystick;
    private AnalogInput m_rightBeltJoystick;

    // ALL variables below here are state that need to be reset in resetState to
    // revert to an initial state
    // For the toggle
    private boolean m_flywheelOn = false;
    private boolean m_shooterCurrent;
    private boolean m_shooterPrev;

    private double m_leftJoyAxis;
    private double m_rightJoyAxis;

    // For checking if the gates should open when flywheels are up to speed
    private boolean readyToShootLeft = false;
    private boolean readyToShootRight = false;

    // Can override the flywheel speed checker and open gates anyway
    private boolean m_shootOverride = false;
    // Limits for range flywheels should be at before opening gates

    // Enumeration variable for SHOOT, REVERSE, and STOP
    private FeedDirection m_leftFeedDirection;
    private FeedDirection m_rightFeedDirection;

    private double m_LF;
    private double m_LP;
    private double m_LI;
    private double m_LD;

    private double m_RF;
    private double m_RP;
    private double m_RI;
    private double m_RD;

    @Override
    public void resetState() {
        m_shootOverride = false;

        m_leftFeedDirection = FeedDirection.STOP;
        m_rightFeedDirection = FeedDirection.STOP;

        readyToShootLeft = false;
        readyToShootRight = false;
        m_leftJoyAxis = 0;
        m_rightJoyAxis = 0;

        m_flywheelOn = false;

        // Toggle state variables
        m_shooterCurrent = false;
        m_shooterPrev = false;

    }

    @Override
    public void selfTest() {
        // DO NOT IMPLELMENT
    }

    @Override
    public String getName() {
        return "Shooter";
    }

    @Override
    public void init() {
        if (true)// RobotTemplate.LOG_STATE)
        {
            Core.getStateTracker().addIOInfo("Left shooter (RPM)", "Shooter", "Input", null);
            Core.getStateTracker().addIOInfo("Right shooter (RPM)", "Shooter", "Input", null);
            Core.getStateTracker().addIOInfo("Left shooter voltage", "Shooter", "Input", null);
            Core.getStateTracker().addIOInfo("Right shooter voltage", "Shooter", "Input", null);
            Core.getStateTracker().addIOInfo("Left shooter current", "Shooter", "Input", null);
            Core.getStateTracker().addIOInfo("Right shooter current", "Shooter", "Input", null);
        }

        // Read config values
        readConfigValues();

        // Flywheels
        // CAN talons
        m_CANFlywheelLeft = new TalonSRX(CANConstants.FLYWHEEL_LEFT_TALON_ID);
        m_CANFlywheelRight = new TalonSRX(CANConstants.FLYWHEEL_RIGHT_TALON_ID);

        configureFlywheelTalons();

        m_leftFlywheel = new Flywheel(m_CANFlywheelLeft, m_targetSpeedLeft);
        m_rightFlywheel = new Flywheel(m_CANFlywheelRight, m_targetSpeedRight);

        // Feeds
        m_leftFeedVictor = (WsVictor) Core.getOutputManager()
                .getOutput(WSOutputs.FEEDER_LEFT.getName());
        m_rightFeedVictor = (WsVictor) Core.getOutputManager()
                .getOutput(WSOutputs.FEEDER_RIGHT.getName());
        m_leftFeed = new Feed(m_leftFeedVictor, m_feedSpeed);
        m_rightFeed = new Feed(m_rightFeedVictor, m_feedSpeed);

        m_blenderVictor = (WsVictor) Core.getOutputManager().getOutput(WSOutputs.BLENDER.getName());
        m_blender = new Blender(m_blenderVictor);

        // PDP
        pdp = new PowerDistributionPanel();

        // Input Listeners
        m_flywheelButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.FLYWHEEL.getName());
        m_flywheelButton.addInputListener(this);

        m_leftBeltJoystick = (AnalogInput) Core.getInputManager()
                .getInput(WSInputs.FEEDER_LEFT.getName());
        m_leftBeltJoystick.addInputListener(this);
        m_rightBeltJoystick = (AnalogInput) Core.getInputManager()
                .getInput(WSInputs.FEEDER_RIGHT.getName());
        m_rightBeltJoystick.addInputListener(this);

        m_overrideButton = (DigitalInput) Core.getInputManager()
                .getInput(WSInputs.OVERRIDE.getName());
        m_overrideButton.addInputListener(this);

        // m_led =
        // (LED)Core.getSubsystemManager().getSubsystem(WSSubsystems.LED.getName());

        resetState();
    }

    private void configureFlywheelTalons() {
        // Configure left talon
        configureFlywheelTalon(m_CANFlywheelLeft, m_LF, m_LP, m_LI, m_LD);

        // Configure right talon
        configureFlywheelTalon(m_CANFlywheelRight, m_RF, m_RP, m_RI, m_RD);
    }

    private void readConfigValues() {
        // Reads values from Ws Config
        m_targetSpeedLeft = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + ".flywheelSpeedLeft", 4700.0);
        m_targetSpeedRight = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + ".flywheelSpeedRight", 4700.0);
        m_lowLimitSpeed = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + ".lowLimitSpeed", 4600.0);
        m_highLimitSpeed = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + ".highLimitSpeed", 5000.0);
        m_feedSpeed = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + ".feedSpeed", 0.8);
        m_feedDeadBand = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + ".feedDeadBand", 0.05);

        m_LF = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".L_F",
                0.0225);
        m_LP = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".L_P",
                0.03);
        m_LI = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".L_I", 0);
        m_LD = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".L_D",
                0.5);

        m_RF = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".R_F",
                0.0225);
        m_RP = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".R_P",
                0.03);
        m_RI = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".R_I", 0);
        m_RD = Core.getConfigManager().getConfig().getDouble(this.getClass().getName() + ".R_D",
                0.5);
    }

    private void configureFlywheelTalon(TalonSRX p_talon, double p_fGain, double p_pGain,
            double p_iGain, double p_dGain) {
        p_talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        p_talon.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 10);
        p_talon.setNeutralMode(NeutralMode.Coast);

        p_talon.setSensorPhase(true);
        p_talon.getSensorCollection().setQuadraturePosition(0, 0);
        p_talon.set(ControlMode.Velocity, 0);

        p_talon.configNominalOutputForward(0);
        p_talon.configNominalOutputReverse(0);
        p_talon.configPeakOutputForward(1);
        p_talon.configPeakOutputReverse(-1);
        // p_talon.setVoltageRampRate(24.0); // Max spinup of 24V/s - start here

        // Set up closed loop PID control gains in slot 0
        p_talon.selectProfileSlot(0, 0);
        p_talon.config_kF(0, p_fGain);
        p_talon.config_kP(0, p_pGain);
        p_talon.config_kI(0, p_iGain);
        p_talon.config_kD(0, p_dGain);
    }

    @Override
    public void inputUpdate(Input p_source) {
        if (p_source == m_flywheelButton) {
            m_shooterCurrent = m_flywheelButton.getValue();
            // Toggle for flywheels
            if (m_shooterCurrent && !m_shooterPrev) {
                m_flywheelOn = !m_flywheelOn;
            }
            m_shooterPrev = m_shooterCurrent;
        }

        else if (p_source == m_leftBeltJoystick) {
            m_leftJoyAxis = m_leftBeltJoystick.getValue();
            if (m_leftJoyAxis > m_feedDeadBand) {
                m_leftFeedDirection = FeedDirection.SHOOT;
            } else if (m_leftJoyAxis < -m_feedDeadBand) {
                m_leftFeedDirection = FeedDirection.REVERSE;
            } else {
                m_leftFeedDirection = FeedDirection.STOP;
            }
        } else if (p_source == m_rightBeltJoystick) {
            m_rightJoyAxis = m_rightBeltJoystick.getValue();
            if (m_rightJoyAxis > m_feedDeadBand) {
                m_rightFeedDirection = FeedDirection.SHOOT;
            } else if (m_rightJoyAxis < -m_feedDeadBand) {
                m_rightFeedDirection = FeedDirection.REVERSE;
            } else {
                m_rightFeedDirection = FeedDirection.STOP;
            }
        } else if (p_source == m_overrideButton) {
            m_shootOverride = !m_shootOverride;
        }

    }

    @Override
    public void update() {
        updateFlywheels();
        updateFeed();

        updateDashboardData();

        if (RobotTemplate.LOG_STATE) {
            Core.getStateTracker().addState("Left shooter (RPM)", "Shooter",
                    m_CANFlywheelLeft.getSelectedSensorVelocity());
            Core.getStateTracker().addState("Right shooter (RPM)", "Shooter",
                    m_CANFlywheelRight.getSelectedSensorVelocity());
            Core.getStateTracker().addState("Left shooter voltage", "Shooter",
                    m_CANFlywheelLeft.getMotorOutputVoltage());
            Core.getStateTracker().addState("Right shooter voltage", "Shooter",
                    m_CANFlywheelRight.getMotorOutputVoltage());
            Core.getStateTracker().addState("Left shooter current", "Shooter",
                    m_CANFlywheelLeft.getOutputCurrent());
            Core.getStateTracker().addState("Right shooter current", "Shooter",
                    m_CANFlywheelRight.getOutputCurrent());
        }
    }

    // Flywheel stuff
    // Turns on the flywheels w/out buttons for auto
    public void turnFlywheelOn() {
        m_flywheelOn = true;
    }

    // Turns off the flywheels w/out buttons for auto
    public void turnFlywheelOff() {
        m_flywheelOn = false;
    }

    // Updates the state of the flywheels based off of the toggle switch and
    // button
    public void updateFlywheels() {
        if (m_flywheelOn) {
            m_leftFlywheel.turnOn();
            m_rightFlywheel.turnOn();
        } else if (!m_flywheelOn) {
            m_leftFlywheel.turnOff();
            m_rightFlywheel.turnOff();
        }
    }

    public boolean isFlywheelOn() {
        return m_flywheelOn;
    }

    public boolean isShooting() {
        return isFlywheelOn() && (m_leftFeedDirection == FeedDirection.SHOOT)
                || (m_rightFeedDirection == FeedDirection.SHOOT);
    }

    public boolean isLeftReadyToShoot() {
        return isReadyToShoot(m_CANFlywheelLeft);
    }

    public boolean isRightReadyToShoot() {
        return isReadyToShoot(m_CANFlywheelRight);
    }

    public boolean isReadyToShoot(TalonSRX p_talon) {
        double speed = p_talon.getSelectedSensorVelocity();

        return (speed >= m_lowLimitSpeed && speed <= m_highLimitSpeed);
    }

    // Feed Stuff
    // Turns on the belts w/out buttons for auto
    public void turnFeedOn() {
        m_leftFeedDirection = FeedDirection.SHOOT;
        m_rightFeedDirection = FeedDirection.SHOOT;
    }

    // Turns off the belts w/out buttons for auto
    public void turnFeedOff() {
        m_leftFeedDirection = FeedDirection.STOP;
        m_rightFeedDirection = FeedDirection.STOP;
    }

    public boolean checkLeftFeedJammed() {
        return m_leftFeed.isJammed(pdp.getCurrent(11));
    }

    public boolean checkRightFeedJammed() {
        return m_rightFeed.isJammed(pdp.getCurrent(4));
    }

    public void updateFeed() {
        // LEFT SIDE
        if (checkLeftFeedJammed()) {
            m_leftFeedDirection = FeedDirection.STOP;
            // m_led.sendLeftFeedJammed();
        }

        // RIGHT SIDE
        if (checkRightFeedJammed()) {
            m_rightFeedDirection = FeedDirection.STOP;
            // m_led.sendLeftFeedJammed();
        }

        runFeedBelt(m_leftFeed, m_leftFeedDirection);
        runFeedBelt(m_rightFeed, m_rightFeedDirection);

        if (m_leftFeedDirection == FeedDirection.SHOOT
                || m_rightFeedDirection == FeedDirection.SHOOT) {
            m_blender.runIn();
        } else if (m_leftFeedDirection == FeedDirection.REVERSE
                || m_rightFeedDirection == FeedDirection.REVERSE) {
            m_blender.runOut();
        } else {
            m_blender.turnOff();
        }
    }

    private void runFeedBelt(Feed p_feed, FeedDirection p_direction) {
        switch (p_direction) {
        case SHOOT:
            p_feed.runForward();
        break;
        case REVERSE:
            p_feed.runBackwards();
        break;
        case STOP:
            p_feed.stop();
        break;
        default:
            p_feed.stop();
        break;
        }
    }

    enum FeedDirection {
        SHOOT,
        REVERSE,
        STOP;
    }

    // Shows speeds and states for testing
    public void updateDashboardData() {
        SmartDashboard.putBoolean("Left flywheel on", m_leftFlywheel.isRunning());
        SmartDashboard.putBoolean("Right flywheel on", m_rightFlywheel.isRunning());

        SmartDashboard.putNumber("Left flywheel speed", m_leftFlywheel.getSpeed());
        SmartDashboard.putNumber("Right flywheel speed", m_rightFlywheel.getSpeed());

        SmartDashboard.putNumber("Left flywheel error", m_CANFlywheelLeft.getClosedLoopError());
        SmartDashboard.putNumber("Right flywheel error", m_CANFlywheelRight.getClosedLoopError());

        SmartDashboard.putBoolean("Left feed jammed", m_leftFeed.isJammed(pdp.getCurrent(11)));
        SmartDashboard.putBoolean("Right feed jammed", m_rightFeed.isJammed(pdp.getCurrent(4)));

        SmartDashboard.putBoolean("Left ready", readyToShootLeft);
        SmartDashboard.putBoolean("Right ready", readyToShootRight);

        // WS config
        SmartDashboard.putNumber("Left flywheel target", m_targetSpeedLeft);
        SmartDashboard.putNumber("Right flywheel target", m_targetSpeedRight);
        SmartDashboard.putNumber("Flywheel low limit", m_lowLimitSpeed);
        SmartDashboard.putNumber("Flywheel high limit", m_highLimitSpeed);
    }

}
