package jprog.comm;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 22.03.2008
 * Time: 15:58:03
 * To change this template use File | Settings | File Templates.
 */
public interface IO {

    void write(byte[] buf) throws IOException;

    byte waitByte() throws IOException;
}
