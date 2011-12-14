package dbg.electronics;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 12/14/11
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleLogger implements McLogger {

    private static ConsoleLogger instance;

    private static long initTs = System.currentTimeMillis();

    public static ConsoleLogger getInstance() {
        if (instance == null) {
            instance = new ConsoleLogger();
        }

        return instance;
    }


    private String flatArray(byte... bytes) {
        StringBuffer buf = new StringBuffer();
        for (byte b : bytes) {
            buf.append(String.format("%02X ", b));
        }
        buf.append(" | ");
        for (byte b : bytes) {
            buf.append(String.format("%03d ", b));
        }
        return buf.toString();
    }

    private void print(String level, String text, Exception e) {
        long ts = System.currentTimeMillis() - initTs;
        System.out.println(String.format("%06d.%04d %s - %s %s", ts/1000, ts%1000, level, text, e != null ? e.getMessage() : ""));
    }

    private boolean isDebug() {
        return System.getProperty("mcdebug", "false").equalsIgnoreCase("true");
    }

    public void debug(String text) {
        if (isDebug()) {
            print("DEBUG", text, null);
        }
    }

    public void info(String text) {
        print("INFO", text, null);
    }

    public void error(String text) {
        print("ERROR", text, null);
    }

    public void error(String text, Exception e) {
        print("ERROR", text, e);
    }

    public void info(String text, byte... values) {
        print("INFO", text + " " + flatArray(values), null);
    }

    public void debug(String text, byte... values) {
        if (isDebug()) {
            print("DEBUG", text + " " + flatArray(values), null);
        }

    }
}
