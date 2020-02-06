import org.wildstang.framework.auto.steps.AutoStep;

import java.io.File;

import org.wildstang.framework.core.Core;
import org.wildstang.year2020.robot.WSSubsystems;
import org.wildstang.year2020.subsystems.drive.Drive;
import org.wildstang.year2020.subsystems.drive.Path;
import org.wildstang.year2020.subsystems.drive.PathFollower;
import org.wildstang.year2020.subsystems.drive.PathReader;
import org.wildstang.year2020.subsystems.drive.Trajectory;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;


import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnOffFeedStep extends AutoStep {
private Ballpath turnOffFeedAuto;
    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        turnOffFeedAuto = new Ballpath();


    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        turnOffFeedAuto.turnOffFeed();
        setFinished(true);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Turn Off Feed Step";
    }
    
}