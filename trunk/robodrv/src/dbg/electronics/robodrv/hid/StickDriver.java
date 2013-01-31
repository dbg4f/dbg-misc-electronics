package dbg.electronics.robodrv.hid;

import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.head.ParameterUpdater;

/**
 * Created: 1/31/13  9:44 PM
 */
public class StickDriver implements EventListener<HidInputReport> {

    private ParameterUpdater updater;


    public void setUpdater(ParameterUpdater updater) {
        this.updater = updater;
    }




    @Override
    public void onEvent(HidInputReport event) {



    }
}
