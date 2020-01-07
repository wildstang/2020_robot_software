# Auto

During the first 15 seconds of each match, the robot must operate autonomously without the driver's help. 

## Code locations & organization

- `org.wildstang.framework.auto`: Framework code for autonomous mode. Although the autos change each year, the idea of how to do an auto (go here, then go here, then do this, etc.) stays the same; those common classes reside here.

    - `.program`: auto programs available every year, like `Sleeper` (does nothing).
    
    - `.steps`: Building blocks to build auto programs out of. The base class for robot-specific auto steps is `AutoStep`, defined here.


- `org.wildstang.yearXXXX.auto`: Code defining autonomous behavior for this particular robot.

    - `.programs`: Auto programs that the driver can select and run.
    
    - `.steps`: Building blocks that we use to write the auto programs. Custom auto steps for this robot will inherit from `org.wildstang.framework.auto.AutoStep`.
    
    - `.testprograms`: Test programs that are not intended for competition use.
    
## TODO: more documentation here on how to write autos