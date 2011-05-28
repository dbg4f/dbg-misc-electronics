package dbg.hid2pwm.jna;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 28.05.2011
 * Time: 16:55:54
 * To change this template use File | Settings | File Templates.
 */
public interface SerialIo {

  void write(byte[] bytes) throws IOException;

  byte waitForNextByte() throws IOException;
}
