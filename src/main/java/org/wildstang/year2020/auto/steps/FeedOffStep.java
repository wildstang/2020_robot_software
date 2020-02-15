package org.wildstang.year2020.auto.steps;

import org.wildstang.framework.auto.steps.AutoStep;
import org.wildstang.year2020.subsystems.ballpath.Ballpath;

public class FeedOffStep extends AutoStep{

    private Ballpath Feed;

    public void update() {
        Feed.turnOffFeed();
        this.setFinished(true);
    }
    public String toString(){
        //put a reasonable name for this step inside the string
        return "Feed Off";
    }
    public void initialize(){
        Feed = new Ballpath();
    }
}