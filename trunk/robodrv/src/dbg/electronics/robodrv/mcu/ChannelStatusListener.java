package dbg.electronics.robodrv.mcu;


public interface ChannelStatusListener<T extends Enum> {

    void onStatusChanged(T status);

}
