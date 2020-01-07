# Robot Startup and Run

A quick overview of how the robot initializes and runs.

## Iterative Robot

The WPILib framework provided by FIRST has several templates for writing robot code: command-based, iterative, sample, or timed. It appears that everyone serious uses the iterative robot these days, so the others won't be documented here.

The Iterative robot must define a class that extends edu.wpi.first.wpilibj.IterativeRobot. The fully-qualified name of this class must be specified in the project build.properties. In our code, this is org.wildstang.yearXXXX.robot.Robot.

IterativeRobot is mostly documented in [edu.wpi.first.wpilibj.IterativeRobotBase javadoc](first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj/IterativeRobotBase.html)

## Robot startup
WPI code calls Robot.robotInit() at power-on. robotInit is responsible for all WildStang code init. Highlights:
* creates src.org.wildstang.framework.Core instance, which contains and sets up
    - InputManager s_inputManager
    - OutputManager s_outputManager
    - SubsystemManager s_subsystemManager
    - ConfigManager s_configManager
* Adds all the autos to the AutoManager singleton (FIXME: AutoManager should have the same lifecycle as the other managers above?)

## Robot run
During operation, as discussed in the [IterativeRobotBase documentation](first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj/IterativeRobotBase.html), WPI code will call Robot's periodic update functions:
* robotPeriodic(): called periodically regardless of operating mode.
* teleopPeriodic(): called periodically during driver operation (teleoperation).
* autonomousPeriodic(): called periodically during autonomous operation.
* disabledPeriodic(): called periodically while the robot is disabled.
* testPeriodic(): called periodically while the robot is in test mode.

At all times one of these functions needs to be calling the Core method update(), which is responsible for ensuring that inputs, outputs and subsystems get *their* update() methods called regularly and appropriately. At this time of writing I assume it's most appropriate to put that call in robotPeriodic() but I don't know if there are ordering issues with the other fooPeriodic() calls.

TODO: We should determine and document whether we need some kind of soft-realtime guarantee from update() methods. I am not clear on the threading strategy --- if one thread hits all the update() methods then the update() methods absolutely can't do shenanigans like time delays, while if each one gets a thread that's a little more acceptable.