package dbg.electronics.robodrv.logging;

/**
 * Created with IntelliJ IDEA.
 * User: dmitri
 * Date: 1/12/13
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SimpleLogger {

    void info(String text);

    void debug(String text);

    void error(String text);

    void error(String text, Throwable throwable);

}
