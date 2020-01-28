// package org.wildstang.hardware;

// import com.ctre.phoenix.ErrorCode;
// import com.ctre.phoenix.ParamEnum;
// import com.ctre.phoenix.motion.MotionProfileStatus;
// import com.ctre.phoenix.motion.TrajectoryPoint;
// import com.ctre.phoenix.motorcontrol.ControlFrame;
// import com.ctre.phoenix.motorcontrol.ControlMode;
// import com.ctre.phoenix.motorcontrol.DemandType;
// import com.ctre.phoenix.motorcontrol.Faults;
// import com.ctre.phoenix.motorcontrol.FeedbackDevice;
// import com.ctre.phoenix.motorcontrol.IMotorController;
// import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
// import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
// import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
// import com.ctre.phoenix.motorcontrol.NeutralMode;
// import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
// import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
// import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
// import com.ctre.phoenix.motorcontrol.SensorCollection;
// import com.ctre.phoenix.motorcontrol.SensorTerm;
// import com.ctre.phoenix.motorcontrol.StatusFrame;
// import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
// import com.ctre.phoenix.motorcontrol.StickyFaults;
// import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;

// /**
//  * This class implements enough of a TalonSRX interface to let us sub it into
//  * code that expects a TalonSRX but isn't actually going to be talking to a
//  * Talon for whatever reason (e.g. it turns out late in the game that wiring the
//  * encoder to the motor controller isn't possible).
//  * 
//  * TODO: implement
//  */
// public class FakeTalonSRX implements IMotorControllerEnhanced {

//     @Override
//     public void valueUpdated() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void set(ControlMode Mode, double demand) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void set(ControlMode Mode, double demand0, double demand1) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void set(ControlMode Mode, double demand0, DemandType demand1Type, double demand1) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void neutralOutput() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void setNeutralMode(NeutralMode neutralMode) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void setSensorPhase(boolean PhaseSensor) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void setInverted(boolean invert) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public boolean getInverted() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void enableVoltageCompensation(boolean enable) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getBusVoltage() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getMotorOutputPercent() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getMotorOutputVoltage() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getTemperature() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configSelectedFeedbackCoefficient(double coefficient, int pidIdx, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal,
//             int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getSelectedSensorPosition(int pidIdx) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getSelectedSensorVelocity(int pidIdx) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
//             int deviceID, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
//             int deviceID, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void overrideLimitSwitchesEnable(boolean enable) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void overrideSoftLimitsEnable(boolean enable) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode config_IntegralZone(int slotIdx, int izone, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableCloseLoopError, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configAuxPIDPolarity(boolean invert, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getClosedLoopError(int pidIdx) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getIntegralAccumulator(int pidIdx) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getErrorDerivative(int pidIdx) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void selectProfileSlot(int slotIdx, int pidIdx) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getClosedLoopTarget(int pidIdx) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getActiveTrajectoryPosition() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getActiveTrajectoryVelocity() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getActiveTrajectoryHeading() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configMotionProfileTrajectoryPeriod(int baseTrajDurationMs, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode clearMotionProfileTrajectories() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getMotionProfileTopLevelBufferCount() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public boolean isMotionProfileTopLevelBufferFull() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void processMotionProfileBuffer() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode changeMotionControlFramePeriod(int periodMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode getLastError() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode getFaults(Faults toFill) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode getStickyFaults(StickyFaults toFill) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode clearStickyFaults(int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getFirmwareVersion() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public boolean hasResetOccurred() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int configGetCustomParam(int paramIndex, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double configGetParameter(ParamEnum paramEnum, int ordinal, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double configGetParameter(int paramEnum, int ordinal, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getBaseID() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getDeviceID() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ControlMode getControlMode() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public void follow(IMotorController masterToFollow) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public double getOutputCurrent() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
//             int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     @Override
//     public ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
//             int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     public ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     public ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     public ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     public void enableCurrentLimit(boolean enable) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     public SensorCollection getSensorCollection() {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }

//     public ErrorCode configMotionSCurveStrength(int curveStrength, int timeoutMs) {
//         throw new RuntimeException(); // UNIMPLEMENTED
//     }
// }