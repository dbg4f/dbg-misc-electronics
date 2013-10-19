package dbg.electronics.robodrv.logging;

import org.apache.log4j.Logger;

/**
 * Created: 1/25/13  8:46 PM
 */
public class Log4jLogger extends SimpleConsoleLogger {

    Logger logger;

    public Log4jLogger(String name) {
        super(name);
        logger = Logger.getLogger(name);
    }

    @Override
    protected void message(String level, String text, Throwable throwable) {
        if ("INFO".equalsIgnoreCase(level) && logger.isInfoEnabled()){
            logger.info(text, throwable);
        }
        else if ("DEBUG".equalsIgnoreCase(level) && logger.isDebugEnabled()) {
            logger.debug(text, throwable);
        }
        else if ("ERROR".equalsIgnoreCase(level)) {
            logger.error(text, throwable);
        }



    }
}
