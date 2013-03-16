package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.head.MultilineReportable;

import java.awt.event.KeyEvent;

public class CommandLines implements MultilineReportable {


    public static final char CURSOR_CHAR = '#';

    private static final long CURSOR_BLINK_INTERVAL_MSEC = 600;

    private String commandLine = "";

    private long lastToggleTime = System.currentTimeMillis();

    private char currentCursorChar = CURSOR_CHAR;

    @Override
    public String[] toStringArray() {

        if (System.currentTimeMillis() - lastToggleTime > CURSOR_BLINK_INTERVAL_MSEC) {
            toggleCursor();
        }

        return new String[]{commandLine + currentCursorChar};  //To change body of implemented methods use File | Settings | File Templates.
    }


    private void toggleCursor() {
        currentCursorChar = (currentCursorChar == CURSOR_CHAR ? ' ' : CURSOR_CHAR);
        lastToggleTime = System.currentTimeMillis();
    }


    public void onChar(char ch) {
        if (isPrintableChar(ch)) {
            commandLine += ch;
        } else {
            commandLine = "";
        }

    }

    public boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    public void onCode(int code) {
    }

}
