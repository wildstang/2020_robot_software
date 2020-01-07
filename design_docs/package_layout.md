# Package Layout

A quick overview of how the packages are organized in our sources and what belongs where.

##`org.wildstang.framework`

WildStang code common to every robot. The accumulated reusable Java assets of the team. If the code you are writing is not specific to the robot but general functionality, then it should go here.
    
- `.auto`: See [Auto][auto]. Framework code for autonomous mode.
- `.config`: These classes let us manage the robot's configuration on the fly. A Robot class will call the ConfigManager loadConfig() method to use this.
- `.core`: If you only used one package in the framework it would be .core. .core.Core is a class that, when instantiated, sets up the basics of the framework; one of a Robot class's first actions on startup will be to instantiate its Core object member. Also in this package are the configuration interfaces for [inputs][io], [outputs][io] and [subsystems][sub] that a particular robot's code will need to implement in order to enumerate its components.
- `.hardware`: See [Inputs and Outputs][io].
- `.io`: See [Inputs and Outputs][io].
- `.logger`: Various tools to log, track and debug robot state. TODO: investigate more here.
- `.motionprofile`: TODO: document
- `.pid`: See [PID][PID]. 
- `.subsystems`: See [Subsystems][sub].
- `.timer`: Various timing utility classes.

## `org.wildstang.hardware`

See [Inputs and Outputs][io]. Some driver-station-specific code is also here.

## `org.wildstang.yearXXXX`

Code for a particular robot e.g. year2016, year2017, devbase1.
- `.auto`: See [Auto][auto]. Code defining autonomous behavior for this robot.
- `.robot`: Code to configure and intialize the robot itself. The [inputs,][io] [outputs][io] and [subsystems][sub] are configured here. The Robot class that represents the robot as a whole resides here.
- `.subsystems`: See [Subsystems][sub].
 



[io]: inputs_and_outputs.md "Inputs and Outputs"
[sub]: subsystems.md "Subsystems"
[PID]: PID.md "PID"
[auto]: auto.md "Auto"
    