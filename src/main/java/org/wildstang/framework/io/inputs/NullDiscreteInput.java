package org.wildstang.framework.io.inputs;

public class NullDiscreteInput extends DiscreteInput {

    public NullDiscreteInput(String p_name) {
        super(p_name);
    }

    public NullDiscreteInput(String p_name, int p_default) {
        super(p_name, p_default);
    }

    @Override
    protected int readRawValue() {
        return 0;
    }

}
