package org.wildstang.framework.io;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.CoreUtils;

/**
 * This class in the manager for all outputs.
 *
 * @author Steve
 *
 */

/*
 * TODO: this is only inhabitant of IOutputManager. Collapse interface and
 * inhabitant.
 */

public class OutputManager implements IOutputManager {
    private static Logger s_log = Logger.getLogger(OutputManager.class.getName());
    private static final String s_className = "OutputManager";

    private HashMap<String, Output> m_outputs = new HashMap<>();
    private boolean s_initialised = false;

    public OutputManager() {
    }

    @Override
    public void init() {
        s_log.entering(s_className, "init");

        if (!s_initialised) {
            s_initialised = true;
        }

        s_log.exiting(s_className, "init");
    }

    @Override
    public void update() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "update");
        }

        // Iterate over all outputs and update each one
        for (Output out : m_outputs.values()) {
            if (out.isEnabled()) {
                if (s_log.isLoggable(Level.FINEST)) {
                    s_log.finest("Updating Output: " + out.getName());
                }

                // Update the output - send value to output
                out.update();
            } else {
                if (s_log.isLoggable(Level.FINEST)) {
                    s_log.finest("Output " + out.getName() + " is disabled. Not calling update.");
                }
            }
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "update");
        }
    }

    @Override
    public void addOutput(Output p_output) {
        CoreUtils.checkNotNull(p_output, "p_output is null");

        if (!m_outputs.containsKey(p_output.getName())) {
            m_outputs.put(p_output.getName(), p_output);
        }
    }

    @Override
    public void removeOutput(Output p_output) {
        CoreUtils.checkNotNull(p_output, "p_output is null");

        if (s_log.isLoggable(Level.WARNING)) {
            s_log.warning("Removing output " + p_output.getName());
        }
        m_outputs.remove(p_output.getName());
    }

    @Override
    public Output getOutput(String p_name) {
        CoreUtils.checkNotNull(p_name, "p_name is null");
        Output output = null;

        if (!m_outputs.containsKey(p_name)) {
            throw new NoSuchElementException("No input with name '" + p_name + "' in InputManager");
        }
        output = m_outputs.get(p_name);

        return output;
    }

    @Override
    public boolean contains(String p_name) {
        CoreUtils.checkNotNull(p_name, "p_name is null");
        return m_outputs.containsKey(p_name);
    }

    @Override
    public int size() {
        return m_outputs.size();
    }

    @Override
    public void removeAll() {
        m_outputs.clear();
    }

    @Override
    public HashMap<String, Output> getHashMap() {
        return new HashMap<>(m_outputs);
    }
}
