package dbg.electronics.robodrv.hid;

import dbg.electronics.robodrv.*;
import dbg.electronics.robodrv.head.Failure;
import dbg.electronics.robodrv.head.FailureListener;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class HidEventFileReader extends GenericThread {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    public int sizeOfHidEvent = HidInputReport.HID_REPORT_SIZE;

    private String eventFileName;
    private EventListener<HidInputReport> eventListener;
    private FailureListener failureListener;

    private FileInputStream inputStream;

    public void setEventFileName(String eventFileName) {
        this.eventFileName = eventFileName;
    }

    public void setEventListener(EventListener<HidInputReport> eventListener) {
        this.eventListener = eventListener;
    }

    public void setFailureListener(FailureListener failureListener) {
        this.failureListener = failureListener;
    }

    @Override
    public void startWork() {
        try {
            readEvents();
        } catch (Exception e) {
            failureListener.onFailure(new Failure("HID input listening failed", e));
            log.error("HID input listening failed " + e.getMessage(), e);
        }
    }

    private void closeResourcesQuietly() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("Failed to close HID input stream " + e.getMessage(), e);
            }
        }
    }

    private void readEvents() throws IOException {

        inputStream = new FileInputStream(eventFileName);

        BufferedInputStream rd = new BufferedInputStream(inputStream);

        log.info("HID listener started: " + eventFileName);

        while (!Thread.currentThread().isInterrupted()) {

            int buf[] = new int[sizeOfHidEvent];

            for (int i=0; i< sizeOfHidEvent; i++) {

                buf[i] = rd.read();

            }

            HidInputReport report = new HidInputReport(buf);

            eventListener.onEvent(report);

        }

        closeResourcesQuietly();

        log.info("HID listener stopped");

    }


}
