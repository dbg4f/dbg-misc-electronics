package dbg.electronics.robodrv.mcu;


import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.head.stat.StatisticCounterType;
import dbg.electronics.robodrv.head.stat.Statistics;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class McuSocketCommunicator implements McuBytesWriter {

    private static final Logger log = Logger.getLogger(McuSocketCommunicator.class);

    private String host;
    private int port;
    private Socket socket;
    private McuBytesListener bytesListener;
    private Statistics statistics;

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void setBytesListener(McuBytesListener bytesListener) {
        this.bytesListener = bytesListener;
    }

    public McuSocketCommunicator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() throws IOException {
        socket = new Socket(host, port);

        log.info("Socket connected " + socket);

        // TODO: use container to keep threads

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listeningCycle();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }).start();

    }


    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    @Override
    public void write(byte[] command) throws IOException {
        socket.getOutputStream().write(command);
        statistics.update(StatisticCounterType.RAW_SENT, command.length);
    }

    public void listeningCycle() throws IOException {

        while (!Thread.currentThread().isInterrupted()) {
            waitAndProcessNextByte();
        }

    }

    public void waitAndProcessNextByte() throws IOException {
        int nextValue = socket.getInputStream().read();
        bytesListener.onNextByte((byte)nextValue);
        statistics.update(StatisticCounterType.RAW_RECEIVED, 1);
    }

}
