package dbg.electronics.robodrv.logging;

/**
 * Created with IntelliJ IDEA.
 * User: dmitri
 * Date: 1/12/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleConsoleLogger implements SimpleLogger {

    @Override
    public void info(String text) {
        message("INFO", text, null);
    }

    @Override
    public void debug(String text) {
        message("DEBUG", text, null);
    }

    @Override
    public void error(String text) {
        message("ERROR", text, null);
    }

    @Override
    public void error(String text, Throwable throwable) {
        message("ERROR", text, throwable);
    }

    private void message(String level, String text, Throwable throwable) {

        System.out.println(level + " " + text + " " + throwable);

    }

}
