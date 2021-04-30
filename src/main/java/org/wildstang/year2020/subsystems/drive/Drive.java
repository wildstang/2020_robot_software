package org.wildstang.year2020.subsystems.drive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
//import org.wildstang.framework.logger.StateTracker;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2020.robot.CANConstants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This subsystem controls the drive wheels so that the robot can move around.
 * 
 * TODO: factor input management and drive logic into separate classes somehow.
 * 
 * TODO: Factor the helper classes into core framework
 */
public class Drive implements Subsystem {

    /**
     * Status frame period controls how frequently the TalonSRX reports back with
     * its sensor status over the CANBus. This constant is in milliseconds.
     */
    // private static final int STATUS_FRAME_PERIOD = 10;
    // private static final double NEUTRAL_DEADBAND = 0.001;
    private static final int TIMEOUT = -1; // milliseconds

    // Parameterizing over left and right makes motor setup code DRYer
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int[] SIDES = { LEFT, RIGHT };
    private static final String[] SIDE_NAMES = { "left", "right" };
    private static final int[] MASTER_IDS = { CANConstants.LEFT_DRIVE_TALON, CANConstants.RIGHT_DRIVE_TALON };
    private static final int[][] FOLLOWER_IDS = { CANConstants.LEFT_DRIVE_TALON_FOLLOWER,
            CANConstants.RIGHT_DRIVE_TALON_FOLLOWER };
    /** Left and right Talon master controllers */
    private TalonFX[] masters = new TalonFX[2];
    private TalonFX[][] followers = new TalonFX[2][1];

    public static boolean autoEStopActivated = false;

    /** Input to steer robot */
    private AnalogInput headingInput;
    /** Input to control forward-backward movement */
    private AnalogInput throttleInput;
    /** Button to control base lock mode */
    private DigitalInput baseLockInput;
    /** Button to control quick turn mode */
    private AnalogInput quickTurnInput;
    /** Button to control anti-turbo mode */
    private DigitalInput antiTurboInput;
    // private AnalogInput turboInput;
    private AnalogInput intake;

    /**
     * Keeps track of what kind of drive we're doing (e.g. cheesy drive vs path vs
     * magic)
     */
    private DriveType driveMode;

    /** Counter used to space out updates in update() method */
    private int updateCounter;

    /**
     * This PathFollower helper activates when we're in path mode to follow paths
     */
    private PathFollower pathFollower;

    /** The Cheesy helper calculates the cheesy drive strategy */
    private CheesyDriveHelper cheesyHelper = new CheesyDriveHelper();

    // While this is really a temporary variable, declared here to prevent
    // constant stack allocation.
    // TODO: Determine whether this is premature optimization.
    private DriveSignal driveSignal;

    //////////////////////////////////////////////////////
    // Commanded values
    /** The throttle value currently being commanded. */
    private double commandThrottle;
    /** The heading value currently being commanded. */
    private double commandHeading;
    /** True iff quick-turn is currently commanded. */
    private double commandQuickTurn;
    /** True iff raw mode is currently commanded. */
    private boolean commandRawMode = false;
    /** True iff antiturbo is currently commanded. */
    private boolean commandAntiTurbo = false;

    // private double turboPower;

    private boolean isQuick = false;

    ///////////////////////////////////////////////////////////
    // PUBLIC METHODS

    public Drive() {
    }

    @Override
    public void init() {
        // TODO: set up logging DONE
        Core.getStateTracker().addIOInfo("Left speed (RPM)", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Right speed (RPM)", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Left output", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Right output", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Left 1 current", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Left 2 current", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Right 1 current", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Right 2 current", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Left 1 voltage", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Left 2 voltage", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Right 1 voltage", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Right 2 voltage", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Drive heading", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Drive throttle", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Vision distance", "Drive", "Input", null);
        Core.getStateTracker().addIOInfo("Vision correction", "Drive", "Input", null);

        initMotorControllers();
        initInputs();
        resetState();
    }

