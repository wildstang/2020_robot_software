package org.wildstang.framework.hardware;

import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.io.Output;

/**
 * Creates Output objects attached to a specified port, and of a specified type.
 *
 * @author Steve
 *
 */
public interface OutputFactory {
    public void init();

    public Output createOutput(Outputs p_output);
}
