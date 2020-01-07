package org.wildstang.framework.io.inputs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.InputListener;
import org.wildstang.framework.logger.StateTracker;

//import com.sun.jndi.cosnaming.CNNameParser;

/**
 * This is an abstract implementation of the Input interface. This class
 * implements the Input listener mechanism.
 *
 * @author Steve
 *
 */
public abstract class AbstractInput implements Input {

    private static Logger s_log = Logger.getLogger(AbstractInput.class.getName());
    private static final String s_className = "AbstractInput";

    private ArrayList<InputListener> m_listeners = new ArrayList<>(5);
    private String m_name = "Undefined";
    private boolean m_valueChanged = false;
    private boolean m_enabled = true;

    private StateTracker m_stateTracker;

    public AbstractInput(String p_name) {
        CoreUtils.checkNotNull(p_name, "p_name is null");

        m_name = p_name;
    }

    @Override
    public void addInputListener(InputListener p_listener) {
        CoreUtils.checkNotNull(p_listener, "p_listener is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "addInputListener");
        }

        // Only add the listener if it does not exist in the list already
        if (!m_listeners.contains(p_listener)) {
            m_listeners.add(p_listener);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "addInputListener");
        }
    }

    @Override
    public void removeInputListener(InputListener p_listener) {
        CoreUtils.checkNotNull(p_listener, "p_listener is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "removeInputListener");
        }

        for (int i = 0; i < m_listeners.size(); i++) {
            if (m_listeners.get(i).equals(p_listener)) {
                m_listeners.remove(i);
            }
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "removeInputListener");
        }
    }

    @Override
    public List<InputListener> getInputListeners() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getInputListeners");
        }

        // Make a copy of the list so that callers are not working on the internal list
        ArrayList<InputListener> copy = new ArrayList<>(m_listeners);

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "getInputListeners");
        }

        return copy;
    }

    protected void notifyListeners() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "notifyListeners");
        }

        // If the value changed, notify listeners
        if (hasValueChanged()) {
            if (s_log.isLoggable(Level.FINEST)) {
                s_log.finest("Input " + getName() + ": value has changed - notifying listeners");
            }

            for (InputListener listener : m_listeners) {
                if (s_log.isLoggable(Level.FINER)) {
                    s_log.finer("Notifying input listener: " + listener);
                }
                listener.inputUpdate(this);
            }
        } else {
            if (s_log.isLoggable(Level.FINEST)) {
                s_log.finest("Input value has not changed - not notifying");
            }
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "notifyListeners");
        }
    }

    @Override
    public void update() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "update");
        }

        // Read the raw input state
        if (s_log.isLoggable(Level.FINER)) {
            s_log.finer("Reading data from input");
        }
        readDataFromInput();

        logCurrentState();

        // Notify any listeners
        notifyListeners();

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "update");
        }
    }

    protected void logCurrentState() {
        // Log any state information
        if (m_stateTracker != null) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Logging input state");
            }
            logCurrentStateInternal();
        }
    }

    @Override
    public String getName() {
        return m_name;
    }

    protected void setValueChanged(boolean p_changed) {
        m_valueChanged = p_changed;
    }

    protected boolean hasValueChanged() {
        return m_valueChanged;
    }

    protected abstract void logCurrentStateInternal();

    protected abstract void readDataFromInput();

    @Override
    public void removeAllListeners() {
        m_listeners.clear();
    }

    @Override
    public void setStateTracker(StateTracker p_tracker) {
        m_stateTracker = p_tracker;
    }

    public StateTracker getStateTracker() {
        return m_stateTracker;
    }

    @Override
    public void enable() {
        m_enabled = true;
    }

    @Override
    public void disable() {
        m_enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return m_enabled;
    }

}
