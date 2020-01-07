package org.wildstang.framework.io;

public interface InputListener {

    /**
     * Notifies the listener that an input event has occurred.
     *
     * @param source
     *            the Input that has updated
     */
    public void inputUpdate(Input source);
}
