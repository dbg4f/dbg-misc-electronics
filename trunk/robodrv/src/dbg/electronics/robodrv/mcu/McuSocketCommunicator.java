package dbg.electronics.robodrv.mcu;

import java.io.IOException;
import java.net.Socket;

public class McuSocketCommunicator implements McuBytesWriter {

    private String host;
    private int port;
    private Socket socket;
    private McuBytesListener bytesListener;

    public void setBytesListener(McuBytesListener bytesListener) {
        this.bytesListener = bytesListener;
    }

    public McuSocketCommunicator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() throws IOException {
        socket = new Socket(host, port);
    }

    @Override
    public void write(byte[] command) throws IOException {
        socket.getOutputStream().write(command);
    }

    public void listeningCycle() throws IOException {

        while (!Thread.currentThread().isInterrupted()) {
            waitAndProcessNextByte();
        }

    }

    public void waitAndProcessNextByte() throws IOException {
        int nextValue = socket.getInputStream().read();
        bytesListener.onNextByte((byte)nextValue);
    }

}
