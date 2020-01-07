package org.wildstang.framework.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.auto.AutoManager;
import org.wildstang.framework.config.ConfigManager;
import org.wildstang.framework.hardware.InputFactory;
import org.wildstang.framework.hardware.OutputFactory;
import org.wildstang.framework.io.IInputManager;
import org.wildstang.framework.io.IOutputManager;
import org.wildstang.framework.io.Input;
import org.wildstang.framework.io.InputManager;
import org.wildstang.framework.io.Output;
import org.wildstang.framework.io.OutputManager;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.RemoteAnalogInput;
import org.wildstang.framework.io.inputs.RemoteDigitalInput;
import org.wildstang.framework.io.outputs.AnalogOutput;
import org.wildstang.framework.io.outputs.DigitalOutput;
import org.wildstang.framework.io.outputs.RemoteAnalogOutput;
import org.wildstang.framework.io.outputs.RemoteDigitalOutput;
import org.wildstang.framework.logger.StateTracker;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.subsystems.SubsystemManager;

public class Core {
    private static Logger s_log = Logger.getLogger(Core.class.getName());
    private static final String s_className = "Core";

    private static IInputManager s_inputManager;
    private static IOutputManager s_outputManager;
    private static SubsystemManager s_subsystemManager;
    private static ConfigManager s_configManager;
    private static StateTracker s_stateTracker;
    private static InputFactory s_inputFactory;
    private static OutputFactory s_outputFactory;

    private AutoManager m_autoManager = null;

    private Class<?> m_inputFactoryClass;
    private Class<?> m_outputFactoryClass;

    public Core(Class<?> p_inputFactoryClass, Class<?> p_outputFactoryClass) {
        CoreUtils.checkNotNull(p_inputFactoryClass, "p_inputFactoryClass is null");
        CoreUtils.checkNotNull(p_outputFactoryClass, "p_outputFactoryClass is null");

        m_inputFactoryClass = p_inputFactoryClass;
        m_outputFactoryClass = p_outputFactoryClass;

        init();
    }

    /*
     * FIXME Static initialization tied to instance initialization. This can't be
     * the right lifecycle.
     */
    private void init() {
        s_inputManager = new InputManager();
        s_inputManager.init();

        s_outputManager = new OutputManager(); /*
                                                * FIXME no point in IOutputManager interface if this
                                                * is hard-coded
                                                */
        s_outputManager.init();

        s_subsystemManager = new SubsystemManager();
        s_subsystemManager.init();

        s_configManager = new ConfigManager();
        s_configManager.init();

        s_stateTracker = new StateTracker();
        s_stateTracker.init();

        s_inputFactory = (InputFactory) createObject(m_inputFactoryClass);
        s_inputFactory.init();

        s_outputFactory = (OutputFactory) createObject(m_outputFactoryClass);
        s_outputFactory.init();
    }

