package org.wildstang.hardware.crio;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputFactory;
import org.wildstang.framework.hardware.WsRemoteAnalogOutputConfig;
import org.wildstang.framework.hardware.WsRemoteDigitalOutputConfig;
import org.wildstang.framework.io.Output;
import org.wildstang.framework.io.outputs.RemoteAnalogOutput;
import org.wildstang.framework.io.outputs.RemoteDigitalOutput;
import org.wildstang.hardware.crio.outputs.WSOutputType;
import org.wildstang.hardware.crio.outputs.WsDigitalOutput;
import org.wildstang.hardware.crio.outputs.WsDoubleSolenoid;
import org.wildstang.hardware.crio.outputs.WsI2COutput;
import org.wildstang.hardware.crio.outputs.WsRelay;
import org.wildstang.hardware.crio.outputs.WsServo;
import org.wildstang.hardware.crio.outputs.WsSolenoid;
import org.wildstang.hardware.crio.outputs.WsTalon;
import org.wildstang.hardware.crio.outputs.WsVictor;
import org.wildstang.hardware.crio.outputs.config.WsDigitalOutputConfig;
import org.wildstang.hardware.crio.outputs.config.WsDoubleSolenoidConfig;
import org.wildstang.hardware.crio.outputs.config.WsI2COutputConfig;
import org.wildstang.hardware.crio.outputs.config.WsRelayConfig;
import org.wildstang.hardware.crio.outputs.config.WsServoConfig;
import org.wildstang.hardware.crio.outputs.config.WsSolenoidConfig;
import org.wildstang.hardware.crio.outputs.config.WsTalonConfig;
import org.wildstang.hardware.crio.outputs.config.WsVictorConfig;

/*
 * TODO: This factory seems unnecessary; it contains no configuration data.
 * We can probably get rid of it.
 */
public class RoboRIOOutputFactory implements OutputFactory {

    private static Logger s_log = Logger.getLogger(RoboRIOOutputFactory.class.getName());
    private static final String s_className = "RoboRIOOutputFactory";

    private boolean s_initialised = false;

    public RoboRIOOutputFactory() {

    }

    @Override
    public void init() {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "init");
        }

        if (!s_initialised) {
            s_initialised = true;
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "init");
        }
    }

    @Override
    public Output createOutput(Outputs p_output) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "createDigitalInput");
        }

        Output out = null;

        if (s_log.isLoggable(Level.FINE)) {
            s_log.fine("Creating digital output: Name = " + p_output.getName() + ", type = "
                    + p_output.getType());
        }

        switch ((WSOutputType) p_output.getType()) {
        case DIGITAL_OUTPUT:
            out = new WsDigitalOutput(p_output.getName(),
                    ((WsDigitalOutputConfig) p_output.getConfig()).getChannel(),
                    ((WsDigitalOutputConfig) p_output.getConfig()).getDefault());
        break;
        case SERVO:
            // out = new WsServo(p_output.getName(), ((WsAnalogOutputConfig)
            // p_output.getConfig()).getChannel(), ((WsAnalogOutputConfig)
            // p_output.getConfig()).getDefault());
            out = new WsServo(p_output.getName(),
                    ((WsServoConfig) p_output.getConfig()).getChannel(),
                    ((WsServoConfig) p_output.getConfig()).getDefault());
        break;
        case RELAY:
            out = new WsRelay(p_output.getName(),
                    ((WsRelayConfig) p_output.getConfig()).getChannel());
        break;
        case VICTOR:
            out = new WsVictor(p_output.getName(),
                    ((WsVictorConfig) p_output.getConfig()).getChannel(),
                    ((WsVictorConfig) p_output.getConfig()).getDefault());
        break;
        case TALON:
            out = new WsTalon(p_output.getName(),
                    ((WsTalonConfig) p_output.getConfig()).getChannel(),
                    ((WsTalonConfig) p_output.getConfig()).getDefault());
        break;
        case SOLENOID_SINGLE:
            WsSolenoidConfig ssConfig = (WsSolenoidConfig) p_output.getConfig();
            out = new WsSolenoid(p_output.getName(), ssConfig.getModule(), ssConfig.getChannel(),
                    ssConfig.getDefault());
        break;
        case SOLENOID_DOUBLE:
            WsDoubleSolenoidConfig dsConfig = (WsDoubleSolenoidConfig) p_output.getConfig();
            out = new WsDoubleSolenoid(p_output.getName(), dsConfig.getModule(),
                    dsConfig.getChannel1(), dsConfig.getChannel2(), dsConfig.getDefault());
        break;
        case I2C:
            out = new WsI2COutput(p_output.getName(),
                    ((WsI2COutputConfig) p_output.getConfig()).getPort(),
                    ((WsI2COutputConfig) p_output.getConfig()).getAddress());
        break;
        case REMOTE_DIGITAL:
            out = new RemoteDigitalOutput(p_output.getName(),
                    ((WsRemoteDigitalOutputConfig) p_output.getConfig()).getTableName(),
                    ((WsRemoteDigitalOutputConfig) p_output.getConfig()).getDefault());
        break;
        case REMOTE_ANALOG:
            out = new RemoteAnalogOutput(p_output.getName(),
                    ((WsRemoteAnalogOutputConfig) p_output.getConfig()).getTableName(),
                    ((WsRemoteAnalogOutputConfig) p_output.getConfig()).getDefault());
        break;
        case NULL:
        default:
            // out = new NullDigitalOutput(p_name);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "createDigitalInput");
        }

        return out;
    }

}
