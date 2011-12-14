package dbg.electronics;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 12/14/11
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface McLogger {

    void debug(String text);

    void info(String text);

    void info(String text, byte... values);

    void debug(String text, byte... values);

    void error(String text);

    void error(String text, Exception e);

}
