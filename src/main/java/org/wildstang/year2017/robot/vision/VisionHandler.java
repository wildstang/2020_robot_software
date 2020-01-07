package org.wildstang.year2017.robot.vision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import org.wildstang.framework.core.Core;
import org.wildstang.year2017.robot.RobotTemplate;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionHandler implements Runnable {

    final int CORRECTION_LEVEL_INDEX = 0;
    final int DISTANCE_INDEX = 1;
    final int SET_INDEX = 2;
    final int IMAGE_INDEX = 3;

    private Socket m_socket;
    private boolean m_running;
    private InputStream m_inputStream;
    private OutputStream m_outputStream;
    private VisionServer m_visionServer;
    private String m_ip;
    BufferedReader m_in = null;
    PrintWriter m_out = null;

    private int h_min;
    private int s_min;
    private int v_min;
    private int h_max;
    private int s_max;
    private int v_max;
    private int center;
    private int threshold;
    private double blurRadius;

    final private int H_MIN_DEFAULT = 64;
    final private int S_MIN_DEFAULT = 0;
    final private int V_MIN_DEFAULT = 0;
    final private int H_MAX_DEFAULT = 154;
    final private int S_MAX_DEFAULT = 255;
    final private int V_MAX_DEFAULT = 255;
    final private int CENTER_DEFAULT = 0;
    final private int THRESHOLD_DEFAULT = 10;
    final private double BLUR_RADIUS_DEFAULT = 5.0;
    final private String H_MIN_KEY = ".h_min";
    final private String S_MIN_KEY = ".s_min";
    final private String V_MIN_KEY = ".v_min";
    final private String H_MAX_KEY = ".h_max";
    final private String S_MAX_KEY = ".s_max";
    final private String V_MAX_KEY = ".v_max";
    final private String CENTER_KEY = ".center";
    final private String THRESHOLD_KEY = ".threshold";
    final private String BLUR_RADIUS_KEY = ".blur";

    private long m_lastMsgReceived;

    public VisionHandler(VisionServer p_server, Socket p_socket) {
        m_socket = p_socket;
        m_visionServer = p_server;
        m_ip = p_socket.getInetAddress().getHostAddress();

        h_min = Core.getConfigManager().getConfig().getInt(this.getClass().getName() + H_MIN_KEY,
                H_MIN_DEFAULT);
        s_min = Core.getConfigManager().getConfig().getInt(this.getClass().getName() + S_MIN_KEY,
                S_MIN_DEFAULT);
        v_min = Core.getConfigManager().getConfig().getInt(this.getClass().getName() + V_MIN_KEY,
                V_MIN_DEFAULT);
        h_max = Core.getConfigManager().getConfig().getInt(this.getClass().getName() + H_MAX_KEY,
                H_MAX_DEFAULT);
        s_max = Core.getConfigManager().getConfig().getInt(this.getClass().getName() + S_MAX_KEY,
                S_MAX_DEFAULT);
        v_max = Core.getConfigManager().getConfig().getInt(this.getClass().getName() + V_MAX_KEY,
                V_MAX_DEFAULT);

        center = Core.getConfigManager().getConfig().getInt(this.getClass().getName() + CENTER_KEY,
                CENTER_DEFAULT);
        threshold = Core.getConfigManager().getConfig()
                .getInt(this.getClass().getName() + THRESHOLD_KEY, THRESHOLD_DEFAULT);
        blurRadius = Core.getConfigManager().getConfig()
                .getDouble(this.getClass().getName() + BLUR_RADIUS_KEY, BLUR_RADIUS_DEFAULT);

        if (RobotTemplate.LOG_STATE) {
            Core.getStateTracker().addIOInfo("Image set", "Vision", "Input", null);
            Core.getStateTracker().addIOInfo("Image number", "Vision", "Input", null);
        }
    }

    public boolean isRunning() {
        return m_running;
    }

    @Override
    public void run() {
        String line = null;
        String delims = "[,|]";

        try {
            m_inputStream = m_socket.getInputStream();
            m_in = new BufferedReader(new InputStreamReader(m_inputStream));

            m_outputStream = m_socket.getOutputStream();
            m_out = new PrintWriter(m_outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (m_in != null && m_out != null) {
            System.out.println("Camera connection: " + true);

            // We've just connected - send the required HSV values to the client
            sendPreferences(m_out);

            m_running = true;
            m_lastMsgReceived = System.currentTimeMillis();

            // Start health check thread to see if we're still alive
            startHeartbeatThread();

            while (m_running) {
                // read the value sent from the client and update the current
                // value to be used
                try {
                    line = m_in.readLine();

                    if (line != null) {
                        SmartDashboard.putString("vision string", line);
                        // System.out.println("line: " + line);

                        String[] tokens = line.split(delims);
                        double[] parms = new double[tokens.length];

                        for (int i = 0; i < tokens.length; i++) {
                            parms[i] = Double.parseDouble(tokens[i].trim());
                            System.out.println(parms[i]);
                        }

                        if (parms.length > 0) {
                            m_visionServer.setXCorrectionLevel(parms[CORRECTION_LEVEL_INDEX]);
                        }
                        if (parms.length > 1) {
                            m_visionServer.setDistance(parms[DISTANCE_INDEX]);
                        }
                        // if (RobotTemplate.LOG_STATE)
                        // {
                        // if (parms.length > 2)
                        // {
                        // Core.getStateTracker().addState("Image set", "Vision", parms[SET_INDEX]);
                        // }
                        // if (parms.length > 3)
                        // {
                        // Core.getStateTracker().addState("Image number", "Vision",
                        // parms[IMAGE_INDEX]);
                        // }
                        // }

                        long now = System.currentTimeMillis();
                        SmartDashboard.putNumber("Vision update delta", (now - m_lastMsgReceived));
                        SmartDashboard.putNumber("Distance", m_visionServer.getDistance());
                        SmartDashboard.putNumber("xCorrection",
                                m_visionServer.getXCorrectionLevel());

                        m_lastMsgReceived = now;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Camera connection: " + false);
        }

    }

    public void enableVideoLogging() {
        if (m_out != null) {
            m_out.println('b');
            m_out.flush();
        }
    }

    public void disableVideoLogging() {
        if (m_out != null) {
            m_out.println('e');
            m_out.flush();
        }
    }

    private void startHeartbeatThread() {
        Thread t = new Thread(new Heartbeat(this));
        t.start();
    }

    class Heartbeat implements Runnable {
        VisionHandler m_handler;
        boolean m_running = false;

        Heartbeat(VisionHandler handler) {
            m_handler = handler;
        }

        public void run() {
            m_running = true;

            while (m_running) {
                long timeSinceLastMsg = (System.currentTimeMillis() - m_lastMsgReceived);
                if (timeSinceLastMsg > 2000) {
                    // TODO: Flag handler as inactivef
                    SmartDashboard.putBoolean("Camera maybe offline", true);
                    SmartDashboard.putNumber("Time since last camera update", timeSinceLastMsg);
                } else {
                    SmartDashboard.putBoolean("Camera maybe offline", false);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            m_running = false;
        }
    }

    private void sendPreferences(PrintWriter p_out) {
        StringBuffer buf = new StringBuffer();

        buf.append(h_min);
        buf.append("|");
        buf.append(s_min);
        buf.append("|");
        buf.append(v_min);
        buf.append("|");
        buf.append(h_max);
        buf.append("|");
        buf.append(s_max);
        buf.append("|");
        buf.append(v_max);
        buf.append("|");
        buf.append(center);
        buf.append("|");
        buf.append(threshold);
        buf.append("|");
        buf.append(blurRadius);
        buf.append("\n");

        p_out.println(buf.toString());
        p_out.flush();
    }

    public void stop() {
        m_running = false;

        try {
            if (m_inputStream != null) {
                m_inputStream.close();
            }

            if (m_outputStream != null) {
                m_outputStream.close();
            }

            if (m_socket != null) {
                m_socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getIP() {
        return m_ip;
    }

}
