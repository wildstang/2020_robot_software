# WildStang Subsystems

WildStang organizes the robot into distinct subsystems each covering a major functional area of the robot. For example, the 2016 robot had subsystems for the climber, the drive base, the intake and the shooter. Other subsystems that year included a status monitor, the LED lighting system, a vision system, and certain testing subsystems. Additionally, the core framework includes a WSMJPEGStreamer subsystem whose function is to stream video to the driver's station.

## Code locations & structure

-  `org.wildstang.framework.core.Subsystems`: Configuration enum interface.
-  `org.wildstang.subsystems`: Generic subsystem bookkeeping. The base class for all subsystems, `Subsystem`, resides here.
-  `org.wildstang.yearXXXX.robot.WSSubsystems`: Enum implementing `Subsystems` interface that configs all subsystems on robot.
-  `org.wildstang.yearXXXX.subsystems`: Classes for each subsystem of this year's robot.

Every subsystem must implement the interface `org.wildstang.framework.subsystems.Subsystem` (not to be confused with `org.wildstang.framework.core.Subsystems`). This means implementing these methods:

* `inputUpdate()` documented in `InputListener` javadoc
* `init()` documented in `Subsystem` javadoc
* `selfTest()` documented in `Subsystem` javadoc
* `update()` documented in `Subsystem` javadoc
* `resetState()` documented in `Subsystem` javadoc
* `getName()` documented in `Subsystem` javadoc


## Initialization
The list of subsystems for the robot is defined in `org.wildstang.yearXXXX.robot.WSSubsystems`, which is an enum implementing `org.wildstang.framework.subsystems.Subsystem`. 

`Robot.robotInit()` (see robot_startup_and_run.md) calls `Core.createSubsystems()`, which sets up the `SubsystemManager` and each subsystem listed in `WSSubsystems`. Each subsystem overrides the `init()` method. It appears that all initialization happens in `init()` instead of the constructor (TODO: figure out why this is?)

## Input
Subsystems can receive input from `Input` objects through the `inputUpdate()` method. `inputUpdate()` will be called with the input every time an input changes; the subsystem is responsible for deciding whether this particular input is relevant. 

For example, consider the 2016 `DriveBase` subsystem (`org.wildstang.year2016.subsystems.DriveBase`) `inputUpdate()` method. This subsystem's `inputUpdate()` checks to see if the input is one of the driver's steering, gearshift or throttle controls. If it is, it changes the behavior of the drivebase. For example, if the input is the gear shift button, it makes the robot drive base shift into high gear.

## Update
Like most other software components on the robot, the subsystem has a periodic `update()`. The `SubsystemManager` is responsible for making sure that `update()` is called regularly. In the `update()` method, the subsystem should do whatever it is responsible for doing continuously.

For example, consider the 2016 `DriveBase` subsystem (`org.wildstang.year2016.subsystems.DriveBase`) `update()` method. Because the drivebase includes a control loop, the `update()` method must pump the control loop. This is to say, even when the input has not changed, the drivebase must make minor course corrections in order to keep going in the direction and at the speed commanded by the driver. This occurs in the `update()` method.
