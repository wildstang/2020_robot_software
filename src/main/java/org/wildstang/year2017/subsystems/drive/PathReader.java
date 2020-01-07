package org.wildstang.year2017.subsystems.drive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.ctre.phoenix.motion.TrajectoryPoint;

public class PathReader {

    public static Trajectory readTrajectory(File p_path) {
        Trajectory trajectory = new Trajectory();

        ArrayList<String> rawData = new ArrayList<String>();

        // Open the file
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(p_path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (reader != null) {
            String line;
            try {
                // Read all the lines. Sort into left and right paths
                while ((line = reader.readLine()) != null) {
                    rawData.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        double[][] dataPoints = new double[rawData.size()][];
        TrajectoryPoint mpPoint = null;
        ArrayList<TrajectoryPoint> trajPoints = new ArrayList<TrajectoryPoint>();

        // Parse into numbers
        for (int i = 0; i < rawData.size(); i++) {
            String tempLine = rawData.get(i);
            StringTokenizer st = new StringTokenizer(tempLine, ",\n");

            dataPoints[i] = new double[3];
            double rotations = Double.parseDouble(st.nextToken());
            double velocity = Double.parseDouble(st.nextToken());
            double interval = Double.parseDouble(st.nextToken());

            dataPoints[i][0] = rotations;
            dataPoints[i][1] = velocity;
            dataPoints[i][2] = interval;

            // Create a TrajectoryPoint for the Talon - do this while reading the file
            mpPoint = new TrajectoryPoint();
            mpPoint.position = rotations;
            mpPoint.velocity = velocity;
            // FIXME The durations are quantized now! We can't put in an arbitrary duration.
            // mpPoint.timeDur = (int) interval;
            mpPoint.profileSlotSelect0 = 0; // which set of gains would you like to use?
            // FIXME no analog for velocityOnly now?
            // mpPoint.velocityOnly = false; // set true to not do any position servo, just velocity
            //                               // feedforward
            mpPoint.zeroPos = false;

            if (i == 0) {
                mpPoint.zeroPos = true; // set this to true on the first point
            }

            mpPoint.isLastPoint = false;

            if ((i + 1) == rawData.size()) {
                mpPoint.isLastPoint = true; // set this to true on the last point
            }

            trajPoints.add(mpPoint);
        }

        trajectory.setTrajectoryPoints(dataPoints);
        trajectory.setTalonPoints(trajPoints);

        return trajectory;
    }
}
