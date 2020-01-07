package org.wildstang.devbase1.subsystems;

import org.wildstang.devbase1.robot.WSInputs;
import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.pid.PIDConstants;
import org.wildstang.framework.subsystems.Subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** This subsystem controls the drive wheels so that the robot can move
 * around. */
public class Drive implements Subsystem {
    // Constants
    /** Distance from the centerline of left wheels to centerline of right wheels */
    // private static final double WHEELBASE_WIDTH_INCHES = 30;
    /** Diameter of drive wheels */
    private static final double WHEEL_DIAMETER_INCHES = 4;
    /** Number of encoder ticks in one revolution of the wheel */
    private static final double ENCODER_CPR = 4096;
    /** # of ticks in one surface inch of wheel movement */
    private static final double TICKS_PER_INCH = ENCODER_CPR / (WHEEL_DIAMETER_INCHES * Math.PI);
    // private static final double RADIANS = Math.PI / 180;

    /** Maximum wheel speed in inches / 100ms */
    private static final double MAX_SPEED_INCHES = 1.5;
    /** Max wheel speed in ticks / 100ms */
    private static final double MAX_SPEED_TICKS = MAX_SPEED_INCHES * TICKS_PER_INCH;

    /** Status frame period controls how frequently the TalonSRX reports back with
     * its sensor status over the CANBus. This constant is in milliseconds. */
    //private static final int STATUS_FRAME_PERIOD = 10;
    //private static final double NEUTRAL_DEADBAND = 0.001;
    private static final int TIMEOUT = 100; // milliseconds

    // Makes motor setup code DRYer
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int[] SIDES = {LEFT, RIGHT};
    private static final String[] SIDE_NAMES = {"left", "right"};
    // left then right
    private static final int[] MASTER_IDS = {7, 8};
    // left two then right two
    private static final int[][] FOLLOWER_IDS = {{4, 11}, {9, 3}};

    private static final PIDConstants MANUAL_DRIVE_PID_CONSTANTS =
            new PIDConstants(.8, 0.001, 10, 0.55);

    /** Left and right Talon master controllers */
    private TalonSRX[] masters = new TalonSRX[2];
    /** Left and right pairs of Victor follower controllers */
    private VictorSPX[][] followers = new VictorSPX[2][2];

    /** Input to steer robot */
    private AnalogInput headingInput;
    /** Input to control forward-backward movement */
    private AnalogInput throttleInput;

    private int updateCounter;

    public Drive() {
        /* Nothing to do in constructor. */
    }