    @Override
    public void resetState() {
        resetEncoders();
        setBrakeMode(false);
        setOpenLoopDrive();
        setMotorSpeeds(new DriveSignal(0, 0), 1.0);
        setThrottle(0);
        setHeading(0);
        commandQuickTurn = 0.0;
        commandRawMode = false;
        commandAntiTurbo = false;
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == throttleInput) {
            setThrottle(-throttleInput.getValue());
        } else if (source == headingInput) {
            setHeading(-headingInput.getValue());
        } else if (Math.abs(quickTurnInput.getValue()) > 0.75) {
            commandQuickTurn = quickTurnInput.getValue();
            isQuick = true;
        } else if (source == antiTurboInput) {
            commandAntiTurbo = antiTurboInput.getValue();
            // } else if (source == turboInput){
            // turboPower = Math.abs(turboInput.getValue());
        } else if (source == baseLockInput) {
            commandRawMode = baseLockInput.getValue();
            if (commandRawMode) {
                setFullBrakeMode();
            } else {
                setOpenLoopDrive();
            }
        } else {
            isQuick = false;
        }
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        if (updateCounter % 10 == 0) {
            SmartDashboard.putNumber("commandThrottle", commandThrottle);
            SmartDashboard.putNumber("commandHeading", commandHeading);
            SmartDashboard.putString("Drive mode", driveMode.name());
        }
        switch (driveMode) {
        case PATH:
            SmartDashboard.putNumber("master left", masters[LEFT].getMotorOutputPercent());
            SmartDashboard.putNumber("master right", masters[RIGHT].getMotorOutputPercent());
            SmartDashboard.putNumber("master left velocity",
                    masters[LEFT].getSensorCollection().getIntegratedSensorVelocity());
            SmartDashboard.putNumber("master right velocity",
                    masters[RIGHT].getSensorCollection().getIntegratedSensorVelocity());
            break;
        case CHEESY:
            double effectiveThrottle = commandThrottle;
            if (commandAntiTurbo) {
                effectiveThrottle = commandThrottle * DriveConstants.ANTI_TURBO_FACTOR;
            }
            SmartDashboard.putNumber("Quick Turn", commandQuickTurn);
            driveSignal = cheesyHelper.cheesyDrive(effectiveThrottle, commandHeading, isQuick);
            SmartDashboard.putNumber("driveSignal.left", driveSignal.leftMotor);
            SmartDashboard.putNumber("driveSignal.right", driveSignal.rightMotor);
            setMotorSpeeds(driveSignal, 1.0);
            SmartDashboard.putNumber("master left velocity",
                    masters[LEFT].getSensorCollection().getIntegratedSensorVelocity());
            SmartDashboard.putNumber("master right velocity",
                    masters[RIGHT].getSensorCollection().getIntegratedSensorVelocity());
            // if (commandAntiTurbo) setMotorSpeeds(driveSignal,
            // DriveConstants.ANTI_TURBO_FACTOR);
            // else setMotorSpeeds(driveSignal, (1-DriveConstants.TURBO_FACTOR) +
            // turboPower*DriveConstants.TURBO_FACTOR);
            break;
        case FULL_BRAKE:
            break;
        case RAW:
        default:
            driveSignal = new DriveSignal(commandThrottle, commandThrottle);
            break;
        }
        // SensorCollection leftEncoder = masters[LEFT].getSensorCollection();
        SmartDashboard.putNumber("Left Encoder", masters[LEFT].getSensorCollection().getIntegratedSensorPosition()); // leftEncoder.getQuadraturePosition()
        // SensorCollection rightEncoder = masters[RIGHT].getSensorCollection();
        SmartDashboard.putNumber("Right Encoder", masters[RIGHT].getSensorCollection().getIntegratedSensorPosition()); // rightEncoder.getQuadraturePosition()

