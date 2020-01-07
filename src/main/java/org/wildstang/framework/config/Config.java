package org.wildstang.framework.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.CoreUtils;

/**
 * This class represents the config parameters. It is a map of key/value pairs.
 *
 * @author Steve
 *
 */
public class Config {
    private static Logger s_log = Logger.getLogger(Config.class.getName());
    private static final String s_className = "Config";

    private HashMap<String, Object> m_configMap = new HashMap<>();

    public Config() {
    }

    protected void load(BufferedReader p_reader) {
        CoreUtils.checkNotNull(p_reader, "p_reader is null");

        String currentLine;

        String[] keyValue;
        String key = null;
        String value = null;
        Object parsedValue = null;

        try {
            while ((currentLine = p_reader.readLine()) != null) {
                currentLine = stripComments(currentLine.trim());

                if (currentLine.length() > 0) {
                    // TODO: Error checking
                    // Split token on = to get name/value
                    keyValue = getKeyValuePair(currentLine);

                    key = keyValue[0];
                    if (key != null && key.length() > 0 && keyValue.length > 1) {
                        value = keyValue[1];

                        // Parse value
                        parsedValue = parseValue(value);

                        if (parsedValue != null) {
                            m_configMap.put(key, parsedValue);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Removes any part of a line following a hash character - '#'. Returns any part
     * of the line that comes before that character, which will be an empty string
     * if the line starts with a '#'. If a null string is passed in, a
     * NullPointerException is thrown. The String is trimmed before being returned
     * to remove any whitespace on either end
     *
     * @param p_line
     *            a String representing a line of text
     *
     * @return the String, minus any present # character, and any characters that
     *         follow it, if any. May return an empty String, but will never return
     *         null
     *
     * @throws NullPointerException
     *             if the input String is null
     */
    protected String stripComments(String p_line) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "stripComments");
        }

        CoreUtils.checkNotNull(p_line, "p_line is null");

        String result;

        // TODO: split line into parts and add error checking
        result = p_line.split("#")[0].trim();

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "stripComments");
        }

        return result;
    }

    /**
     * Splits the input String on an equals (=) character, and returns the two parts
     * as an array of two Strings.
     *
     * @param p_line
     *
     * @return
     *
     * @throws NullPointerException
     *             if the String passed in is null
     */
    protected String[] getKeyValuePair(String p_line) {
        String[] result;

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getKeyValuePair");
        }

        result = p_line.split("=");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "getKeyValuePair");
        }

        return result;
    }

    /**
     * Parses the value from a String. It attempts to parse it into a primitive
     * type, returns as its wrapper class. The order it attempts is: double, int,
     * boolean, String
     *
     * @param p_valueStr
     * @return
     */
    protected Object parseValue(String p_valueStr) {
        CoreUtils.checkNotNull(p_valueStr, "p_valueStr is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "parseValue");
        }

        Object result;

        // 1. if it contains . try to parse as double, otherwise take as string
        if (s_log.isLoggable(Level.FINER)) {
            s_log.finer("Parsing value: '" + p_valueStr + "'");
            s_log.finer("Attempting to parse as double");
        }
        result = parseDouble(p_valueStr);

        // 2. parse as int
        if (result == null) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Failed parsing as double. Attempting to parse as int");
            }
            result = parseInt(p_valueStr);
        }

        // 3. if it equals 'true' or 'false' ignoring case, parse as boolean
        if (result == null) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Failed parsing as int. Attempting to parse as boolean");
            }
            result = parseBoolean(p_valueStr);
        }

        // 4. if all these tests or parses fail, leave as string
        if (result == null) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("Failed parsing as boolean. Leaving as String value");
            }
            if (p_valueStr != null && !p_valueStr.equals("")) {
                result = p_valueStr;
            }
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "parseValue");
        }

        return result;
    }

    protected Double parseDouble(String p_valueStr) {
        CoreUtils.checkNotNull(p_valueStr, "p_valueStr is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "parseDouble");
        }

        Double d = null;

        if (p_valueStr.indexOf('.') >= 0) {
            try {
                d = Double.valueOf(p_valueStr);
            } catch (NumberFormatException e) {
            }
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "parseDouble");
        }

        return d;
    }

    protected Integer parseInt(String p_valueStr) {
        CoreUtils.checkNotNull(p_valueStr, "p_valueStr is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "parseInt");
        }

        Integer i = null;

        try {
            i = Integer.valueOf(p_valueStr);
        } catch (NumberFormatException e) {
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "parseInt");
        }

        return i;
    }

    protected Boolean parseBoolean(String p_valueStr) {
        CoreUtils.checkNotNull(p_valueStr, "p_valueStr is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "parseBoolean");
        }

        Boolean b = null;

        if (p_valueStr.equalsIgnoreCase("true") || p_valueStr.equalsIgnoreCase("false")) {
            try {
                b = Boolean.valueOf(p_valueStr);
            } catch (NumberFormatException e) {
            }
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "parseBoolean");
        }

        return b;
    }

    public Object getValue(String p_key) {
        CoreUtils.checkNotNull(p_key, "p_key is null");

        Object result = null;

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getValue");
        }

        result = m_configMap.get(p_key);

        return result;
    }

    public Object getValue(String p_key, Object p_default) {
        CoreUtils.checkNotNull(p_key, "p_key is null");

        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getValue");
        }

        Object value = m_configMap.get(p_key);

        if (value == null) {
            if (s_log.isLoggable(Level.FINER)) {
                s_log.finer("No value for key " + p_key + ". Using default value: " + p_default);
            }

            value = p_default;
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "getValue");
        }

        return value;
    }

    public double getDouble(String p_key, double p_default) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getDouble");
        }

        Object value = getValue(p_key, p_default);
        if (!(value instanceof Double)) {
            throw new NumberFormatException("Value found for " + p_key
                    + " but value was not a Double: " + value.getClass().getName());
        }

        return (Double) value;
    }

    public int getInt(String p_key, int p_default) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getInt");
        }

        Object value = getValue(p_key, p_default);
        if (!(value instanceof Integer)) {
            throw new NumberFormatException("Value found for " + p_key
                    + " but value was not a Integer: " + value.getClass().getName());
        }

        return (Integer) value;
    }

    public boolean getBoolean(String p_key, boolean p_default) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getBoolean");
        }

        Object value = getValue(p_key, p_default);
        if (!(value instanceof Boolean)) {
            throw new NumberFormatException("Value found for " + p_key
                    + " but value was not a Boolean: " + value.getClass().getName());
        }

        return (Boolean) value;
    }

    public String getString(String p_key, String p_default) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "getString");
        }

        Object value = getValue(p_key, p_default);
        if (!(value instanceof String)) {
            throw new NumberFormatException("Value found for " + p_key
                    + " but value was not a String: " + value.getClass().getName());
        }
        return (String) value;
    }

    public int size() {
        return m_configMap.size();
    }
}