    public void createOutputs(Outputs[] p_outputs) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "createOutputs");
        }

        Output out = null;

        // Iterate over all output enum values and create an output for each
        for (Outputs output : p_outputs) {
            if (s_log.isLoggable(Level.FINE)) {
                s_log.fine("Creating output for " + output.getName());
            }
            // FIXME variable names are confusing in this function, as are types. out is an
            // Output. output, however, is an Outputs. an Outputs is a configuration
            // constant, but an Output is a class that does work at runtime.
            // Neither should be confused with an OutputConfig, which is a different beast
            // altogether.

            // Check if it is digital or analog, to create the correct type
            out = s_outputFactory.createOutput(output);

            // Add the output to the output manager
            if (output.isTrackingState()) {
                out.setStateTracker(s_stateTracker);
                s_stateTracker.addIOInfo(output.getName(), output.getType().toString(), "Output",
                        output.getConfig());
            }
            s_outputManager.addOutput(out);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "createOutputs");
        }
    }

    public void createInputs(Inputs[] p_inputs) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "createInputs");
        }

        Input in = null;

        // Iterate over all input enum values and create an input for each
        for (Inputs input : p_inputs) {
            if (s_log.isLoggable(Level.FINE)) {
                s_log.fine("Creating input for " + input.getName());
            }

            in = s_inputFactory.createInput(input);

            // Add the input to the input manager
            if (input.isTrackingState()) {
                in.setStateTracker(s_stateTracker);
                s_stateTracker.addIOInfo(input.getName(), input.getType().toString(), "Input",
                        input.getConfig());
            }
            s_inputManager.addInput(in);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "createInputs");
        }
    }

    public void createSubsystems(Subsystems[] p_subsystems) {
        if (s_log.isLoggable(Level.FINER)) {
            s_log.entering(s_className, "createSubsystems");
        }

        // Iterate over all input enum values and create a subsystem for each
        for (Subsystems subsystem : p_subsystems) {
            if (s_log.isLoggable(Level.FINE)) {
                s_log.fine("Creating subsystem: " + subsystem.getName());
            }

            // Instantiate the class
            Subsystem sub = (Subsystem) createObject(subsystem.getSubsystemClass());

            // Call the init method
            sub.init();

            s_subsystemManager.addSubsystem(sub);
        }

        if (s_log.isLoggable(Level.FINER)) {
            s_log.exiting(s_className, "createSubsystems");
        }
    }

    public void mapInputsToRemoteOutputs() {
        // Get all the Inputs
        HashMap<String, Input> inputMap = s_inputManager.getHashMap();

        // For each input that is not a remote input, create a remote output
        Iterator<HashMap.Entry<String, Input>> iter = inputMap.entrySet().iterator();

        if (iter != null) {
            while (iter.hasNext()) {
                Map.Entry<String, Input> entry = iter.next();

                String name = entry.getKey();
                Input input = entry.getValue();

                Output out = null;
                if (!(input instanceof RemoteAnalogInput)
                        && !(input instanceof RemoteDigitalInput)) {
                    if (input instanceof AnalogInput) {
                        out = new RemoteAnalogOutput(name, "remoteIO", 0);
                    } else if (input instanceof DigitalInput) {
                        out = new RemoteDigitalOutput(name, "remoteIO", false);
                    }
                    s_outputManager.addOutput(out);
                }
            }
        }
    }

    public void mapOutputsToRemoteInputs() {
        // Get all the Outputs
        HashMap<String, Output> outputMap = s_outputManager.getHashMap();

        // For each input that is not a remote input, create a remote output
        Iterator<HashMap.Entry<String, Output>> iter = outputMap.entrySet().iterator();

        if (iter != null) {
            while (iter.hasNext()) {
                Map.Entry<String, Output> entry = iter.next();

                String name = entry.getKey();
                Output output = entry.getValue();

                Input in = null;
                if (!(output instanceof RemoteAnalogInput)
                        && !(output instanceof RemoteDigitalInput)) {
                    if (output instanceof AnalogOutput) {
                        in = new RemoteAnalogInput(name, "remoteIO");
                    } else if (output instanceof DigitalOutput) {
                        in = new RemoteDigitalInput(name, "remoteIO");
                    }
                    s_inputManager.addInput(in);
                }
            }
        }
    }

    public static IInputManager getInputManager() {
        return s_inputManager;
    }

    public static IOutputManager getOutputManager() {
        return s_outputManager;
    }

    public static SubsystemManager getSubsystemManager() {
        return s_subsystemManager;
    }

    public static ConfigManager getConfigManager() {
        return s_configManager;
    }

    public static StateTracker getStateTracker() {
        return s_stateTracker;
    }

    protected Object createObject(Class<?> p_class) {
        CoreUtils.checkNotNull(p_class, "p_class is null");

        Object obj = null;

        try {
            obj = p_class.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public void setAutoManager(AutoManager p_autoManager) {
        m_autoManager = p_autoManager;
    }

    public void executeUpdate() {
        s_stateTracker.beginCycle(new Date());

        // Read input from hardware
        s_inputManager.update();

        if (m_autoManager != null) {
            //System.out.println("Checkpoint 909 yay");
            m_autoManager.update();
        }
        // Let subsystems react to changes
        s_subsystemManager.update();

        // Update outputs - send data to devices
        s_outputManager.update();

        s_stateTracker.endCycle();
    }

}
