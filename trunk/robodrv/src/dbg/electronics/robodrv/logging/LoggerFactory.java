package dbg.electronics.robodrv.logging;


public class LoggerFactory {

    public static SimpleLogger getLogger() {
        return new Log4jLogger("");
    }

    public static SimpleLogger getLogger(String name) {
        return new Log4jLogger(name);
    }

}
