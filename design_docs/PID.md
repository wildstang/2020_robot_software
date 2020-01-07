# PID

[PID control][wikipedia].

WildStang classes for PID control are in `org.wildstang.framework.pid`. In addition, the Talon SRX motor controllers have built-in PID on board, so the WildStang PID code may be obsolete. TODO: verify this.

## Why PID?

Suppose we want the robot to drive forward at a certain speed. We could set the motors to a certain power based on what speed we want. The robot would indeed drive forward at some speed, but it wouldn't be very precise. IF the battery were a little run-down or the robot were a little heavier, it might move more slowly; if the robot were lighter or the battery fresher, it might move faster.

If a human driver were operating the robot and saw it was moving too quickly, they could ease off the throttle in order to get back to the desired speed. This process of detecting the error (too fast) and correcting it (less throttle) forms a *feedback loop*, where an attentive driver can control the speed of the robot to be exactly what they want.

However, humans are profoundly fallible, and the robot still needs to be able to drive precisely in autonomous. Therefore it's important to teach the robot how to have a feedback loop of its own without human intervention. There are many ways to do this, but a very popular and easily-understood approach to closed-loop control is [PID control][wikipedia].

## Why not PID?
However, PID is not a panacea. The way PID works is by detecting and minimizing error; by definition, then, if PID control kicks in, the robot has made a mistake. PID does not *prevent* error.

[wikipedia]: https://en.wikipedia.org/wiki/PID_controller "Wikipedia: PID Controller"