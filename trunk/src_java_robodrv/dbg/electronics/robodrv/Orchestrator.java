package dbg.electronics.robodrv;

import dbg.electronics.robodrv.graphics.MultiBufferFullScreen;
import dbg.electronics.robodrv.hid.HidEventListener;
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

    private MultiBufferFullScreen screen;

    private HidEventListener hidEventListener;

    private static Orchestrator instance;

    private Orchestrator() {
    }

    public static synchronized Orchestrator getInstance() {

        if (instance == null) {
            instance = new Orchestrator();
        }

        return instance;
    }

    void start() {

      screen = new MultiBufferFullScreen();

      hidEventListener = new HidEventListener("/dev/input/event14", this, this);

      hidEventListener.launch();

      screen.launch();


    }


    @Override
    public void onFailure(Failure failure) {
        log.error(String.valueOf(failure));
    }

    @Override
    public void onEvent(InputEvent event) {

        log.info(String.valueOf(event));

        if (event.getContent() == InputEvent.Content.TEST_INT_VALUE && event.getValue() == -999) {

            hidEventListener.terminate();

            screen.terminate();

            log.info("Termination initiated");

            System.exit(0);

        }
        else {

            screen.onEvent(event);
        }

    }
}
