/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.hardware.crio.outputs;

import org.wildstang.framework.io.outputs.AnalogOutput;

import edu.wpi.first.wpilibj.Servo;

/**
 *
 */
public class WsServo extends AnalogOutput {

    Servo servo;

    public WsServo(String name, int channel, double p_default) {
        super(name, p_default);

        this.servo = new Servo(channel);
    }

    @Override
    public void sendDataToOutput() {
        servo.setAngle(getValue());
    }

    public void notifyConfigChange() {
    }
}
