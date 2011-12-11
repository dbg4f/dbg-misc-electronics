package dbg.hid2pwm.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 10/16/11
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSer2Net {

    public static void main(String[] args) throws IOException {


        Socket s = new Socket("127.0.0.1", 4444);

        InputStream socketStream = s.getInputStream();

        while (!Thread.currentThread().isInterrupted()) {
            int res = socketStream.read();
            System.out.println(String.format("res = %02X\n", res));
        }


    }


}
