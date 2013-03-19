package dbg.electronics.robodrv.mcu;

public interface McuReportListener {

    void onAdcValue(int channel, byte value);

    void onCounterUpdate(byte value);

    void onCommandResponse(byte sequence, byte[] params);

    void onMcuReset();

}
