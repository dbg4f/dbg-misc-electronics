package dbg.electronics.robodrv.logging;


public interface SimpleLogger {

    void info(String text);

    void debug(String text);

    void error(String text);

    void error(String text, Throwable throwable);

}
