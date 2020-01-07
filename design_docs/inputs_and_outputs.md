# Inputs and Outputs

The WS framework has a concept of *inputs* and *outputs*. An *output* is a signal from the RIO to another device; an *input* is a signal from another device to the RIO. An input could be, for example, a sensor or a network message from another device. An output could be, for example, a motor or a network message to another device.

CAVEAT: The CAN motor controllers are NOT SET UP AS OUTPUTS. Unfortunately, their API is complicated and doesn't really lend itself to the Output abstraction. The drive base subsystem handles them separately.

TODO: Prove me wrong and roll the TalonSRX and VictorSPX into the outputs system. We could do this if each control mode were a separate output. If outputs could be enabled and disabled, then we could create a TalonSRXVelocity output, a TalonSRXPosition output, etc. and when you activate one, it disables the rest. Distinct output for each PID profile and all that. Then the complex API reduces back down to scalar outputs.

## Code locations

-  `org.wildstang.framework.io`: The bulk of inputs and outputs framework code.
-  `org.wildstang.framework.hardware`: Base classes for device-specific IO code in `org.wildstang.hardware`, as well as network remote inputs and outputs.
-  `org.wildstang.hardware`: Device-specific IO code. e.g. class to support the LIDAR device is org.wildstang.hardware.inputs.WSLidar.
-  `org.wildstang.yearXXXX.robot`: Configuration of what inputs and outputs are present on each robot in WSInputs and WSOutputs enums.

TODO: We may want to examine whether inputs and outputs could be factored together. Much of the bookkeeping code that controls i/o is common between input and output; actually, much of the bookkeeping around items that need regular updating is common. We might be able to DRY out this design a little.

## Types

Here described for output; input is the same.

An `Output` is the object that's actually used to interact with the output during robot run.

An `Outputs` is a configuration object hardcoded into the source that specifies what name and type the output is, and contains an OutputConfig.

`OutputConfig` is defined in `org.wildstang.framework.hardware`. An `OutputConfig` is a configuration object hardcoded into the source that contains type-specific information on how this output should be set up; e.g. for a Victor motor output, motor parameters would go in a `WSVictorConfig` that implements `OutputConfig`.

We should probably rename something here, but I'm really not sure what. Perhaps the `Outputs` -> `OutputConfig` relationship should be refactored from membership to inheritance, so that the type currently known as `Outputs` can instead be called `OutputConfig`.

## Configuration
The outputs available on the robot should be defined in the `org.wildstang.yearXXXX.robot.WSOutputs` enum.

The inputs available on the robot should be defined in the `org.wildstang.yearXXXX.robot.WSInputs` enum.

Each `Inputs` or `Outputs` in the enum contains an `InputConfig` or `OutputConfig` object inside it that will be passed to the `Input` or `Output` that will be created during initialization.

## Initialization
During robot startup, `Robot.robotInit` calls `Core.createOutputs` in org.wildstang.framework.core.Core.

For each `Outputs` in `WSOutputs`, createOutputs creates an `Output` (not to be confused with `Outputs`) and uses the `OutputManager`'s `addOutput()` to add the `Output` (not `Outputs`) to the `OutputManager`.

The parallel is true for input. For input, any object (like a subsystem) that wants to know about changes to an input must subscribe to the input e.g. 


        headingInput = (AnalogInput) Core.getInputManager()
                                         .getInput(WSInputs.HEADING.getName());
        headingInput.addInputListener(this);

## Operation
`Robot.autonomousPeriodic` and `Robot.teleopPeriodic` periodically call `Core.executeUpdate()`. `Core.executeUpdate()` calls `update()` methods of `InputManager` and `OutputManager`, which call the `update()` methods of individual inputs and outputs.

Inputs and outputs call inputUpdate method of objects subscribve