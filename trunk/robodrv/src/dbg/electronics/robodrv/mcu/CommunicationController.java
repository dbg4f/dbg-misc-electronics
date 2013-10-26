package dbg.electronics.robodrv.mcu;

import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.io.IOException;

import static dbg.electronics.robodrv.mcu.ChannelStatus.CONNECTED;
import static dbg.electronics.robodrv.mcu.ChannelStatus.FAILURE;

public class CommunicationController extends GenericThread implements McuBytesWriter {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private ChannelStatus status = ChannelStatus.UNKNWON;

    private McuSocketCommunicator socketCommunicator;

    private ChannelStatusListener<ChannelStatus> statusListener;

    public void setSocketCommunicator(McuSocketCommunicator socketCommunicator) {
        this.socketCommunicator = socketCommunicator;
    }

    public void setStatusListener(ChannelStatusListener<ChannelStatus> statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void write(byte[] command) throws IOException {

        try {
            socketCommunicator.write(command);
            updateStatus(CONNECTED);
        }
        catch (Exception e) {
            log.error("Communication error while sending bytes command : " + e.getMessage(), e);
            updateStatus(FAILURE);
        }

    }

    private void updateStatus(ChannelStatus communicationChannelStatus) {

        if (status != communicationChannelStatus) {

            log.info("Channel Status change " + status + "->" + communicationChannelStatus);

            status = communicationChannelStatus;

            statusListener.onStatusChanged(status);

        }

    }

    public void listeningCycle() throws IOException {

        while (!Thread.currentThread().isInterrupted()) {


            while (!socketCommunicator.isConnected()) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

            }

            try {
                socketCommunicator.waitAndProcessNextByte();
            }
            catch (Exception e) {
                log.error("Communication error while listening bytes report : " + e.getMessage(), e);
                updateStatus(FAILURE);
            }

            if (status == ChannelStatus.FAILURE) {

                try {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    break;
                }

                try {

                    //socketCommunicator.init();

                    updateStatus(CONNECTED);

                }
                catch (Exception e) {
                    log.error("Failed to restart communication : " + e.getMessage(), e);
                }


            }


        }

    }


    @Override
    public void startWork() throws IOException {
        listeningCycle();
    }
}