    public void init() {
        /*try {
            initControllers();
        } catch () {
            System.out.println("Failed to init drive motor controllers: " + e);
        }*/

        // Set and subscribe to inputs
        headingInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVE_HEADING);
        headingInput.addInputListener(this);
        throttleInput = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVE_THROTTLE);
        throttleInput.addInputListener(this);
    }

    @Override
    public void resetState() {
        // TODO
    }

    /** Initialize all drive base motor controllers. */
    private void initControllers() /*throws CoreUtils.CTREException*/ {
        for (int side : SIDES) {
            masters[side] = new TalonSRX(MASTER_IDS[side]);

            initMaster(side, masters[side]);

            for (int i = 0; i < FOLLOWER_IDS[side].length; ++i) {
                followers[side][i] = new VictorSPX(FOLLOWER_IDS[side][i]);
                initFollower(side, followers[side][i]);
            }
        }
    }

    /** Initialize this master controller.
     *
     * <p>See https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages/ for
     * examples on how to set up a Talon.
     *
     * @param side Which side (LEFT or RIGHT) this is master for
     * @param master The WSTalonSRX object to set up */
    private void initMaster(int side, TalonSRX master) /*throws CoreUtils.CTREException*/ {
        System.out.println("Initializing TalonSRX master ID " + MASTER_IDS[side]);

        // The Talon SRX should be directly connected to an encoder
        master.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, TIMEOUT);

        /* If one of the sensors is on backwards we might do
         * if (side == LEFT) {
         *     master.setSensorPhase(true); // invert sensor w.r.t. motor
         * } */
        if (side == LEFT) {
            master.setInverted(false);
            master.setSensorPhase(true);
        } else {
            master.setInverted(true);
            master.setSensorPhase(true);
        }

        /*
        CoreUtils.checkCTRE(master.setStatusFramePeriod(
                StatusFrame.Status_2_Feedback0, STATUS_FRAME_PERIOD, TIMEOUT));

        CoreUtils.checkCTRE(master.configNeutralDeadband(NEUTRAL_DEADBAND, TIMEOUT));*/

        // Configure output to range from full-forward to full-reverse.
        /*CoreUtils.checkCTRE*/master.configNominalOutputForward(0, TIMEOUT);
        /*CoreUtils.checkCTRE*/master.configNominalOutputReverse(0, TIMEOUT);
        /*CoreUtils.checkCTRE*/master.configPeakOutputForward(+1.0, TIMEOUT);
        /*CoreUtils.checkCTRE*/master.configPeakOutputReverse(-1.0, TIMEOUT);

        // Put in sane default PID constants.
        /*CoreUtils.checkCTRE*/master.config_kP(0, MANUAL_DRIVE_PID_CONSTANTS.p, TIMEOUT);
        /*CoreUtils.checkCTRE*/master.config_kI(0, MANUAL_DRIVE_PID_CONSTANTS.i, TIMEOUT);
        /*CoreUtils.checkCTRE*/master.config_kD(0, MANUAL_DRIVE_PID_CONSTANTS.d, TIMEOUT);
        /*CoreUtils.checkCTRE*/master.config_kF(0, MANUAL_DRIVE_PID_CONSTANTS.f, TIMEOUT);

        master.setNeutralMode(NeutralMode.Coast);

        // CoreUtils.checkCTRE(master.configSetParameter(
        //         ParamEnum.ePIDLoopPeriod, PID_PERIOD, 0 /*Subvalue, ignored */, 0, TIMEOUT));
        // CoreUtils.checkCTRE(master.configSetParameter(
        //         ParamEnum.ePIDLoopPeriod, PID_PERIOD, 0 /*Subvalue, ignored */, 1, TIMEOUT));

        /*CoreUtils.checkCTRE*/master.getSensorCollection().setQuadraturePosition(0, TIMEOUT);

        TalonSRXConfiguration read_talon = new TalonSRXConfiguration();
        master.getAllConfigs(read_talon, TIMEOUT);
        System.out.print(read_talon.toString("drive talon " + SIDE_NAMES[side]));
    }

    private void initFollower(int side, VictorSPX follower) {
        TalonSRX master = masters[side];
        follower.setInverted(side == RIGHT);
        follower.follow(master);
        follower.setNeutralMode(NeutralMode.Coast);
    }

    @Override
    public void inputUpdate(Input source) {
        updateDrive(headingInput.getValue(), throttleInput.getValue());
    }

    @Override
    public void selfTest() {
        // DO NOT IMPLEMENT
        // TODO WHY NOT?
    }

    @Override
    public void update() {
        // TODO
        if (updateCounter % 10 == 0) {
            for (int side : SIDES) {
                TalonSRX master = masters[side];
                double output = master.getMotorOutputPercent();
                SmartDashboard.putNumber(SIDE_NAMES[side] + " output", output);
                double speed = master.getSelectedSensorVelocity();
                SmartDashboard.putNumber(SIDE_NAMES[side] + " speed", speed);
                double error = master.getClosedLoopError();
                SmartDashboard.putNumber(SIDE_NAMES[side] + " error", error);
                double target = master.getClosedLoopTarget();
                SmartDashboard.putNumber(SIDE_NAMES[side] + " target", target);
            }
        }
        updateCounter += 1;
    }

    @Override
    public String getName() {
        return "Drive Base";
    }

    private void updateDrive(double heading, double throttle) {
        double left_throttle = throttle + heading;
        double right_throttle = throttle - heading;

        double max_throttle = Math.max(Math.abs(left_throttle), Math.abs(right_throttle));

        double normalized_left_throttle = left_throttle / Math.max(1, max_throttle);
        double normalized_right_throttle = right_throttle / Math.max(1, max_throttle);

        //System.out.println(normalized_left_throttle + "   " + normalized_right_throttle);
        //masters[LEFT].set(ControlMode.Velocity, left_throttle * MAX_SPEED_TICKS);
        //masters[RIGHT].set(ControlMode.Velocity, right_throttle * MAX_SPEED_TICKS);
        masters[LEFT].set(ControlMode.PercentOutput, normalized_left_throttle);
        masters[RIGHT].set(ControlMode.PercentOutput, normalized_right_throttle);
    }
}
