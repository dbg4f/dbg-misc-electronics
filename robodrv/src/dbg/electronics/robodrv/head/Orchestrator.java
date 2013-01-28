package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.graphics.MultiBufferFullScreen;
import dbg.electronics.robodrv.hid.HidEventFileReader;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

public class Orchestrator implements FailureListener, EventListener<Event> {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private MultiBufferFullScreen screen;

    private HidEventFileReader hidEventFileReader;

    private static Orchestrator instance;

    private Orchestrator() {
    }

    public void setHidEventFileReader(HidEventFileReader hidEventFileReader) {
        this.hidEventFileReader = hidEventFileReader;
    }

    public static synchronized Orchestrator getInstance() {

        if (instance == null) {
            instance = new Orchestrator();
        }

        return instance;
    }

    public void start() {

      screen = new MultiBufferFullScreen();

      //hidEventFileReader = new HidEventFileReader("/dev/input/event14", this, this);

      hidEventFileReader.launch();

      screen.launch();


    }


    @Override
    public void onFailure(Failure failure) {
        log.error(String.valueOf(failure));
    }

    @Override
    public void onEvent(Event event) {

        log.info(String.valueOf(event));

        if (event.getContent() == Event.Content.TEST_INT_VALUE && event.getValue() == -999) {

            hidEventFileReader.terminate();

            screen.terminate();

            log.info("Termination initiated");

            System.exit(0);

        }
        else {

            screen.onEvent(event);
        }

    }
}
