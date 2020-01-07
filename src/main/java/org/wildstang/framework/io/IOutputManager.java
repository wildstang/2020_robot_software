package org.wildstang.framework.io;

import java.util.HashMap;

public interface IOutputManager {

    public void init();

    /**
     * Updates all outputs registered with the manager.
     */
    public void update();

    public void addOutput(Output p_output);

    public void removeOutput(Output p_output);

    public Output getOutput(String p_name);

    public int size();

    public void removeAll();

    public boolean contains(String p_name);

    public HashMap<String, Output> getHashMap();

}