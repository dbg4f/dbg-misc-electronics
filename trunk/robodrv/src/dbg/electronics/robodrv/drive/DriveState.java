package dbg.electronics.robodrv.drive;

import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.head.MultilineReportable;
import dbg.electronics.robodrv.mcu.ChannelStatus;
import dbg.electronics.robodrv.mcu.ChannelStatusListener;
import dbg.electronics.robodrv.mcu.McuReportListener;
import dbg.electronics.robodrv.mcu.ProtocolState;

public class DriveState implements McuReportListener, MultilineReportable {

    private ProtocolState protocolState;
    private ChannelStatus channelStatus;
    private int steeringAngle; // +/- degrees
    private int pwrCurrent;  // milliampers
    private int pwrVoltage;  // millivolts
    private int signalVoltage; // millivolts

    private ValueWithHistory sampleValueWithHistory;


    public void setSampleValueWithHistory(ValueWithHistory sampleValueWithHistory) {
        this.sampleValueWithHistory = sampleValueWithHistory;
    }

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

    public ProtocolStateUpdater protocolStateUpdater = new ProtocolStateUpdater();

    public ChannelStatusUpdater channelStatusUpdater = new ChannelStatusUpdater();


    @Override
    public void onAdcValue(int channel, byte value) {
        //System.out.println("channel = " + channel + " value " + value);
        pwrCurrent = value;
        sampleValueWithHistory.update(value);
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


    @Override
    public String[] toStringArray() {
        return new String[] {
                "Channel  " + channelStatus,
                "Protocol " + protocolState,
                "pwr      " + pwrCurrent
        };
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

}
