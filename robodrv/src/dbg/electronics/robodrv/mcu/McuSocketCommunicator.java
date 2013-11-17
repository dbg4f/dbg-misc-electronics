package dbg.electronics.robodrv.mcu;


import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.head.stat.StatisticCounterType;
import dbg.electronics.robodrv.head.stat.Statistics;
import dbg.electronics.robodrv.io.FormatUtils;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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

        if (socket != null) {
            return;
        }

        socket = new Socket(host, port);

        log.info("Socket connected " + socket);

        // TODO: use container to keep threads
        /*
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
          */
    }


    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    @Override
    public void write(byte[] command) throws IOException {
        log.debug("Write bytes " + FormatUtils.formatArray(command));
        /*
        for (byte b : command) {
          socket.getOutputStream().write(b);
            try {
                log.debug("Write byte " + FormatUtils.formatArray(new byte[]{b}));

                Thread.sleep(100);

            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        */
        socket.getOutputStream().write(command);
        statistics.update(StatisticCounterType.RAW_SENT, command.length);
    }

    public void listeningCycle() throws IOException, McuCommunicationException {

        while (!Thread.currentThread().isInterrupted()) {
            waitAndProcessNextByte();
        }

    }

    public void waitAndProcessNextByte() throws IOException, McuCommunicationException {
        // TODO: check for -1 value (end of stream)
        int nextValue = socket.getInputStream().read();

        if (nextValue == -1) {
            throw new McuCommunicationException("End of stream detected");
        }

        bytesListener.onNextByte((byte)nextValue);
        statistics.update(StatisticCounterType.RAW_RECEIVED, 1);
    }

}
