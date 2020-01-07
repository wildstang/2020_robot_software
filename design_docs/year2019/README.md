# 2019 Robot: Destination: Deep Space

## Subsystems

Subsystem divisions are tentative.

### Drive

The robot is an 8-wheel tank-drive robot, and the usual Cheesy-drive code applies here.

Actuators:
*  L and R motor controllers (talon-victor-victor)

Sensors:
*  L and R wheel encoders
*  NavX

### Ball path
The intake, hopper and carriage collectively constitute the ballpath subsystem. 

Actuators:
*  Intake deploy piston
*  Intake roller
*  Hopper conveyor belt
*  Hopper conveyor belt position piston
*  Carriage belt

Sensors: 
*  Carriage ball presence detect

### Strafe
The strafe axis onto which the hatch mechanism is mounted, as well as the line sensor mounted to the base of the robot, collectively constitute the strafe subsystem.

Actuators:
*  Strafe axis talon

Sensors: 
*  Encoder for strafe axis
*  Limit switch(es) for strafe axis
*  ~32 photosensors for line detection (on RasPi?)

### Lift
It's basically a forklift. In departure from previous designs, it doesn't move in stages, so the same weight is on the lift at all times; this means the PID constants can be, well, constant.

Actuators:
*  Drive motor for lift

Sensors:
*  Encoder on drive motor
*  Limit switch(es)

### Vision processing
Vision will be on the RasPi this year. The scope of this component TBD; first step is simply to be serving camera data. This might include processing the line data for the strafe axis.

### Climbing wedge
The climbing wedge is a one-shot permanent-deploy device.

Actuators:
*  Deploy pistons

