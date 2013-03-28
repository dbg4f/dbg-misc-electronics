package dbg.electronics.robodrv.drive;

import dbg.electronics.robodrv.mcu.ChannelStatus;
import dbg.electronics.robodrv.mcu.ChannelStatusListener;
import dbg.electronics.robodrv.mcu.McuReportListener;
import dbg.electronics.robodrv.mcu.ProtocolState;

public class DriveState implements McuReportListener {

    private ProtocolState protocolState;
    private ChannelStatus channelStatus;
    private int steeringAngle; // +/- degrees
    private int pwrCurrent;  // milliampers
    private int pwrVoltage;  // millivolts
    private int signalVoltage; // millivolts
    private int echoTurnaroundTime; // msec
    private int wheelTicks;
    private int speed; // cm/s

    public class ProtocolStateUpdater implements ChannelStatusListener<ProtocolState> {

        @Override
        public void onStatusChanged(ProtocolState status) {
            protocolState = status;
        }
    }

    public class ChannelStatusUpdater implements ChannelStatusListener<ChannelStatus> {

        @Override
        public void onStatusChanged(ChannelStatus status) {
            channelStatus = status;
        }
    }

    @Override
    public void onAdcValue(int channel, byte value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCounterUpdate(byte value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCommandResponse(byte sequence, byte[] params) {

    }

    @Override
    public void onMcuReset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ProtocolState getProtocolState() {
        return protocolState;
    }

    public ChannelStatus getChannelStatus() {
        return channelStatus;
    }

    public int getSteeringAngle() {
        return steeringAngle;
    }

    public int getPwrCurrent() {
        return pwrCurrent;
    }

    public int getPwrVoltage() {
        return pwrVoltage;
    }

    public int getSignalVoltage() {
        return signalVoltage;
    }

    public int getEchoTurnaroundTime() {
        return echoTurnaroundTime;
    }

    public int getWheelTicks() {
        return wheelTicks;
    }

    public int getSpeed() {
        return speed;
    }
}
