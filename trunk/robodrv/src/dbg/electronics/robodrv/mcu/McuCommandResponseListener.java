package dbg.electronics.robodrv.mcu;


public interface McuCommandResponseListener {

    void onCommandResponse(byte sequence, byte[] params);

}
