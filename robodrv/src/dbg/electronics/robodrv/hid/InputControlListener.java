package dbg.electronics.robodrv.hid;

/**
 */
public interface InputControlListener {

    void onUpdate(InputRangedControl control, int value);

}
