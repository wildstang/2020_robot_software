/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wildstang.year2016.robot;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.pid.output.IPidOutput;
import org.wildstang.year2016.subsystems.DriveBase;

/**
 *
 * @author Nathan
 */
public class WsDriveBaseSpeedPidOutput implements IPidOutput {

    public WsDriveBaseSpeedPidOutput() {
        // Nothing to do here
    }

    @Override
    public void pidWrite(double output) {
        ((DriveBase) Core.getSubsystemManager().getSubsystem(WSSubsystems.DRIVE_BASE.getName()))
                .setPidSpeedValue(output);
    }
}