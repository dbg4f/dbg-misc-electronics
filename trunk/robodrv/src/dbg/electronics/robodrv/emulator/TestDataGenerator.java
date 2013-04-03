package dbg.electronics.robodrv.emulator;

import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.graphics.EnumValueWithHistory;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.mcu.ChannelStatus;

import java.util.Calendar;


/**
 */
public class TestDataGenerator extends GenericThread {

    private ValueWithHistory secondsValue;
    private EnumValueWithHistory<ChannelStatus> channelStatus;

    /*
    public TestDataGenerator() {
        secondsValue = new ValueWithHistory();
        secondsValue.setValueFormatString("%d sec");
        secondsValue.setValueRange(new Range(0, 60));
        channelStatus = new EnumValueWithHistory<ChannelStatus>(ChannelStatus.UNKNWON);
    }
    */

    public void setSecondsValue(ValueWithHistory secondsValue) {
        this.secondsValue = secondsValue;
    }

    public void setChannelStatus(EnumValueWithHistory<ChannelStatus> channelStatus) {
        this.channelStatus = channelStatus;
    }

    public void startWork() {

        while(!Thread.currentThread().isInterrupted()) {

            int value = Calendar.getInstance().get(Calendar.SECOND);//(int)System.currentTimeMillis() % 1000;

            ChannelStatus status = value > 30 ? ChannelStatus.CONNECTED : ChannelStatus.FAILURE;

            secondsValue.update(value);

            channelStatus.update(status);


            //System.out.println("secondsValue = " + secondsValue.getCurrentSeries());

            //System.out.println("channelStatus = " + channelStatus.getCurrentSeries());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
               break;
            }

        }

    }
}
