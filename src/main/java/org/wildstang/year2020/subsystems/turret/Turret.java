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

public class turret implements Subsystem {
    // digital inputs
	//DigitalInput activatelime;
	//Please comment on what the variables are.
    DigitalInput aimright;
	DigitalInput aimleft;
	private boolean limeOn = false;
	private boolean aimlefton;
	private boolean aimrighton;
	private double v;
    // talons
    TalonSRX turretPivot;
	private double x;
	private int mx;
	private double ConstantA = 1.2;
	@Override
	public void init() {
        // initialize inputs and outputs
		
	
		//activatelime = (DigitalInput) Core.getInputManager().getInput(WSInputs.BUTTON.getName());
        //activatelime.addInputListener(this);
        aimright = (DigitalInput) Core.getInputManager().getInput(WSInputs.TURRETRIGHT.getName());
        aimright.addInputListener(this);
		aimleft = (DigitalInput) Core.getInputManager().getInput(WSInputs.TURRETLEFT.getName());
        aimleft.addInputListener(this);
		
		
		turretPivot = new TalonSRX(CANConstants.TURRET_PIVOT);
	}

	@Override
	public void resetState() {
        // set default values
	}

	@Override
	public void inputUpdate(Input source) {
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tv = table.getEntry("tv");
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
				aimrighton = true;
			}
		}
        // respond to registered inputs
	}

	@Override
	public void update() {
		//get limelight values
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tx = table.getEntry("tx");
		NetworkTableEntry tv = table.getEntry("tv");
		x = tx.getDouble(0.0);
		v = tv.getDouble(0.0);
		//turn turret
		if (limeOn && (mx != 1)&& (mx!= -1) && (v == 1)){
			turretPivot.set(ControlMode.PercentOutput,F(x));
		}
		if ((aimrighton) || (aimlefton)){
			turretPivot.set(ControlMode.PercentOutput,mx);
		}
	}

	@Override
	public String getName() {
		return "Turret";
	}
	private double F(double k){
		return ((Math.pow(Math.abs(k/27),ConstantA))*(Math.abs(k)/k));
	}

	@Override
	public void selfTest() {
        // we don't really test... we probably should
	}
}