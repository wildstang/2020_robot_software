package org.wildstang.framework.io;

import java.util.HashMap;

import org.wildstang.framework.core.Inputs;

public interface IInputManager {

    public void init();

    /**
     * Updates all inputs registered with the manager.
     */
    public void update();

    public void addInput(Input p_input);

    public void removeInput(Input p_input);

    public Input getInput(String p_name);

    public Input getInput(Inputs p_input);

    public int size();

    public void removeAll();

    public boolean contains(String p_name);

    public HashMap<String, Input> getHashMap();

}