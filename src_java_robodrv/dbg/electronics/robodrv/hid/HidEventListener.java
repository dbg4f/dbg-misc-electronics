package dbg.electronics.robodrv.hid;

import dbg.electronics.robodrv.*;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class HidEventListener implements Threaded {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private final String eventFileName;
    private final InputListener inputListener;
    private final FailuresListener failuresListener;

    private FileInputStream inputStream;
    private Thread listeningThread;


    public HidEventListener(String eventFileName, InputListener inputListener, FailuresListener failuresListener) {
        this.eventFileName = eventFileName;
        this.inputListener = inputListener;
        this.failuresListener = failuresListener;
    }

    @Override
    public void launch() {

        listeningThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    readEvents();
                } catch (IOException e) {

                   closeResourcesQuietly();

                    failuresListener.onFailure(new Failure("HID input listening failed", e));
                }

            }
        });

        listeningThread.start();

    }

    @Override
    public void terminate() {

        if (listeningThread != null) {

            listeningThread.interrupt();

        }

    }

    private void closeResourcesQuietly(){
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // result is not meaningful
            }
        }
    }

    private void readEvents() throws IOException {

        inputStream = new FileInputStream(eventFileName);

        BufferedInputStream rd = new BufferedInputStream(inputStream);

        log.info("HID listener started");

        while (!Thread.currentThread().isInterrupted()) {



            int buf[] = new int[16];

            for (int i=0; i<16; i++) {

                buf[i] = rd.read();

                //System.out.print(String.format("%02X ", buf[i] & 0xFF));

            }


            if (buf[8] == 0x03 && buf[10] == 0x01) {

                int jsRaw = buf[12];

                inputListener.onEvent(new InputEvent(jsRaw));

            }


        }

        closeResourcesQuietly();

        log.info("HID listener stopped");



    }


}
