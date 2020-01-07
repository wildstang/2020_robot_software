package org.wildstang.year2019.subsystems.drive;

import com.ctre.phoenix.motion.TrajectoryPoint;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class PathReader {

    private static int modifier = 1;

    public static Trajectory readTrajectory(File p_path, boolean isForwards) {
        Reader in;
        List<CSVRecord> records;
        try {
            in = new FileReader(p_path);
            records = CSVFormat.EXCEL.withHeader("Delta Time","X Point","Y Point","Position","Velocity","Acceleration","Jerk","Heading").parse(in).getRecords();
        } catch (Exception e) {
            for (int i = 0; i < 100; ++i) {
                System.out.println("FAILED TO READ PATH!");
            }
            return new Trajectory();
        }
        if (records.get(0).get("Delta Time").equals("Delta Time")){
            records.remove(0);
        }
        

        if (isForwards) modifier = 1;
        if (!isForwards) modifier = -1;

        ArrayList<TrajectoryPoint> trajPoints = new ArrayList<TrajectoryPoint>();
        double[][] dataPoints = new double[records.size()][3];
        Trajectory trajectory = new Trajectory();
        TrajectoryPoint mpPoint = null;

        int i = 0;
        for (CSVRecord record : records) {
            mpPoint = new TrajectoryPoint();
            dataPoints[i] = new double[3];

            dataPoints[i][0] = Double.parseDouble(record.get("Delta Time"));
            dataPoints[i][1] = modifier * Double.parseDouble(record.get("Position")) * DriveConstants.TICKS_PER_INCH_MOD;
            dataPoints[i][2] = modifier * Double.parseDouble(record.get("Velocity")) * DriveConstants.TICKS_PER_INCH_MOD/10;

            mpPoint.timeDur = (int) dataPoints[i][0];
            mpPoint.position = dataPoints[i][1];
            mpPoint.velocity = dataPoints[i][2];

            mpPoint.profileSlotSelect0 = 0;

            //System.out.println(mpPoint.position);

            if (i == 0) {
                mpPoint.zeroPos = true;
            } else {
                mpPoint.zeroPos = false;
            }

            if (i == records.size() - 1) {
                mpPoint.isLastPoint = true;
            } else {
                mpPoint.isLastPoint = false;
            }

            trajPoints.add(mpPoint);
            ++i;
        }

        trajectory.setTalonPoints(trajPoints);
        trajectory.setTrajectoryPoints(dataPoints);

        return trajectory;
    }

}
