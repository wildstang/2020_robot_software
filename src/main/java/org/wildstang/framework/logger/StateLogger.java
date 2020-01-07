package org.wildstang.framework.logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.wildstang.framework.CoreUtils;

public class StateLogger implements Runnable {
    private static final long s_defaultWriteInterval = 200;

    private Writer m_output;
    private boolean m_infoWritten = false;
    private boolean m_firstState = true;
    private boolean m_stateWritten = false;
    private boolean m_running = false;
    private StateTracker m_tracker;
    private long m_writeInterval = s_defaultWriteInterval;

    public StateLogger(StateTracker p_tracker) {
        m_tracker = p_tracker;
    }

    public void setWriter(Writer p_output) {
        m_output = p_output;
    }

    public Writer getOutput() {
        return m_output;
    }

    public boolean isRunning() {
        return m_running;
    }

    public void start() {
        m_running = true;
    }

    public void stop() {
        m_running = false;
    }

    @Override
    public void run() {
        // Open the JSON
        try {
            writeStart(m_output);

            while (m_running) {
                try {
                    Thread.sleep(m_writeInterval);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (m_tracker.isTrackingState()) {
                    if (!m_infoWritten) {
                        // These calls should not go back to the state manager - need to pass the
                        // list
                        // in somehow
                        writeInfo(m_output, m_tracker.getIoSet());
                        m_infoWritten = true;
                    } else {
                        writeState(m_output, m_tracker.getStateList());
                    }
                }
            }

            // Close the JSON
            // Write any remaining state information
            writeState(m_output, m_tracker.getStateList());
            writeEnd(m_output);
            m_output.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setWriteInterval(long p_interval) {
        m_writeInterval = p_interval;
    }

    protected void writeInfo(Writer p_output, IOSet p_set) throws IOException {
        // The set should not be null - this would indicate a code error
        CoreUtils.checkNotNull(p_set, "p_set is null");
        // The Writer may be null - it is not fatal. Errors causing a null Writer
        // should be caught by the caller

        Iterator<IOInfo> iter = p_set.getInfoList().iterator();
        StringBuilder builder = new StringBuilder();

        builder.append("\"ioinfo\":[\n");

        while (iter.hasNext()) {
            builder.append(formatIOInfo(iter.next()));
            if (iter.hasNext()) {
                builder.append(",\n");
            }
        }
        builder.append("\n],\n");

        if (p_output != null) {
            p_output.write(builder.toString());
        }
    }

    protected void writeState(Writer p_output, List<StateGroup> p_stateList) throws IOException {
        // The list should never be null - this would indicate a code error
        CoreUtils.checkNotNull(p_stateList, "p_stateList is null");
        // The Writer could be null - this is not fatal. Errors that would
        // lead to a null writer should be caught before calling this

        StringBuilder builder = new StringBuilder();

        if (m_firstState) {
            builder.append("\"state\":[\n");
            // Set flag that this has run once
            m_firstState = false;
        }

        if (p_stateList.size() > 0) {
            if (m_stateWritten) {
                builder.append(",\n");
            }

            Iterator<StateGroup> iter = p_stateList.iterator();

            while (iter.hasNext()) {
                builder.append(formatState(iter.next()));
                if (iter.hasNext()) {
                    builder.append(",\n");
                }

            }

            m_stateWritten = true;
        }

        // If the writer is null, don't write it
        // This is non-fatal. If we miss a few values, it doesn't matter
        // Don't log that it is null - it gets called too frequently
        if (p_output != null) {
            p_output.write(builder.toString());
        }

    }

    protected void writeStart(Writer p_output) throws IOException {

        StringBuilder builder = new StringBuilder();

        builder.append("{\n");

        // It's not critical that this is not written out
        // Don't log that it is null - it gets called too frequently
        if (p_output != null) {
            p_output.write(builder.toString());
        }
    }

    protected void writeEnd(Writer p_output) throws IOException {

        StringBuilder builder = new StringBuilder();

        builder.append("\n]\n}\n");

        // It's not critical that this is not written out
        // Don't log that it is null - it gets called too frequently
        if (p_output != null) {
            p_output.write(builder.toString());
        }
    }

    protected String formatState(StateGroup p_group) {
        StringBuilder builder = new StringBuilder();

        builder.append("\t{\n\t\t\"timestamp\":\"");
        builder.append(p_group.getTimestamp().getTime());
        builder.append("\",\n\t\t\"values\":[\n");

        Iterator<StateInfo> iter = p_group.getStateList().values().iterator();
        StateInfo temp;

        while (iter.hasNext()) {
            temp = iter.next();

            builder.append("\t\t\t{\"name\":\"");
            builder.append(temp.getName());
            // builder.append("\",\"parent\":\"");
            // builder.append(temp.getParent());
            builder.append("\",\"value\":\"");
            builder.append(temp.getValue());
            builder.append("\"}");

            if (iter.hasNext()) {
                builder.append(",");
            }

            builder.append("\n");
        }

        builder.append("\t\t]\n\t}");

        return builder.toString();
    }

    protected String formatIOInfo(IOInfo p_info) {
        StringBuilder builder = new StringBuilder();

        // Format the info set
        builder.append("\t{\"name\":\"");
        builder.append(p_info.getName());
        builder.append("\",\"type\":\"");
        builder.append(p_info.getType());
        builder.append("\",\"direction\":\"");
        builder.append(p_info.getDirection());
        builder.append("\",\"port\":");

        Object portInfo = p_info.getPort();
        if (portInfo == null) {
            builder.append("{}");
        } else {
            builder.append(portInfo);
        }

        builder.append("}");

        return builder.toString();
    }
}
