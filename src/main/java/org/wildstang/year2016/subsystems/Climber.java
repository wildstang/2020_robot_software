package org.wildstang.year2016.subsystems;

import org.wildstang.framework.config.Config;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsSolenoid;
import org.wildstang.hardware.crio.outputs.WsVictor;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSOutputs;
import org.wildstang.year2016.robot.WSSubsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber implements Subsystem {
    /*
     * Climber Robot Class Authors: Wallace Butler and Lucas Papaioannou
     */
    private boolean armsDeploying = false;
    private boolean armsDeployed = false;
    private boolean hooksDeployed = false;
    private boolean deployStarted = false;

    private boolean winchMoved = false;

    private int startDelay = 0;
    private double previousWinch;
    private boolean brakeDelay;
    private double brakeTimer;

    private boolean brakeEngaged = true;

    private double joystickWinchSpeed = 0.0;
    private double winchSpeed = 0.0;

    private boolean override = false;

    private double armHelpSpeed;
    private double armHelpRunTime;

    private boolean brakeOverride = false;

    private static final String armsUpOutSpeed = ".arm_up_out_speed";
    private static final String armsUpRunTime = ".arm_up_run_time";
    private static final double ARMOUTSPEED_DEFAULT = -.75;
    private static final double ARMOUTTIME_DEFAULT = 3.2;
    private static final double WINCH_SPEED_DEADBAND = 0.1;

    private double winchEndTime;
    private WsSolenoid leftBrake;
    private WsSolenoid rightBrake;
    private WsSolenoid hooks;
    private WsSolenoid armSolenoid;
    private WsVictor leftWinch;
    private WsVictor rightWinch;

    @Override
    public void inputUpdate(Input source) {
        if (source.getName().equals(WSInputs.MAN_BUTTON_1.getName())) {
            if (((DigitalInput) source).getValue() && ((Intake) Core.getSubsystemManager()
                    .getSubsystem(WSSubsystems.INTAKE.getName())).isDeployed()) {
                armsDeploying = true;
                winchEndTime = Timer.getFPGATimestamp() + armHelpRunTime;
            }
        } else if (source.getName().equals(WSInputs.MAN_RIGHT_JOYSTICK_Y.getName())) {
            joystickWinchSpeed = ((AnalogInput) source).getValue();
        } else if (source.getName().equals(WSInputs.MAN_BUTTON_2.getName())) {
            if (((DigitalInput) source).getValue()) {
                hooksDeployed = !hooksDeployed;
            }

        } else if (source.getName().equals(WSInputs.MAN_BUTTON_9.getName())) {
            brakeOverride = ((DigitalInput) source).getValue();
        } else if (source.getName().equals(WSInputs.MAN_BUTTON_10.getName())) {
            override = ((DigitalInput) source).getValue();
        }
    }

    @Override
    public void init() {
        hooksDeployed = false;
        brakeTimer = 0;
        startDelay = 0;
        previousWinch = 0;
        brakeDelay = false;
        armsDeployed = false;
        armsDeploying = false;
        joystickWinchSpeed = 0;
        winchSpeed = 0;
        Core.getInputManager().getInput(WSInputs.MAN_RIGHT_JOYSTICK_Y.getName())
                .addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_1.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_2.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_10.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_9.getName()).addInputListener(this);
        // Core.getInputManager().getInput(WSInputs.RIGHT_ARM_TOUCHING.getName()).addInputListener(this);
        // Core.getInputManager().getInput(WSInputs.LEFT_ARM_TOUCHING.getName()).addInputListener(this);
        leftBrake = ((WsSolenoid) Core.getOutputManager()
                .getOutput(WSOutputs.LEFT_BRAKE.getName()));
        rightBrake = ((WsSolenoid) Core.getOutputManager()
                .getOutput(WSOutputs.RIGHT_BRAKE.getName()));
        hooks = (WsSolenoid) Core.getOutputManager().getOutput(WSOutputs.HOOK_EXTENSION.getName());
        armSolenoid = (WsSolenoid) Core.getOutputManager().getOutput(WSOutputs.ARMS.getName());
        leftWinch = (WsVictor) Core.getOutputManager().getOutput(WSOutputs.WINCH_LEFT.getName());
        rightWinch = (WsVictor) Core.getOutputManager().getOutput(WSOutputs.WINCH_RIGHT.getName());

        armHelpSpeed = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + armsUpOutSpeed, ARMOUTSPEED_DEFAULT);

        armHelpRunTime = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + armsUpRunTime, ARMOUTTIME_DEFAULT);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        if (override) {
            // double dummySpeed = joystickWinchSpeed/2;
            // Run at half speed in override mode
            // joystickWinchSpeed = dummySpeed;
            if (winchInDeadband(joystickWinchSpeed)) {
                // Explicitly stop if we're in the deadband
                winchSpeed = 0.0;
            } else {
                // Steve: Do we need this state check? I was going somewhere with this, but
                // can't remember now...
                // If we have started to move, remove air from the arm pistons

                winchSpeed = joystickWinchSpeed;
                if (!winchMoved && winchSpeed < 0) {
                    winchMoved = true;

                    // If we have started to move, remove air from the arm pistons
                    armsDeploying = false;
                }
            }
            if (previousWinch == 0 && winchSpeed != 0) {
                brakeTimer = Timer.getFPGATimestamp();
                brakeDelay = true;
            }
            if (brakeDelay) {
                if (Timer.getFPGATimestamp() - brakeTimer > .3) {
                    brakeDelay = false;
                }
            }
            if (!brakeDelay) {
                leftWinch.setValue(winchSpeed);
                rightWinch.setValue(winchSpeed);
            } else {
                // leftWinch.setValue(0);
                // rightWinch.setValue(0);
                leftWinch.setValue(.2);
                rightWinch.setValue(.2);
            }
            previousWinch = winchSpeed;
        }
        // Button has been pressed to deploy arms. Activate arm pistons and wind winches
        // out
        else if (armsDeploying && !armsDeployed) {
            // Set the wind-out winch speed
            winchSpeed = -armHelpSpeed;

            // Make sure the intake is out of the way
            if (!deployStarted) {
                // Flag to not repeatedly call this
                deployStarted = true;
                // protectIntake();

                // Start winch timer

            }

            // Delay the winches running to allow the brakes to disengage
            // Actively set the speed to 0 if the delay has not ended
            if (deployStarted && startDelay < 12) {
                // Flag that we are disengaging the brakes
                brakeEngaged = false;
                winchSpeed = -0.25;
                startDelay++;
            }

            if (Timer.getFPGATimestamp() >= winchEndTime) {
                winchSpeed = 0.0;
                armsDeployed = true;
            }

            // E-stop arms!
            if (!winchInDeadband(joystickWinchSpeed)) {
                winchSpeed = 0.0;
            }
        }
        // Arms are now at full extension. Allow joystick control of the winches
        else if (armsDeployed) {
            // Explicitly set speed to 0 if we're in the deadband
            if (winchInDeadband(joystickWinchSpeed)) {
                winchSpeed = 0.0;
            } else {
                // Steve: Do we need this state check? I was going somewhere with this, but
                // can't remember now...

                winchSpeed = joystickWinchSpeed;
                if (!winchMoved && winchSpeed < 0) {
                    winchMoved = true;

                    // If we have started to move, remove air from the arm pistons
                    armsDeploying = false;
                }
            }

            if (previousWinch == 0 && winchSpeed != 0) {
                brakeTimer = Timer.getFPGATimestamp();
                brakeDelay = true;
            }
            if (brakeDelay) {
                if (Timer.getFPGATimestamp() - brakeTimer > .3) {
                    brakeDelay = false;
                }
            }
            // Safety - don't pull down too far
            // TODO: Need switches/limit sensor
            // if (upperArmLimit || lowerArmLimit)
            // {
            // winchSpeed = 0;
            // }
        }

        // Set if the brake should be engaged. Note this is the reverse of what you
        // expect, due to the wiring of the solenoid
        if (winchSpeed != 0) {
            brakeEngaged = true;
        } else {
            brakeEngaged = false;
        }

        // Set the output values
        if (!hooksDeployed) {
            hooks.setValue(false);
        } else {
            hooks.setValue(true);
        }

        armSolenoid.setValue(armsDeploying);
        if (!override) {
            if (!brakeDelay) {
                leftWinch.setValue(winchSpeed);
                rightWinch.setValue(winchSpeed);
            } else {
                // leftWinch.setValue(0);
                // rightWinch.setValue(0);
                leftWinch.setValue(.2);
                rightWinch.setValue(.2);
            }
        }
        if (!brakeOverride) {
            // if (canEngageBrakes) {
            rightBrake.setValue(brakeEngaged);
            leftBrake.setValue(brakeEngaged);
            // }
        } else {
            rightBrake.setValue(true);
            leftBrake.setValue(true);
        }

        previousWinch = winchSpeed;
        SmartDashboard.putBoolean("Arms deploying", armsDeploying);
        SmartDashboard.putBoolean("Arms deployed", armsDeployed);
        SmartDashboard.putBoolean("hooks deployed", hooksDeployed);
        SmartDashboard.putNumber("Winch Speed", winchSpeed);
        SmartDashboard.putBoolean("Override", override);
        SmartDashboard.putBoolean("brakeEngaged", brakeEngaged);
        SmartDashboard.putBoolean("Arms", armSolenoid.getValue());
        SmartDashboard.putBoolean("Left Brake", leftBrake.getValue());
        SmartDashboard.putBoolean("Right Brake", rightBrake.getValue());
        SmartDashboard.putBoolean("Brake Override", brakeOverride);
        SmartDashboard.putNumber("Winch Speed", winchSpeed);
    }

    private boolean winchInDeadband(double p_winchSpeed) {
        return (p_winchSpeed > -WINCH_SPEED_DEADBAND) && (p_winchSpeed < WINCH_SPEED_DEADBAND);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Climber";
    }

    public void notifyConfigChange(Config p_newConfig) {
        armHelpSpeed = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + armsUpOutSpeed, ARMOUTSPEED_DEFAULT);

        armHelpRunTime = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + armsUpRunTime, ARMOUTTIME_DEFAULT);

    }

    public void protectIntake() {
        if (((Intake) Core.getSubsystemManager().getSubsystem(WSSubsystems.INTAKE.getName()))
                .isDeployed() != true) {
            ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_8.getName()))
                    .setValue(true);
        }
    }

    public void resetIntakeToggle() {
        ((DigitalInput) Core.getInputManager().getInput(WSInputs.MAN_BUTTON_8.getName()))
                .setValue(false);
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

}
