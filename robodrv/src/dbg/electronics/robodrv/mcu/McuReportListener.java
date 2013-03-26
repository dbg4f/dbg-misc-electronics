package dbg.electronics.robodrv.mcu;

public interface McuReportListener extends McuCommandResponseListener {

    void onAdcValue(int channel, byte value);

    void onCounterUpdate(byte value);

    void onMcuReset();

}
