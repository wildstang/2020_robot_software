package org.wildstang.hardware.crio.inputs;

import org.wildstang.framework.io.inputs.I2CInput;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class WsI2CInput extends I2CInput {

    private I2C i2c;
    // MessageHandler m_handler;

    public WsI2CInput(String name, Port port, int p_address) {
        super(name);

        i2c = new I2C(port, p_address);

        // m_handler = new MessageHandler(i2c);
        // Thread t = new Thread(m_handler);
        // t.start();
    }

    @Override
    protected byte[] readRawValue() {
        // byte[] data = m_handler.getRecentData();
        byte rcvBytes[] = new byte[1];

        i2c.readOnly(rcvBytes, 1);

        return rcvBytes;// data;
    }

    // private static class MessageHandler implements Runnable
    // {
    // // Offload to a thread avoid blocking main thread with I2C reads.
    //
    // byte[] rcvBytes;
    // boolean running = true;
    // private I2C i2c;
    //
    // public MessageHandler(I2C p_i2c)
    // {
    // // Get ourselves an i2c instance to read data.
    // i2c = p_i2c;
    // }
    //
    // @Override
    // public void run()
    // {
    // while (running)
    // {
    // rcvBytes = new byte[1];
    // // Read a single byte
    // i2c.readOnly(rcvBytes, 1);
    // }
    // }
    //
    // public byte[] getRecentData()
    // {
    // return rcvBytes;
    // }
    //
    // public void stop()
    // {
    // running = false;
    // }
    // }
}
