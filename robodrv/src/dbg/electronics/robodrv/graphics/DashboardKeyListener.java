package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DashboardKeyListener implements KeyListener {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private EventListener<Event> eventListener;

    public DashboardKeyListener(EventListener<Event> eventListener) {
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
            eventListener.onEvent(new Event(-999));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.info("Key Released: " + e.paramString());
    }
}
