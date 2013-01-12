package dbg.electronics.robodrv;

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

    public HidEventListener(String eventFileName, InputListener inputListener, FailuresListener failuresListener) {
        this.eventFileName = eventFileName;
        this.inputListener = inputListener;
        this.failuresListener = failuresListener;
    }

    @Override
    public void launch() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    readEvents();
                } catch (IOException e) {
                    failuresListener.onFailure(new Failure("HID input listening failed", e));
                }

            }
        }).start();

    }

    private void readEvents() throws IOException {

        FileInputStream fileIs = new FileInputStream(eventFileName);

        BufferedInputStream rd = new BufferedInputStream(fileIs);

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





    }


}
