package org.wildstang.year2017.robot.vision;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionServer implements Runnable {
    private int m_port;
    private boolean m_running;
    private ServerSocket m_serverSocket;

    private int m_currentValue;
    double m_xCorrectionLevel;
    double m_distance;

    private VisionHandler m_handler = null;

    public VisionServer(int p_port) {
        m_port = p_port;
    }

    public void startVisionServer() {
        SmartDashboard.putBoolean("Camera", false);

        // Create Server Socket
        try {
            m_serverSocket = new ServerSocket(m_port);
        } catch (IOException e) {

            e.printStackTrace();
        }

        // Start thread
        if (m_serverSocket != null) {
            Thread t = new Thread(this);
            t.start();
            m_running = true;
            SmartDashboard.putBoolean("VisionServer", isRunning());
        }
    }

    public boolean isRunning() {
        return m_running;
    }

    public int getCurrentValue() {
        return m_currentValue;
    }

    public void run() {
        // Listen for clients
        while (m_running) {
            // Accept new connection
            Socket s;
            try {
                s = m_serverSocket.accept();

                // Remove any current handler
                removeCurrentHandler();

                // Create new VisionHandler for requests
                VisionHandler handler = new VisionHandler(this, s);

                startHandler(handler);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void startHandler(VisionHandler handler) {
        if (handler.getIP().equals("10.1.11.10")) {
            SmartDashboard.putBoolean("Camera", true);
        }
        SmartDashboard.putString("Vision server connected to", handler.getIP());

        Thread t = new Thread(handler);
        t.start();
        m_handler = handler;
    }

    private void removeCurrentHandler() {
        if (m_handler != null) {
            m_handler.stop();
        }
        m_handler = null;
        SmartDashboard.putBoolean("Camera", false);
        SmartDashboard.putString("Vision server connected to", "");

        // Reset any values read from camera to 0 to remove any residual values
        resetReadState();

    }

    private void resetReadState() {
        m_xCorrectionLevel = 0;
        m_distance = 0;
    }

    public void updateValue(int p_value) {
        m_currentValue = p_value;
    }

    public void shutdown() {
        m_running = false;

        removeCurrentHandler();

        SmartDashboard.putBoolean("VisionServer", isRunning());
    }

    public void setXCorrectionLevel(double newVal) {
        m_xCorrectionLevel = newVal;
    }

    public double getXCorrectionLevel() {
        return m_xCorrectionLevel;
    }

    public void setDistance(double newVal) {
        m_distance = newVal;
    }

    public double getDistance() {
        return m_distance;
    }

    public void startVideoLogging() {
        if (m_handler != null) {
            m_handler.enableVideoLogging();
        }
    }

    public void stopVideoLogging() {
        if (m_handler != null) {
            m_handler.disableVideoLogging();
        }
    }

}
