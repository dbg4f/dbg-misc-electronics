package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.groovy.GroovyEvaluator;
import dbg.electronics.robodrv.head.MultilineReportable;
import dbg.electronics.robodrv.logging.ValueHistorySerializer;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CommandLines implements MultilineReportable {

    public static final int RESULTS_DEPTH = 3;
    public static final int HISTORY_DEPTH = 10;

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

    private int historyPointer = 0;

    private ArrayList<String> results = new ArrayList<String>();
    private ArrayList<String> history = new ArrayList<String>();


    @Override
    public String[] toStringArray() {

        if (isCursorBlinkPeriodExpired()) {
            toggleCursorBlinking();
        }

        ArrayList<String> currentLines = new ArrayList<String>();

        currentLines.add(commandLine + currentCursorChar);
        currentLines.addAll(results);

        return currentLines.toArray(new String[currentLines.size()]);
    }


    void addResult(String result) {
        ringBufferAdd(result, RESULTS_DEPTH, results);
    }

    private void ringBufferAdd(String result, int depth, ArrayList<String> buffer) {
        while (buffer.size() > depth) {
            buffer.remove(buffer.size() - 1);
        }
        buffer.add(0, result);
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

    public void onKey(int keyCode) {
        if (keyCode == 40) {
            onArrowDown();
        }
        else if (keyCode == 38) {
            onArrowUp();
        }
    }

    public void onChar(char ch) {

        if (ch == '\b' && commandLine.length() > 0) {
            onBackspace();
        }
        if (ch == '\n' && commandLine.length() > 0) {
            onEnter();
        }
        else if (isPrintableChar(ch)) {
            commandLine += ch;
        }

    }

    private void onBackspace() {
        commandLine = commandLine.substring(0, commandLine.length()-1);
    }

    private void onArrowDown() {

        if (historyPointer > 0) {
            historyPointer--;
        }
        else if (history.size() > 0){
            historyPointer = history.size() - 1;
        }


        if (history.size()-1 >= historyPointer) {
            commandLine = history.get(historyPointer);
        }



    }

    private void onArrowUp() {
        if (historyPointer < history.size() - 1) {
            historyPointer++;
        }
        else {
            historyPointer = 0;
        }

        if (historyPointer <= history.size()-1) {
            commandLine = history.get(historyPointer);
        }

    }

    private void onEnter() {

        String result;

        try {
            result = evaluator.evaluate(commandLine);
            addCommandToHistory(commandLine);
        } catch (Exception e) {
            e.printStackTrace();
            result = "ERROR: " + e.getMessage();
        }

        addResult(result);

        commandLine = "";
    }

    private void addCommandToHistory(String cmd) {
        if (history.size() == 0 || history.get(0).compareTo(cmd) != 0) {
            ringBufferAdd(cmd, HISTORY_DEPTH, history);
        }

    }

    public boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

}
