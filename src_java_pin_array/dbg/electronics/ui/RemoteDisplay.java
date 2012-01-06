package dbg.electronics.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 1/6/12
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoteDisplay {

    private OutputStream outputStream;

    RemoteDisplay(String host, int port) throws IOException {

        Socket socket = new Socket(host, port);

        outputStream = socket.getOutputStream();

    }

    public void show(int percentageValue) throws IOException {
        outputStream.write(("" + percentageValue + "\n").getBytes());
        outputStream.flush();
    }

}
