package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;

public class FeedOnStep extends AutoStep{

    private Ballpath Feed;

    public void update() {
        Feed.turnOnFeed();
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Feed On";
    }
    public void initialize(){
        Feed = new Ballpath();
    }
}