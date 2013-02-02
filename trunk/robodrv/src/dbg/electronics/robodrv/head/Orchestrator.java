package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.graphics.MultiBufferFullScreen;
import dbg.electronics.robodrv.hid.HidEventFileReader;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.util.List;

public class Orchestrator implements FailureListener, EventListener<Event> {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private List<GenericThread> threads;

    public void setThreads(List<GenericThread> threads) {
        this.threads = threads;
    }

    public Orchestrator() {
    }

    public void start() {

        for (GenericThread thread : threads) {
            thread.launch();
        }


    }


    @Override
    public void onFailure(Failure failure) {
        log.error(String.valueOf(failure));
    }

    @Override
    public void onEvent(Event event) {

        log.info(String.valueOf(event));

        if (event.getEventCode() == Event.EventCode.SHUTDOWN) {

            for (GenericThread thread : threads) {
                thread.terminate();
            }
        }

        System.exit(0);


    }
}
