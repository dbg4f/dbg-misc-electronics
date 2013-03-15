package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.head.MultilineReportable;

import java.awt.event.KeyEvent;

public class CommandLines implements MultilineReportable {


    private String commandLine = "";

    @Override
    public String[] toStringArray() {
        return new String[]{commandLine};  //To change body of implemented methods use File | Settings | File Templates.
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
