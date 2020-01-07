/* Placeholder --- no line detector code yet. This might be unnecessary as line detection might happen on RasPi instead.*/

package org.wildstang.year2019.subsystems.strafeaxis;

import java.io.FileOutputStream;

//import com.sun.tools.classfile.TypeAnnotation.Position;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
   130  114  96   82   56   40   24   08 0 08   24   40   56   82   96   114  130
   NA - 06 - 05 - 04 - 03 - 02 - 01 - 07 - 15 - 14 - 13 - 12 - 11 - 10 - 09 - 08
   C
   */

//

public class LineDetector extends Thread {
    private SerialPort arduino;
    private byte pass[] = { 10, (byte) 150, 58, 9 };
    private int matchtime = 150;

    // What line widths to try (units of line sensor spacing)
    private static final double LINE_WIDTHS[] = {2.5, 3, 3.5};
    // What line brightnesses to try (units of whatever the bogus units we get are)
    private static final double LINE_BRIGHTNESSES[] = {200, 250, 300};
    // How finely to divide the spacing search (units = multiples of sensor spacing)
    private static final double LINE_SEARCH_SPACING = .1;
    // notional cost of no line detect
    private static final double NO_LINE_COST = 1600000;

    //private static int[] SENSOR_CONSTANTS =
    //    {-130, -114, 96, -82, -56, -40, -24, -8, 0, 8, 24, 40, 56, 82, 96, 114, 130};
    //private static double TICKS_PER_MM = 17.746;
    boolean arduinoActive;
    private int linePosition; // linePosition ranging from 0 to 255
    public static double AVERAGE_CONSTANSTS[] = new double[16];
    public double average[] = new double[16];
    public double offFromAverage[] = new double[16];
    public boolean ArduinoActive = false;
    public int running = 0;

    private double valuesFromArduino[] = new double[16];
    private int linePositionFromArduino;

    public LineDetector() {
        try {
            arduino = new SerialPort(9600, Port.kUSB);
            System.out.println("Connected to kUSB");
            arduinoActive = true;
        } catch (Exception e) {
            System.out.println("Falied to connect to kUSB.  Attempting to connect to kUSB1");
            try {
                arduino = new SerialPort(9600, Port.kUSB1);
                System.out.println("Connected to kUSB1");
                arduinoActive = true;
            } catch (Exception e1) {
                System.out.println("Falied to connect to kUSB1.  Attempting to connect to kUSB2");
                try {
                    arduino = new SerialPort(9600, Port.kUSB2);
                    System.out.println("Connected to kUSB2");
                    arduinoActive = true;
                } catch (Exception e2) {
                    System.out.println("Failed to connect");
                }
            }
        }
        try {
            arduino.setReadBufferSize(1);
        } catch (NullPointerException e) {
            System.out.println("Threw nullptr in LineDetector init!");
        }
    }

    public void run() {
        SmartDashboard.putBoolean("Arduino Active", arduinoActive);
        while (arduinoActive) {
            running++;
            SmartDashboard.putNumber("Running", running);
            readLinePositionFromArduino();
            SmartDashboard.putNumber("Arduino Strafe Target LD", linePosition);
        }
    }

    private byte readByte() {
        byte result = arduino.read(1)[0];
        // System.out.println(result);
        return result;
    }

    private void readLinePositionFromArduino() {
        byte byteRead = readByte();
        while (byteRead != -1) {
            byteRead = readByte();
        }
        for (int i = 0; i < 16; ++i) {
            byteRead = readByte();
            if (byteRead == -1) {
                System.out.println("Communications glitch with Arduino");
            }
            int valueRead = makeUnsigned(byteRead);
            valuesFromArduino[i] = valueRead;
        }
        byteRead = readByte();
        linePositionFromArduino = makeUnsigned(byteRead);
        // linePosition = linePositionFromArduino;

        // experimental -- no effect yet
        inferLinePosition();

        if (running % 10 == 0) {
            SmartDashboard.putNumberArray("Light Sensor Values", valuesFromArduino);
            SmartDashboard.putNumber("Arduino line position", linePositionFromArduino);
        }
    }

    /** cost of assigning the line position to this point
     * position: position of line in multiples of sensor spacing (can be fractional)
     * width: width of line
     * depth: how bright line is
     */
    private double linePositionCost(double position, double width, double depth) {
        double cost = 0;
        for (int i = 0; i < 16; ++i) {
            double expected = 255 - gaussPDF((i - position) / width) * depth;
            double error = valuesFromArduino[i] - expected;
            //System.out.println(expected + " " + error + " " + error*error);
            cost += error * error; 
        }
        //System.out.println(cost);
        return cost;
    }

    // PDF of the unit gaussian
    private static double gaussPDF(double x) {
        return (1/Math.sqrt(2 * Math.PI)) * Math.exp(-x * x / 2);
    }

    private void inferLinePosition() {
        double bestCost = 1.0/0.0; // infinity
        double bestWidth = -1;
        double bestDepth = -1;
        double bestPosition = -1;
        for (double width : LINE_WIDTHS) {
            for (double depth : LINE_BRIGHTNESSES) {
                for (double position = 0; position <= 16; position += LINE_SEARCH_SPACING) {
                    double cost = linePositionCost(position, width, depth);
                    if (cost < bestCost) {
                        bestWidth = width;
                        bestDepth = depth;
                        bestPosition = position;
                        bestCost = cost;
                    }
                }
            }
        }
        if (bestCost < NO_LINE_COST) {
            // TODO uncomment below to enable this
            linePosition = (int)(bestPosition / 16 * 255);
        }
        if (running % 10 == 0) {
            SmartDashboard.putNumber("Line detection cost", bestCost);
            SmartDashboard.putNumber("Line detection line width", bestWidth);
            SmartDashboard.putNumber("Line detection line brightness", bestDepth);
            SmartDashboard.putNumber("Line detection line position out of 255", bestPosition / 16 * 255);
        }
    }

    public int getLineSensorData() throws NullPointerException {
        SmartDashboard.putNumber("Final LD", linePosition);
        return linePosition;
    }

    public static int makeUnsigned(byte byteRead) {
        return (byteRead + 0x100) % 0x100;
    }

    /*
     * 130 114 96 82 56 40 24 08 0 08 24 40 56 82 96 114 130 NA - 06 - 05 - 04 - 03
     * - 02 - 01 - 07 - 15 - 14 - 13 - 12 - 11 - 10 - 09 - 08 C
     */

}
