package dbg.electronics.robodrv;

import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

/**
 * Created with IntelliJ IDEA.
 * User: dmitri
 * Date: 1/12/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Orchestrator implements FailuresListener, InputListener{

    private static final SimpleLogger log = LoggerFactory.getLogger();

    void start() {

      HidEventListener hidEventListener = new HidEventListener("/dev/input/event14", this, this);

      hidEventListener.launch();

      MultiBufferFullScreen screen = new MultiBufferFullScreen();






    }


    @Override
    public void onFailure(Failure failure) {
        log.error(String.valueOf(failure));
    }

    @Override
    public void onEvent(InputEvent event) {
        log.info(String.valueOf(event));
    }
}
