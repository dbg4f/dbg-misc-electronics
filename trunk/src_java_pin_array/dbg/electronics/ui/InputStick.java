package dbg.electronics.ui;

import dbg.electronics.McCommunicationException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class InputStick implements Runnable {

    private final VectorMovementHandler movementHandler;


    public InputStick(VectorMovementHandler movementHandler) {

        this.movementHandler = movementHandler;

        new Thread(this).start();

    }

    /*
    public static void main(String[] args) {

        InputStick stick = new InputStick(new VectorMovementHandler() {
            public void onMove(int x, int y) {
                System.out.println(" " + x + " " + y + " - ");
            }
        });

    }
    */



    public void run() {

        try {
            readInputJs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readInputJs() throws IOException, InterruptedException, McCommunicationException {

       FileInputStream fileIs = new FileInputStream("/dev/input/event4");

       BufferedInputStream rd = new BufferedInputStream(fileIs);

        int x = 0x80, y = 0x80;

        while (!Thread.currentThread().isInterrupted()) {

            int buf[] = new int[16];

            for (int i=0; i<16; i++) {

                buf[i] = rd.read();

            }

            if (buf[8] == 0x03 && buf[10] == 0x01) {

                y = buf[12] & 0xFF;

                //System.out.println(String.format("%s - JS:  Y %02X (%d)", new SimpleDateFormat("mm:ss:SSS").format(new Date()), y, y));

                movementHandler.onMove(x, y);

            }

            if (buf[8] == 0x03 && buf[10] == 0x00) {

                x = buf[12] & 0xFF;

                //System.out.println(String.format("%s - JS: X %02X (%d)", new SimpleDateFormat("mm:ss:SSS").format(new Date()), x, x));

                movementHandler.onMove(x, y);

            }

        }

    }

}
