# Talon SRX API changes

CTRE has (a summary)[https://github.com/CrossTheRoadElec/Phoenix-Documentation/blob/master/Migration%20Guide.md].

Some types were renamed and they don't mention this: 
- TalonControlMode is now ControlMode
- new type SensorCollection
- StatusFrameRate is now StatusFrame and constants are renamed

[CTRE example code](https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages)

####Follower (copied from docs)
Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output. Users will still need to set the motor controller's direction and neutral mode.

There are two methods for creating a follower motor controller. The first method `set(ControlMode.follower, IDofMotorController)` allows users to create a motor controller follower of the same model, talon to talon, or victor to victor.

The second method `follow()` allows users to create a motor controller follower of not only the same model, but also other models, talon to talon, victor to victor, talon to victor, and victor to talon.

    /* The first line, we have a Victor following a Talon. The follow() function may also be used to create Talon follower for a Victor */
    victorFollower.follow(Hardware.TalonMaster);
    /* In the second line, we have a Talon following Talon. The set(ControlMode.Follower, MotorcontrollerID) creates followers of the same model. */
    talonFollower.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, 6);