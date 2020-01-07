package org.wildstang.framework.config;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.CoreUtils;

/**
 * This class is the public interface to accessing and working with the
 * configuration.
 *
 * This class is responsible for reading the configuration from the file into
 * the Config object, and providing access to the Config object.
 *
 * This class is a singleton.
 *
 * @author Steve
 *
 */
public class ConfigManager {
    private static Logger s_log = Logger.getLogger(ConfigManager.class.getName());
    private static final String s_className = "ConfigManager";

    private ArrayList<ConfigListener> m_listeners = new ArrayList<>(5);
    private boolean m_initialised = false;

    private Config m_config;

    public ConfigManager() {
    }

    public void init() {
        s_log.entering(s_className, "init");

        if (!m_initialised) {
            m_config = new Config();
            m_initialised = true;
        }

        s_log.exiting(s_className, "init");
    }

    public Config getConfig() {
        return m_config;
    }

    public void loadConfig(BufferedReader p_reader) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "loadConfig");
        }

        m_config.load(p_reader);

        if (s_log.isLoggable(Level.FINE)) {
            s_log.fine("Notifying listeners of config change");
        }
        notifyListeners();

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "load");
        }
    }

    public void addConfigListener(ConfigListener p_listener) {
        CoreUtils.checkNotNull(p_listener, "p_listener is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "addConfigListener");
        }

        // Only add the listener if it does not exist in the list already
        if (!m_listeners.contains(p_listener)) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Listener does not exist - adding listener");
            }
            m_listeners.add(p_listener);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "addConfigListener");
        }
    }

    public void removeConfigListener(ConfigListener p_listener) {
        CoreUtils.checkNotNull(p_listener, "p_listener is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "removeConfigListener");
        }

        if (m_listeners.contains(p_listener)) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Listener exists in manager - removing listener");
            }
            m_listeners.remove(p_listener);
        } else {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Listener does not exist in manager");
            }
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "removeConfigListener");
        }
    }

    public List<ConfigListener> getConfigListeners() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getConfigListeners");
        }

        // Make a copy of the list so that callers are not working on the internal list
        ArrayList<ConfigListener> copy = new ArrayList<>(m_listeners);

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "getConfigListeners");
        }

        return copy;
    }

    /**
     * Notifies all listeners that the config has changed and they should reload any
     * values.
     */
    protected void notifyListeners() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "notifyListeners");
        }

        // Notify listeners

        for (ConfigListener listener : m_listeners) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Notifying config listener: " + listener);
            }

            listener.notifyConfigChange(m_config);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "notifyListeners");
        }
    }

}
