package org.wildstang.year2020.subsystems.turret;

import java.lang.Math;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.core.Core;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.CoreUtils;

public class Turret implements Subsystem {
    // digital inputs
	//DigitalInput activatelime;
	//Please comment on what the variables are.
    DigitalInput aimright;
	DigitalInput aimleft;
	private boolean limeOn = false; // is it being manually controlled? 
	private boolean aimlefton; //is left trigger pressed?
	private boolean aimrighton; // is right trigger pressed?
	private double v; // a limelight vairible for whether or not valid target is in sight
    // talons
    TalonSRX turretPivot; 
	TalonSRX turretVertical;//turret pivot motor
	private double x; // a varible for the motor percent output when limelight controlled
	private double y; // a variabke for the mtotr
	private int mx; //  a variable for the motor percent output when manually controlled
	private double height; // a cool variable
	private double Encoder; //cool stuff
	private double ConstantA = 1.2; // fine-tuning variable for when limelight controlled. 
	@Override
	public void init() {
        // initialize inputs and outputs	
		//activatelime = (DigitalInput) Core.getInputManager().getInput(WSInputs.BUTTON.getName());
        //activatelime.addInputListener(this);
        aimright = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT.getName());
        aimright.addInputListener(this);
		aimleft = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT.getName());
        aimleft.addInputListener(this);				
		turretPivot = new TalonSRX(CANConstants.TURRET_TALON);//TURRET_PIVOT is changed to TURRET_TALON
		turretVertical = new TalonSRX(CANConstants.HOOD_MOTOR);
	}

	@Override
	public void resetState() {
        // set default values
	}

	@Override
	public void inputUpdate(Input source) {
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tv = table.getEntry("tv");
		NetworkTableEntry ty = table.getEntry("ty");
		NetworkTableEntry tx = table.getEntry("tx");
		if (aimright.getValue() || aimleft.getValue()){
			limeOn = false;
		}
        else{
            limeOn = true;
        }		
		if ((source == aimright)||(source == aimleft)){
			if (aimright.getValue() && (aimlefton == false)){
				mx = 1;
				aimrighton = true;
			}
			if (aimleft.getValue() && (aimrighton == false)){
				mx = -1;
				aimlefton = true;
			}
			if ((!aimleft.getValue()) && (aimlefton == true)){
				mx = 0;
				aimlefton = false;
			}
			if ((!aimright.getValue()) && (aimrighton == true)){
				mx = 0;
				aimrighton = false;
			}
		}
        // respond to registered inputs
	}

	@Override
	public void update() {
		//get limelight values
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tx = table.getEntry("tx");
		NetworkTableEntry ty = table.getEntry("ty");
		NetworkTableEntry tv = table.getEntry("tv");
		x = tx.getDouble(0.0);
		v = tv.getDouble(0.0);
		y = ty.getDouble(0.0);
		//turn turret
		Encoder = turretVertical.getSelectedSensorPosition();
		if (limeOn && (mx != 1)&& (mx!= -1) && (v == 1)){
			turretPivot.set(ControlMode.PercentOutput,F(x));
		}
		if ((aimrighton) || (aimlefton) || (v != 1)){
			turretPivot.set(ControlMode.PercentOutput,mx);
		} 
		turretVertical.set(ControlMode.PercentOutput,F(Encoder-Func(y)));
	}

	@Override
	public String getName() {
		return "Turret";
	}
	private double F(double k){
		return ((Math.pow(Math.abs(k/27),ConstantA))*(Math.abs(k)/k));
	}
		// Math.pow(a,b) = 
	@Override
	public void selfTest() {
        // we don't really test... we probably should
	}
	 private double Func(double c){
        double h = height/Math.tan(c);
        double v = 10;
        return (Math.asin((1-Math.sqrt(1-(8*Math.pow(c,2)*(Math.pow(v,2))-(-9.8))*c*Math.pow(h,2))))/(2*c)); // the angle calculator function goes here
    }
}
