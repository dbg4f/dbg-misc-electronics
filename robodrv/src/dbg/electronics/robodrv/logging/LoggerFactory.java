package dbg.electronics.robodrv.logging;

/**
 * Created with IntelliJ IDEA.
 * User: dmitri
 * Date: 1/12/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoggerFactory {

    public static SimpleLogger getLogger() {
        return new Log4jLogger("");
    }

    public static SimpleLogger getLogger(String name) {
        return new Log4jLogger(name);
    }

}
