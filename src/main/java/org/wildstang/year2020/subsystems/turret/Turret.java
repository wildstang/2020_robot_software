package org.wildstang.year2020.subsystems.turret;
import org.wildstang.year2020.robot.CANConstants;
import org.wildstang.year2020.robot.WSInputs;

import java.lang.Math; //math stuff

import com.ctre.phoenix.motorcontrol.can.TalonSRX; //motor stuff
import com.ctre.phoenix.motorcontrol.ControlMode;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTable; //limelight stuff
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import org.wildstang.framework.io.Input; //input stuff
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.core.Core;



public class Turret implements Subsystem {
    // digital inputs
	//DigitalInput activatelime;
	//Please comment on what the variables are.
    DigitalInput aimright;
	DigitalInput aimleft;
	DigitalInput autoOn;
	DigitalInput autoOff;
	DigitalInput shoot;
	DigitalInput LowSpeed;
	DigitalInput HighSpeed;
	AnalogInput HoodManual;
	AnalogInput TurrManual;
	private boolean limeOn = false; // is it being manually controlled? 
	private boolean aimlefton; //is left trigger pressed?
	private boolean aimrighton; // is right trigger pressed?
	private double v; //used in computing launch angle
	private double insight;// a limelight vairible for whether or not valid target is in sight
	private double V; //shooter speed
	private double MaxS = 300; //shooter max speed
	private double MinS = 10;//shooter min speed
	private double Mhood;
	private double Mturr;
    // talons
    TalonSRX turretPivot; 
	TalonSRX ShootMotor; 
	TalonSRX ShootMotor2;    
	TalonSRX turretVertical;
	private boolean isShooterOn;
	private double x; // a varible for the motor percent output when limelight controlled
	private double y; // like x, but y.
	private double height = 10; //Change this to the height difference between turret and target
	private double Encoder; //what it sounds like
	private double HorzEncoder;
	private double ConstantA = 1.2; // fine-tuning variable for when limelight controlled. 
	@Override
	public void init() {
        // initialize inputs and outputs	
		
        aimright = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT.getName());
        aimright.addInputListener(this);
		aimleft = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT.getName());
        aimleft.addInputListener(this);		
		autoOn = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_LEFT.getName());
        autoOn.addInputListener(this);	
		autoOff = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_BUTTON.getName());
        autoOff.addInputListener(this);
		shoot = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_TRIGGER_RIGHT.getName());
        shoot.addInputListener(this);
		HighSpeed = (DigitalInput) Core.getInputManager().getInput(WSInputs. MANIPULATOR_SHOULDER_RIGHT.getName());
        HighSpeed.addInputListener(this);
		LowSpeed = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_SHOULDER_LEFT.getName());
        LowSpeed.addInputListener(this);
		HoodManual = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_Y.getName());
        HoodManual.addInputListener(this);
		TurrManual = (AnalogInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_JOYSTICK_X.getName());
        TurrManual.addInputListener(this);
		turretPivot = new TalonSRX(CANConstants.TURRET_TALON);//TURRET_PIVOT is changed to TURRET_TALON
		turretVertical = new TalonSRX(CANConstants.HOOD_MOTOR);
		ShootMotor = new TalonSRX(CANConstants.LAUNCHER_TALON);
		ShootMotor2 = new TalonSRX(CANConstants.LAUNCHER_VICTOR);
		ShootMotor2.follow(ShootMotor);
	}

	@Override
	public void resetState() {
        // set default values
	/*TurretPivot.getSensorCollection().setQuadraturePosition(0, 0);
	TurretVertical.getSensorCollection().setQuadraturePosition(0, 0);*/
	}

	@Override
	public void inputUpdate(Input source) {
		if (HoodManual == source){ //manual controls
			Mhood = HoodManual.getValue();
		}
		if (TurrManual == source){
			Mturr = TurrManual.getValue();
		}
		if (source == autoOff){ //auto on/off button
			limeOn = false;
		}
        if (source == autoOn){
            limeOn = true;
	}
        
		if (LowSpeed.getValue()){ //speed chooser
			V = MinS;
		}
		else{
			if (HighSpeed.getValue()){
			V = MaxS;
		}
		else{
			V = ((MaxS+MinS)/2);
		}
		}
			
		isShooterOn = shoot.getValue();
		
		
			if (aimright.getValue()){  //hotkeys pressed??
				
				aimrighton = true; 
				limeOn = false;
			}
			if (aimleft.getValue()){
				
				aimlefton = true;
				limeOn = false;
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
		insight = tv.getDouble(0.0);
		y = ty.getDouble(0.0)+27; //this is for 45 degree angle. change added number accordingly.
		//turn turret
		Encoder = turretVertical.getSelectedSensorPosition();
		HorzEncoder = turretPivot.getSelectedSensorPosition();
		if (limeOn && (insight == 1)){
			turretPivot.set(ControlMode.PercentOutput,position(x)); //move turret to point at target
		}
		if (limeOn && (insight == 0)){
			turretPivot.set(ControlMode.PercentOutput,0.0); //but not when target out of site
		}
		if (!limeOn){
			turretPivot.set(ControlMode.PercentOutput,position(Mturr*27)); //manual control
		}
		if (aimrighton){
			turretPivot.set(ControlMode.PercentOutput,position(((1024-HorzEncoder)/151.703))); //position control
			if ((position((1024-HorzEncoder)/151.703)<0.05)&&(position((1024-HorzEncoder)/151.703)>-0.05)){
				aimrighton = false;
				turretPivot.set(ControlMode.PercentOutput,0.0);
			}
		} 
		if (aimlefton){
			turretPivot.set(ControlMode.PercentOutput,position(((-1024-HorzEncoder)/151.703))); //other position control
			if ((position((-1024-HorzEncoder)/151.703)<0.05)&&(position((-1024-HorzEncoder)/151.703)>-0.05)){
				aimlefton = false;
				turretPivot.set(ControlMode.PercentOutput,0.0);
			}
		} 
		if (limeOn){
		turretVertical.set(ControlMode.PercentOutput,position(Encoder-((aim(y/2)/6.28)*4096))); //vertical aiming
		}
		else {
			turretVertical.set(ControlMode.PercentOutput,position(Mhood*27));// manual aiming
		}
		
		if (isShooterOn == true){
			ShootMotor.set(ControlMode.Velocity,V); //fire (or don't)
				}
				else{
		ShootMotor.set(ControlMode.Velocity,0);
				}
	}

	@Override
	public String getName() {
		return "Turret";
	}
	private double position(double k){
		return ((Math.pow(Math.abs(k/27),ConstantA))*(Math.abs(k)/k)); //to help with motor angleing
	}
		// Math.pow(a,b) = 
	@Override
	public void selfTest() {
        // we don't really test... we probably should
	}
	 private double aim(double c){
        double h = height/Math.tan(c);
	double v = V;
        return (Math.asin((1-Math.sqrt(1-(8*Math.pow(c,2)*(Math.pow(v,2))-(-9.8))*c*Math.pow(h,2))))/(2*c)); // the angle calculator function goes here
    }
}
