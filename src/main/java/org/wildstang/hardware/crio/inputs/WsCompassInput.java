package org.wildstang.hardware.crio.inputs;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class WsCompassInput extends WsI2CInput implements PIDSource {
    public WsCompassInput(String name, Port port, int p_address) {
        super(name, port, p_address);
    }

    @Override
    public void setPIDSourceType(PIDSourceType p_pidSource) {
        // Ignore - can't change type
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    @Override
    public double pidGet() {
        // Expecting the compass to return a value between 0-180. need to multiply by 2
        // for the angle
        // in range 0-360
        return (double) getValue()[0] * 2;
    }

}
