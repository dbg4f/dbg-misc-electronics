package dbg.electronics.robodrv.drive;

import dbg.electronics.robodrv.Range;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.head.HeadController;
import dbg.electronics.robodrv.head.MasterParameterType;
import dbg.electronics.robodrv.head.MultilineReportable;
import dbg.electronics.robodrv.mcu.ChannelStatus;
import dbg.electronics.robodrv.mcu.ChannelStatusListener;
import dbg.electronics.robodrv.mcu.McuReportListener;
import dbg.electronics.robodrv.mcu.ProtocolState;
import org.apache.log4j.Logger;

public class DriveState implements McuReportListener, MultilineReportable {


    private static final Logger log = Logger.getLogger(DriveState.class);

    private static final Range BYTE_RANGE = new Range(0, 255);

    private HeadController headController;
    private ProtocolState protocolState;
    private ChannelStatus channelStatus;
    private int currentRawPos;
    private int steeringAngle; // +/- degrees
    private int pwrCurrent;  // milliampers
    private int pwrVoltage;  // millivolts
    private int signalVoltage; // millivolts

    private ValueWithHistory sampleValueWithHistory;
    private ValueWithHistory sampleValueWithHistory2;


    public void setSampleValueWithHistory(ValueWithHistory sampleValueWithHistory) {
        this.sampleValueWithHistory = sampleValueWithHistory;
    }

    public void setSampleValueWithHistory2(ValueWithHistory sampleValueWithHistory2) {
        this.sampleValueWithHistory2 = sampleValueWithHistory2;
    }

    public void setHeadController(HeadController headController) {
        this.headController = headController;
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

        /*
        0 - V?        m32u4 - position
        1 - current
        2 - V?
        3 - position
         */


        int unsignedValue = (0xFF & value);

        if (channel == 1) {
            pwrCurrent = value;
            sampleValueWithHistory.update(unsignedValue);
            headController.update(MasterParameterType.POWER_CURRENT, unsignedValue, BYTE_RANGE);

        }
        if (channel == 0) {
            sampleValueWithHistory2.update(unsignedValue);
            headController.update(MasterParameterType.STEERING_ANGLE, unsignedValue, BYTE_RANGE);

            if (unsignedValue != currentRawPos) {
                log.info(String.format("pos changed %d -> %d", currentRawPos, unsignedValue));
            }

            currentRawPos = unsignedValue;

        }
    }

    @Override
    public void onCounterUpdate(byte value) {

        int unsignedValue = (0xFF & value);

        //sampleValueWithHistory2.update(unsignedValue);
        //headController.update(MasterParameterType.STEERING_ANGLE, unsignedValue, BYTE_RANGE);


        System.out.println("unsignedValue = " + unsignedValue);

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
