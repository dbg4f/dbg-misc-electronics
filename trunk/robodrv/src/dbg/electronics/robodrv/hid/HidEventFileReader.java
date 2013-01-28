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

    private final String eventFileName;
    private final EventListener<Event> eventListener;
    private final FailureListener failureListener;

    private FileInputStream inputStream;

    public HidEventFileReader(String eventFileName, EventListener<Event> eventListener, FailureListener failureListener) {
        this.eventFileName = eventFileName;
        this.eventListener = eventListener;
        this.failureListener = failureListener;
    }


    @Override
    public void startWork() {
        try {
            readEvents();
        } catch (Exception e) {
            failureListener.onFailure(new Failure("HID input listening failed", e));
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

        log.info("HID listener started");

        while (!Thread.currentThread().isInterrupted()) {

            int buf[] = new int[sizeOfHidEvent];

            for (int i=0; i< sizeOfHidEvent; i++) {

                buf[i] = rd.read();

            }

            HidInputReport report = new HidInputReport(buf);

            eventListener.onEvent(new Event(report.formatType() + "-" + report.formatCode(), report.formatValue()));

            if (report.getType() == 0x03 && report.getCode() == 0x01) {
                eventListener.onEvent(new Event((int)report.getValue()));
            }

        }

        closeResourcesQuietly();

        log.info("HID listener stopped");

    }


}
