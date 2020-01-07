package org.wildstang.year2019.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.hardware.crio.inputs.WsAnalogGyro;
import org.wildstang.year2019.robot.WSSubsystems;

import org.wildstang.year2019.subsystems.drive.Drive;

/**
 * TODO: description of what this auto step does goes here.
 *
 * Questions: in the 2017 AutoSteps program, they have both
 * TurnByNDegreesStep.java and TurnByNDegreesStepMagic.java.
 * 
 * What are the differences between the two and do we need to implement some
 * 'Magic' into steps as well?
 * 
 * XXX TODO: grab a mentor and go over                                   
 * https://github.com/wildstang/2019_robot_software/blob/master/design_docs/year2019/drive.md
 * before using or adding to this class.
 */
public class TurnNDegreesStepDrive extends AutoStep {

    // variables to use
    private Drive drive;
    private WsAnalogGyro gyro;

    private double degreesToTurn;

    public TurnNDegreesStepDrive(double degrees) {
        // construct stuff
    }

    public void initialize() {
        // init
    }

    public void update() {
        // update
    }

    private double calculateRotationSpeed(int current, int target, int tolerance) {
        double rotationSpeed = 0.0;

        // similar to the TurnNDegreesStepDrive from the 2017 AutoSteps..
        int difference = target - current;
        int distanceToTarget = Math.abs(difference);
        int direction = 1;

        if (current > target) {
            if (distanceToTarget >= 180) {
                direction = 1;
            } else {
                direction = -1;
            }
        }
        if (current < target) {
            if (distanceToTarget >= 180) {
                direction = -1;
            } else {
                direction = 1;
            }
        }

        if (distanceToTarget >= 180) {
            distanceToTarget = 360 - distanceToTarget;
        }

        // TODO: find the speed of the motor and initialize how fast to rotate
        // add code here... (is it really the same as the 2017 code? hmm)

        if (distanceToTarget <= tolerance) {
            rotationSpeed = 0.0;
        }

        rotationSpeed = rotationSpeed * direction;

        return rotationSpeed;
    }

    // TODO: do we need a getCompassHeading? what does that do?

    // TODO: do we need a modAngle(double initAngle)? what does that do?

    public String toString() {
        return "TurnNDegreesStepDrive";
    }
}
