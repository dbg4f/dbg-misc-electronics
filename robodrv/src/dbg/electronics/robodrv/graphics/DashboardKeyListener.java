package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static dbg.electronics.robodrv.Event.EventCode.SHUTDOWN;

public class DashboardKeyListener implements KeyListener {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private EventListener<Event> eventListener;


    public void setEventListener(EventListener<Event> eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        log.info("Key Typed: " + e.paramString());



    }

    @Override
    public void keyPressed(KeyEvent e) {
        log.info("Key Pressed: " + e.paramString());

        if (e.getKeyCode() == 27) {
            eventListener.onEvent(new Event(SHUTDOWN));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.info("Key Released: " + e.paramString());
    }
}
