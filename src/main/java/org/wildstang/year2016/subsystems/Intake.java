package org.wildstang.year2016.subsystems;

import org.wildstang.framework.config.Config;
/* Please edit!!!
 * This program does all these things
 * reads INTAKE_BOLDER_SENSOR. INTAKE_BOLDER_SENSOR = intakeSensorReading.
 * intakeSensorReading stops rollerMovingIn unless manRollerInOverrideCurrentState is true and manRollerInOverrideOldState is false.
 *
 * reads MAN_LEFT_JOYSTICK_Y. MAN_LEFT_JOYSTICK_Y = manLeftJoyRollerIn
 * prints out manLeftJoyRollerIn. if manLeftJoyRollerIn is greater than .5, rollerMovingIn is set to true and rollerMovingOut is set to false. this makes the roller move in
 * if manLeftJoyRollerIn is less than -.5, rollerMovingIn is set to false and rollerMovingOut is set to true. this makes the roller move out
 *
 * status of manLeftJoyRollerIn, intakeSensorReading, and rollerMovingIn are printed out
 *
 * reads MAN_BUTTON_6. MAN_BUTTON_6 = manNoseControl
 * changes nosePneumatic to true and deployPneumatic to false when manNoseControl is true
 *
 * reads DRV_BUTTON_6. DRV_BUTTON_6 = drvNoseControl
 * changes nosePneumatic to true and deployPneumatic to false when drvNoseControl is true
 *
 * reads MAN_BUTTON_1. MAN_BUTTON_1 = manRollerInStartNew
 * toggles rollerMovingIn with manRollerInStartNew & manRollerInStartOld, stops intakeSensorReading from being true
 *
 * Continue...
 */
