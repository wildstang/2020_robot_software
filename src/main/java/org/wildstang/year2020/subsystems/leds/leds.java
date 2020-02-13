package org.wildstang.year2020.subsystems.leds;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.crio.outputs.WsI2COutput;
import org.wildstang.year2020.robot.WSInputs;
import org.wildstang.year2020.robot.WSOutputs;
import org.wildstang.year2020.robot.WSSubsystems;
import edu.wpi.first.wpilibj.DriverStation;

/*
    Use IC2 bus for this Java class to communicate to the Arduino over serial
    Finish this class FIRST before working on the arduino ino file
    If you don't know what you're doing please leave this alone (e.g me)
*/
public class leds implements Subsystem
{

    private static final int DISABLED_ID = 1;
    private static final int AUTO_ID = 2;
    private static final int ALLIANCE_ID = 3;
    private static final int LIMELIGHT_AIMING_ID = 4;
    private static final int LAUNCHER_READY_ID = 5;
    private static final int LAUNCHING = 6;
    private static final int CLIMBING_ID = 7;
    private static final int CLIMB_COMPLETE_ID = 8;
    private static final int LEFTJAM_ID = 9;
    private static final int RIGHTJAM_ID = 10;
    private static final int OFF_ID = 12;

   // Sent states
   boolean autoDataSent = false;
   boolean m_newDataAvailable = false;
   boolean disableDataSent = false;

   private String m_name;

   WsI2COutput m_ledOutput;

   boolean m_normal = true;
   boolean m_limelightAiming;
   boolean m_launching;
   boolean m_climbing = false;
   boolean m_intake;
   boolean m_gettingToSpeed;

   boolean m_leftFeedJammed = false;
   boolean m_rightFeedJammed = false;
   boolean m_isFlywheelReady = false;
   
   private launcher m_launcher;

   public static LedCmd disabledCmd = new LedCmd(DISABLED_ID, 255, 255, 255);
   public static LedCmd autoCmd = new LedCmd(AUTO_ID, 255, 255, 0);
   public static LedCmd redAllianceCmd = new LedCmd(ALLIANCE_ID, 255, 0, 0);
   public static LedCmd blueAllianceCmd = new LedCmd(ALLIANCE_ID, 0, 0, 255);
   public static LedCmd purpleAllianceCmd = new LedCmd(ALLIANCE_ID, 255, 0, 255);
   public static LedCmd turboCmd = new LedCmd(TURBO_ID, 0, 0, 0);
   
   public static LedCmd launcherOnCmd = new LedCmd(LAUNCHER_READY_ID, 255, 255, 0);
      // When flywheels are simply running
   public static LedCmd launchingCmd = new LedCmd(LAUNCHING_ID, 0, 0, 0);
      // When flywheels are running and gates are open and feed is going
   
   public static LedCmd launcherReadyCmd = new LedCmd(LAUNCHER_READY_ID, 255, 255, 0);
   public static LedCmd climbingCmd = new LedCmd(CLIMBING_ID, 0, 0, 0);
   public static LedCmd leftFeedCmd = new LedCmd(LEFTJAM_ID, 0, 0, 255);
   public static LedCmd rightFeedCmd = new LedCmd(RIGHTJAM_ID, 255, 0, 0);
   public static LedCmd offCmd = new LedCmd(OFF_ID, 0, 0, 0);

   public LED()
   {
      m_name = "LED";
   }

   @Override
   public void init()
   {
      resetState();
      
      m_ledOutput = (WsI2COutput) Core.getOutputManager().getOutput(WSOutputs.LED.getName());

      m_launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WSSubsystems.LAUNCHER.getName());

    // TODO Add listeners for launching subsystem inputs
    //Core.getInputManager().getInput(WSInputs.FLYWHEEL.getName()).addInputListener(this);

   }

   @Override
   public void resetState()
   {
      autoDataSent = false;
      disableDataSent = false;
      m_newDataAvailable = false;
   }

   @Override
   public void update()
   {
      // Get all inputs relevant to the LEDs
      boolean isRobotEnabled = DriverStation.getInstance().isEnabled();
      boolean isRobotTeleop = DriverStation.getInstance().isOperatorControl();
      boolean isRobotAuton = DriverStation.getInstance().isAutonomous();
            
      m_normal = !m_turbo;

      if (isRobotEnabled)
      {
         // Robot is enabled - teleop or auto
         if (isRobotTeleop)
         {
            LedCmd command = offCmd;
            if (m_newDataAvailable)
            {
               if (m_launcherOn)
               {
                  if (m_gettingToSpeed)
                  {
                     command = launcherOnCmd;
                     if (m_launcher.isLeftReadyToLaunch() && m_launcher.isRightReadyToLaunch())
                     {
                        m_gettingToSpeed = false;
                        command = launcherReadyCmd;
                     }
                  }
               }
               if (m_launching)
               {
                  command = launchingCmd;
               }
               if (m_climbing)
               {
                  command = climbingCmd;
               }
               if (m_leftFeedJammed)
               {
                  command = leftFeedCmd;
               }
               if (m_rightFeedJammed)
               {
                  command = rightFeedCmd;
               }
               
               m_ledOutput.setValue(command.getBytes());
               
            }
            m_newDataAvailable = false;

         }
         else if (isRobotAuton)
         {
            if (!autoDataSent)
            {
               m_ledOutput.setValue(autoCmd.getBytes());
               autoDataSent = true;
            }
         }
      }
   }

   @Override
   public void inputUpdate(Input source)
   {
      if (source.getName().equals(WSInputs.FLYWHEEL.getName()))
      {
         m_launcherOn = m_launcher.isFlywheelOn();
         if (m_launcherOn)
         {
            m_gettingToSpeed = true;
         }
         else
         {
            m_gettingToSpeed = false;
         }
      }
      else if (source.getName().equals(WSInputs.FEEDER_LEFT.getName()))
      {
         m_launching = m_launcher.isLaunching();
      }
      else if (source.getName().equals(WSInputs.FEEDER_RIGHT.getName()))
      {
         m_launching = m_launcher.isLaunching();
      }
      else if (source.getName().equals(WSInputs.CLIMBER_UP.getName()))
      {
         m_climbing = ((DigitalInput)source).getValue();
      }
      m_newDataAvailable = true;
   }

//   public void sendCommand(LedCmd p_command)
//   {
//      m_currentCmd = p_command;
//      m_newDataAvailable = true;
//   }

   public void sendLeftFeedJammed()
   {
      m_leftFeedJammed = true;
      m_newDataAvailable = true;
   }
   
   public void sendrightFeedJammed()
   {
      m_rightFeedJammed = true;
      m_newDataAvailable = true;
   }
   
   @Override
   public void selfTest()
   {
   }

   @Override
   public String getName()
   {
      return m_name;
   }

   public static class LedCmd
   {

      byte[] dataBytes = new byte[4];

      public LedCmd(int command, int red, int green, int blue)
      {

         dataBytes[0] = (byte) command;
         dataBytes[1] = (byte) red;
         dataBytes[2] = (byte) green;
         dataBytes[3] = (byte) blue;
      }

      public byte[] getBytes()
      {
         return dataBytes;
      }
   }
}
