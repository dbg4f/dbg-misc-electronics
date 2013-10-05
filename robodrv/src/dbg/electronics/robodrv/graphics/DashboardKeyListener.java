package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static dbg.electronics.robodrv.Event.EventCode.SHUTDOWN;

public class DashboardKeyListener implements KeyListener {

    private static final SimpleLogger log = LoggerFactory.getLogger(DashboardKeyListener.class.getName());

    private EventListener<Event> eventListener;

    private CommandLines commandLines;


    public void setEventListener(EventListener<Event> eventListener) {
        this.eventListener = eventListener;
    }

    public void setCommandLines(CommandLines commandLines) {
        this.commandLines = commandLines;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        log.info("Key Typed: " + e.paramString());

        commandLines.onChar(e.getKeyChar());

    }

    @Override
    public void keyPressed(KeyEvent e) {
        log.debug("Key Pressed: " + e.paramString());

        if (e.getKeyCode() == 27) {
            eventListener.onEvent(new Event(SHUTDOWN));
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            commandLines.onKey(e.getKeyCode());
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.debug("Key Released: " + e.paramString());
    }
}
