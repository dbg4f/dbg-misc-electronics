package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.InputEvent;
import dbg.electronics.robodrv.InputListener;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DashboardKeyListener implements KeyListener {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private InputListener inputListener;

    public DashboardKeyListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        log.info("Key Typed: " + e.paramString());



    }

    @Override
    public void keyPressed(KeyEvent e) {
        log.info("Key Pressed: " + e.paramString());

        if (e.getKeyCode() == 27) {
            inputListener.onEvent(new InputEvent(-999));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.info("Key Released: " + e.paramString());
    }
}
