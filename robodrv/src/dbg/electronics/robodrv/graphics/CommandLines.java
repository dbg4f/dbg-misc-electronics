package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.groovy.GroovyEvaluator;
import dbg.electronics.robodrv.head.MultilineReportable;
import dbg.electronics.robodrv.logging.ValueHistorySerializer;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class CommandLines implements MultilineReportable {

    private ValueHistorySerializer valueHistorySerializer;

    private TextCommandEvaluator evaluator = new GroovyEvaluator();//DefaultEvaluator();

    public void setValueHistorySerializer(ValueHistorySerializer valueHistorySerializer) {
        this.valueHistorySerializer = valueHistorySerializer;
    }

    public void setEvaluator(TextCommandEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public static final char CURSOR_CHAR = '#';

    private static final long CURSOR_BLINK_INTERVAL_MSEC = 600;

    private String commandLine = "";

    private long lastToggleTime = System.currentTimeMillis();

    private char currentCursorChar = CURSOR_CHAR;

    @Override
    public String[] toStringArray() {

        if (isCursorBlinkPeriodExpired()) {
            toggleCursorBlinking();
        }

        return new String[]{commandLine + currentCursorChar};  //To change body of implemented methods use File | Settings | File Templates.
    }


    class DefaultEvaluator implements TextCommandEvaluator {

        @Override
        public String evaluate(String command) {
            return "Command ok: "+ command;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private boolean isCursorBlinkPeriodExpired() {
        return System.currentTimeMillis() - lastToggleTime > CURSOR_BLINK_INTERVAL_MSEC;
    }


    private void toggleCursorBlinking() {
        currentCursorChar = (currentCursorChar == CURSOR_CHAR ? ' ' : CURSOR_CHAR);
        lastToggleTime = System.currentTimeMillis();
    }


    public void onChar(char ch) {



        //if (isPrintableChar(ch)) {
        if (ch != 13) {
            commandLine += ch;
        } else {
            try {
                if (commandLine.length() > 3)
                //valueHistorySerializer.save(commandLine);
                commandLine = evaluator.evaluate(commandLine);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                commandLine = "ERROR: " + e.getMessage();
            }



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