//expand this and edit if trouble with Ws
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.outputs.AnalogOutput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoid;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoidState;
import org.wildstang.hardware.crio.outputs.WsSolenoid;
import org.wildstang.year2016.robot.WSInputs;
import org.wildstang.year2016.robot.WSOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake implements Subsystem {
    // add variables here

    private boolean intakeSensorReading;
    private boolean nosePneumatic;
    private boolean deployPneumatic;
    private boolean manRollerInOverride;
    private boolean manNoseControl = false;
    private boolean manDeployPneumaticControl = false;
    // private boolean limboOn = false;
    private boolean shoot;
    private WsDoubleSolenoid intakeDeploy;
    private WsSolenoid intakeFrontLower;
    private AnalogOutput frontRoller;
    private AnalogOutput frontRoller2;
    private double manLeftJoyRoller;
    private double rollerSpeed;
    private boolean AutoIntakeOverride = false;
    private boolean ShotIntakeOverride = false;

    private double OverrideValue;

    private double intakeSpeedIn;
    private double intakeSpeedOut;
    private static final String intakeSpeedInKey = ".intakeSpeedIn";
    private static final double INTAKE_IN_DEFAULT = -1;
    private static final String intakeSpeedOutKey = ".intakeSpeedOut";
    private static final double INTAKE_OUT_DEFAULT = 1;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub

        // does something with Inputs and variables

        // setting intakeSensorReading to DIO_0_INTAKE_SENSOR
        if (source.getName().equals(WSInputs.INTAKE_BOLDER_SENSOR.getName())) {
            intakeSensorReading = ((DigitalInput) source).getValue();
        }

        // setting manLeftJoyRollerIn to the left joystick's y axis
        if (source.getName().equals(WSInputs.MAN_LEFT_JOYSTICK_Y.getName())) {
            manLeftJoyRoller = ((AnalogInput) source).getValue();
        }

        if (source.getName().equals(WSInputs.MAN_BUTTON_8.getName())) {
            shoot = ((DigitalInput) source).getValue();
        }

        // sets manNoseControl to Manipulator button 6
        if (source.getName().equals(WSInputs.MAN_BUTTON_5.getName())) {
            // if (((DigitalInput) source).getValue() == true)
            // {
            manNoseControl = ((DigitalInput) source).getValue();
            // }
        }

        // sets drvNoseControl to Drive Button 6
        // if (source.getName().equals(WSInputs.DRV_BUTTON_5.getName()))
        // {
        // if (((DigitalInput) source).getValue() == true)
        // {
        // manNoseControl = ((DigitalInput) source).getValue();
        // }
        // }

        // setting manRollerInOverride to Manipulator button 1
        if (source.getName().equals(WSInputs.MAN_BUTTON_9.getName())) {
            // manRollerInOverride = ((DigitalInput) source).getValue();
        }

        // setting manDeployPneumaticControl to Manipulator button 8
        if (source.getName().equals(WSInputs.MAN_BUTTON_7.getName())) {
            if (((DigitalInput) source).getValue() == true) {
                manDeployPneumaticControl = !manDeployPneumaticControl;
            }
        }

        // if (source.getName().equals(WSInputs.DRV_BUTTON_2.getName()))
        // {
        // if (((DigitalInput) source).getValue() == true)
        // {
        // limboOn = !limboOn;
        // }
        // }
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        intakeDeploy = ((WsDoubleSolenoid) Core.getOutputManager()
                .getOutput(WSOutputs.INTAKE_DEPLOY.getName()));
        intakeFrontLower = ((WsSolenoid) Core.getOutputManager()
                .getOutput(WSOutputs.INTAKE_FRONT_LOWER.getName()));
        frontRoller = ((AnalogOutput) Core.getOutputManager()
                .getOutput(WSOutputs.FRONT_ROLLER.getName()));
        frontRoller2 = ((AnalogOutput) Core.getOutputManager()
                .getOutput(WSOutputs.FRONT_ROLLER_2.getName()));

        intakeSpeedIn = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + intakeSpeedInKey, INTAKE_IN_DEFAULT);
        intakeSpeedOut = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + intakeSpeedOutKey, INTAKE_OUT_DEFAULT);

        // Reset intake state
        deployPneumatic = false;
        manDeployPneumaticControl = false;
        // limboOn = false;

        // asking for below Inputs
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_5.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_5.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_7.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_8.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_BUTTON_9.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.DRV_BUTTON_2.getName()).addInputListener(this);
        Core.getInputManager().getInput(WSInputs.MAN_LEFT_JOYSTICK_Y.getName())
                .addInputListener(this);
        Core.getInputManager().getInput(WSInputs.INTAKE_BOLDER_SENSOR.getName())
                .addInputListener(this);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

        // does something with variables and Outputs

        // tells status of certain variables
        // System.out.println("shoot=" + shoot + " rollerSpeed= " + rollerSpeed);

        // Puts the nose pneumatic in motion when either the drvNoseControl or
        // man nose control are true
        // if (drvNoseControl == true || manNoseControl == true)
        // {
        // nosePneumatic = true;
        // }
        // else
        // {
        // nosePneumatic = false;
        // }
        nosePneumatic = manNoseControl;

        // toggles deployPneumatic to manDeployPneumaticControl
        if (manDeployPneumaticControl == true) {
            deployPneumatic = true;
            rollerSpeed = 0;
        } else {
            deployPneumatic = false;
        }

        // if you push the left joy stick up, the intake will roll outwards.
        // if you push the left joy stick down, the intake will roll inwards.
        if (manLeftJoyRoller <= -0.5) {
            rollerSpeed = intakeSpeedIn;
        } else if (manLeftJoyRoller >= 0.5) {
            rollerSpeed = intakeSpeedOut;
        } else {
            rollerSpeed = 0;
        }

        if (intakeSensorReading == true && rollerSpeed < 0) {
            rollerSpeed = 0;
        }

        if (manRollerInOverride == true) {
            rollerSpeed = intakeSpeedIn;
        }

        if (shoot == true) {
            rollerSpeed = intakeSpeedIn;
        }

        // Allows for toggling of limbo
        // if (limboOn)
        // {
        // if (deployPneumatic == false)
        // {
        // deployPneumatic = true;
        // }
        // if(nosePneumatic == false)
        // {
        // nosePneumatic = true;
        // }
        // }

        // buttonPress controls DIO_LED_0 etc.
        // ((DigitalOutput)Core.getOutputManager().getOutput(WSOutputs.DIO_LED_0.getName())).setValue(manLeftJoyRollerIn
        // >= .5);
        // ((DigitalOutput)Core.getOutputManager().getOutput(WSOutputs.SENSOR_LED_1.getName())).setValue(intakeSensorReading);
        // ((DigitalOutput)Core.getOutputManager().getOutput(WSOutputs.FRONT_ROLLER_LED_2.getName())).setValue(rollerMovingIn);
        // ((DigitalOutput)Core.getOutputManager().getOutput(WSOutputs.Pneumatic_1.getName())).setValue(nosePneumatic);
        // ((DigitalOutput)Core.getOutputManager().getOutput(WSOutputs.Pneumatic_2.getName())).setValue(deployPneumatic);

        intakeDeploy.setValue(deployPneumatic ? (WsDoubleSolenoidState.FORWARD).ordinal()
                : (WsDoubleSolenoidState.REVERSE).ordinal());
        intakeFrontLower.setValue(nosePneumatic);
        if (AutoIntakeOverride && !intakeSensorReading) {
            frontRoller.setValue(OverrideValue);
            frontRoller2.setValue(-OverrideValue);
        } else if (AutoIntakeOverride && intakeSensorReading) {
            frontRoller.setValue(0);
            frontRoller2.setValue(0);
        } else if (ShotIntakeOverride) {
            frontRoller.setValue(OverrideValue);
            frontRoller2.setValue(-OverrideValue);
        } else {
            frontRoller.setValue(rollerSpeed);
            frontRoller2.setValue(-rollerSpeed);
        }
        SmartDashboard.putBoolean("Has Ball", intakeSensorReading);
        SmartDashboard.putNumber("rollerSpeed=", rollerSpeed);
        SmartDashboard.putBoolean("Intake staged=", intakeSensorReading);
    }

    public void notifyConfigChange(Config p_newConfig) {
        intakeSpeedIn = p_newConfig.getDouble(this.getClass().getName() + intakeSpeedInKey,
                INTAKE_IN_DEFAULT);

        intakeSpeedOut = p_newConfig.getDouble(this.getClass().getName() + intakeSpeedOutKey,
                INTAKE_OUT_DEFAULT);

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Intake";
    }

    public boolean isDeployed() {
        return deployPneumatic;
    }

    public boolean isNoseDeployed() {
        return nosePneumatic;
    }

    public void setIntakeOverrideOn(boolean state) {
        AutoIntakeOverride = state;
    }

    public void IntakeValue(double value) {
        OverrideValue = value;
    }

    public void setShotOverride(boolean state) {
        ShotIntakeOverride = state;
    }

    public void shotOverride(boolean on) {
        if (on) {
            OverrideValue = -1;
        } else {
            OverrideValue = 0;
        }
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub

    }

}