        updateCounter += 1;
    }

    @Override
    public String getName() {
        return "Drive Base";
    }

    /** Reset drive encoders back to zero */
    public void resetEncoders() {
        for (TalonFX master : masters) {
            master.setSelectedSensorPosition(0, 0, 10);
        }
    }

    /**
     * Set brake mode when in neutral for all drive motors: true to brake, false to
     * coast
     */
    public void setBrakeMode(boolean brake) {
        NeutralMode mode = brake ? NeutralMode.Brake : NeutralMode.Coast;
        for (int side : SIDES) {
            masters[side].setNeutralMode(mode);
            for (TalonFX follower : followers[side]) {
                follower.setNeutralMode(mode);
            }
        }
    }

    /** Active stop --- stop drive motion immediately. */
    public void setFullBrakeMode() {
        stopPathFollowing();
        // Set talons to hold their current position
        if (driveMode != DriveType.FULL_BRAKE) {
            // Set up Talons to hold their current position as close as possible
            for (TalonFX master : masters) {
                master.selectProfileSlot(DrivePID.BASE_LOCK.slot, 0);
                master.set(ControlMode.Position, master.getSelectedSensorPosition());
            }
            driveMode = DriveType.FULL_BRAKE;
            setBrakeMode(true);
        }
    }

    /** Begin to follow the given path. */
    public void setPath(Path p_path, boolean isForwards) {
        if (pathFollower != null) {
            if (pathFollower.isActive()) {
                throw new IllegalStateException("One path is already active!");
            }
        }
        pathFollower = new PathFollower(p_path, isForwards, masters[LEFT], masters[RIGHT]);
    }

    // FIXME this is an abstraction violation to freely share the path follower
    public PathFollower getPathFollower() {
        return pathFollower;
    }

    public void startFollowingPath() {
        if (pathFollower == null) {
            throw new IllegalStateException("No path set");
        }
        if (pathFollower.isActive()) {
            throw new IllegalStateException("Path is already active");
        }
        pathFollower.start();
    }

    /** Stop following and clean up path. FIXED? */
    public void pathCleanup() {
        if (pathFollower != null) {
            pathFollower.stop();
            pathFollower = null;
        }
    }

    public void abortFollowingPath() {
        if (pathFollower != null) {
            pathFollower.stop();
        }
    }

    /** Switch to cheesy drive. */
    public void setOpenLoopDrive() {
        stopPathFollowing();
        driveMode = DriveType.CHEESY;
        setBrakeMode(false);
    }

    public void setHeading(double heading) {
        this.commandHeading = heading;
    }

    public void setThrottle(double throttle) {
        this.commandThrottle = throttle;
    }

    public void setQuickTurn(boolean quickTurn) {
        this.commandQuickTurn = 0.0;
    }

    /**
     * TODO: Description of what this method should do goes here
     */
    public double getRightSensorValue() {
        return masters[RIGHT].getSensorCollection().getIntegratedSensorPosition();
    }

    /**
     * Clears motion profile trajectories in talon buffers (to be done before auto
     * and teleop)
     */
    public void purgePaths() {
        masters[LEFT].clearMotionProfileTrajectories();
        masters[RIGHT].clearMotionProfileTrajectories();
    }

    /////////////////////////////////////////////////////////
    // PRIVATE METHODS

    /** Change our motor settings to follow a path */

    public void setPathFollowingMode() {
        driveMode = DriveType.PATH;
        // Configure motor controller modes for path following
        masters[LEFT].set(ControlMode.MotionProfile, 0);
        masters[LEFT].selectProfileSlot(DrivePID.PATH.slot, 0);
        masters[RIGHT].set(ControlMode.MotionProfile, 0);
        masters[RIGHT].selectProfileSlot(DrivePID.PATH.slot, 0);
        // Use brake mode to stop quickly at end of path, since Talons will put
        // output to neutral
        // setBrakeMode(true);
    }

    /** Set up our input members and subscribe to inputUpdate events */
    private void initInputs() {
        headingInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_X);
        headingInput.addInputListener(this);
        throttleInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
        throttleInput.addInputListener(this);
        quickTurnInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_RIGHT.getName());
        quickTurnInput.addInputListener(this);
        antiTurboInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_SHOULDER_RIGHT.getName());
        antiTurboInput.addInputListener(this);
        baseLockInput = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_UP.getName());
        baseLockInput.addInputListener(this);
        // turboInput = (AnalogInput)
        // Core.getInputManager().getInput(WSInputs.DRIVER_TRIGGER_LEFT.getName());
        // turboInput.addInputListener(this);
    }

    /** Initialize all drive base motor controllers. */
    private void initMotorControllers() /* throws CoreUtils.CTREException */ {
        for (int side : SIDES) {
            masters[side] = new TalonFX(MASTER_IDS[side]);
            initMaster(side, masters[side]);
            for (int i = 0; i < FOLLOWER_IDS[side].length; i++) {
                followers[side][i] = new TalonFX(FOLLOWER_IDS[side][i]);
                initFollower(side, followers[side][i]);
            }
        }
    }

    /**
     * @param side   Which side (LEFT or RIGHT) this is master for
     * @param master The WSTalonSRX object to set up
     */
    private void initMaster(int side, TalonFX master)  {
        master.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, TIMEOUT);
        master.enableVoltageCompensation(true);
        //master.configContinuousCurrentLimit(50);
        //master.configPeakCurrentLimit(100);
        master.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 50, 100, 1.0));
        if (side == LEFT) {
            master.setInverted(DriveConstants.LEFT_DRIVE_INVERTED);
            master.setSensorPhase(DriveConstants.LEFT_DRIVE_SENSOR_PHASE);
        } else {
            master.setInverted(DriveConstants.RIGHT_DRIVE_INVERTED);
            master.setSensorPhase(DriveConstants.RIGHT_DRIVE_SENSOR_PHASE);
        }
        // Configure output to range from full-forward to full-reverse.
        /* CoreUtils.checkCTRE */master.configNominalOutputForward(0, TIMEOUT);
        /* CoreUtils.checkCTRE */master.configNominalOutputReverse(0, TIMEOUT);
        /* CoreUtils.checkCTRE */master.configPeakOutputForward(+1.0, TIMEOUT);
        /* CoreUtils.checkCTRE */master.configPeakOutputReverse(-1.0, TIMEOUT);
        // Load all the PID settings.
        for (DrivePID pid : DrivePID.values()) {
            master.config_kF(pid.slot, pid.k.f);
            master.config_kP(pid.slot, pid.k.p);
            master.config_kI(pid.slot, pid.k.i);
            master.config_kD(pid.slot, pid.k.d);
        }
        // Special case for base lock: we widen the deadband
        /* CoreUtils.checkCTRE */master.configAllowableClosedloopError(DrivePID.BASE_LOCK.slot,
                DriveConstants.BRAKE_MODE_ALLOWABLE_ERROR);
        // Coast is a reasonable default neutral mode. TODO: is it really?
        master.setNeutralMode(NeutralMode.Coast);
        TalonFXConfiguration master_config = new TalonFXConfiguration();
        master.getAllConfigs(master_config, TIMEOUT);
    }

    private void initFollower(int side, TalonFX follower) {
        TalonFX master = masters[side];
        if (side == LEFT) {
            follower.setInverted(DriveConstants.LEFT_DRIVE_INVERTED);
        } else {
            follower.setInverted(DriveConstants.RIGHT_DRIVE_INVERTED);
        }
        follower.follow(master);
        follower.setNeutralMode(NeutralMode.Coast);
        follower.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 50, 100, 1.0));
        //follower.configPeakCurrentLimit(60);
    }

    private void stopPathFollowing() {
        if (driveMode == DriveType.PATH) {
            abortFollowingPath();
            pathCleanup();
        }
    }

    private void setMotorSpeeds(DriveSignal speeds, double modifier) {
        SmartDashboard.putNumber("2/10 testing", speeds.leftMotor*modifier);
        masters[LEFT].set(ControlMode.PercentOutput, speeds.leftMotor*modifier);
        masters[RIGHT].set(ControlMode.PercentOutput, speeds.rightMotor*modifier);
        SmartDashboard.putNumber("2/8 testing", speeds.leftMotor*modifier);
        SmartDashboard.putNumber("master left",masters[LEFT].getMotorOutputPercent());
        SmartDashboard.putNumber("master right",masters[RIGHT].getMotorOutputPercent());
        SmartDashboard.putNumber("follow left",followers[LEFT][0].getMotorOutputPercent());
        SmartDashboard.putNumber("follow right",followers[RIGHT][0].getMotorOutputPercent());
    }
}