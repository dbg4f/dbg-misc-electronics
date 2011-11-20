package dbg.electronics;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 10/16/11
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSerial {

   /*

    io.write(new byte[]{cmdCode});
    io.write(new byte[]{(BitUtils.toByte((value & 0xF0)>>4))});
    io.write(new byte[]{BitUtils.toByte(value & 0xF)});

   4 set pwm 0
   7 get pwm 0
   6 set dir 0

    DDRB |= (_BV(PB5) | _BV(PB6) | _BV(PB2)); // direction 1 = output PB5-6 (pin17-18, MOSI,MISO) - dir, 2 - OC0A (pin14)


    */

    private Out out;


    private void readInputJs() throws IOException, InterruptedException {

       FileInputStream fileIs = new FileInputStream("/dev/input/event8");

       BufferedInputStream rd = new BufferedInputStream(fileIs);


        Socket socket = new Socket("127.0.0.1", 5555);



        while (!Thread.currentThread().isInterrupted()) {

            int buf[] = new int[16];

            for (int i=0; i<16; i++) {

                //fileIs.read(bb);

                buf[i] = rd.read();

                //System.out.print(String.format("%02X ", buf[i] & 0xFF));

            }


            if (buf[8] == 0x03 && buf[10] == 0x01) {
                //System.out.println("r = " + buf[12]);

                String v = (128 - buf[12]) + "\n";

                int pwm = range(buf[12], 0, 255, 0, 255, false);

                int percent = range(buf[12], 0, 255, 0, 100, true);


                System.out.println("P " + pwm + " " + percent);

                out.setPwm(pwm);

                socket.getOutputStream().write((percent + "\n").getBytes());

            }


            //System.out.println();

            //String txt = String.format("%d E %02X  C %02X - %02X %02X", System.currentTimeMillis(), buf[12], buf[13], buf[14], buf[15]);

            //System.out.println(txt);

        }



    }


    public static int range(int src, int srcMin, int srcMax, int targetMin, int targetMax, boolean invert) {

        if (src > srcMax) {
           src = srcMax;
        }

        if (src < srcMin) {
            src = srcMin;
        }

        int srcLen = srcMax - srcMin;

        int targetLen = targetMax - targetMin;


        int target  = (src - srcMin) * targetLen / srcLen + targetMin;

        if (invert) {
            target = targetMax - target;
        }

        return target;

    }


    public static void main(String[] args) throws IOException, InterruptedException {

        final TestSerial serial = new TestSerial();

        Socket socket = new Socket("127.0.0.1", 4444);

        new Thread(new In(socket)).start();

        serial.out = new Out(socket);


        new Thread(serial.out).start();

        new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            serial.readInputJs();
                        } catch (Exception e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
        ).start();

    }

    public static class In implements Runnable {

        Socket socket;

        public In(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            InputStream socketStream = null;
            try {
                socketStream = socket.getInputStream();

                System.out.println("In started");

                while (!Thread.currentThread().isInterrupted()) {
                    int res = socketStream.read();

                    System.out.println(String.format("%s - res = %02X", new SimpleDateFormat("mm:ss:SSS").format(new Date()), res));

                    if (res == -1) {
                        System.out.println("End of stream");
                        break;
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }


    public static class Out implements Runnable {

        Socket socket;

        OutputStream outputStream;

        public Out(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                outputStream = socket.getOutputStream();


                System.out.println("Out started");

                String str = null;

                InputStreamReader reader = new InputStreamReader(System.in);

                BufferedReader bufferedReader = new BufferedReader(reader);

                while ((str = bufferedReader.readLine()) != null) {

                    try {


                        int value = Integer.valueOf(str);



                        setPwm(value);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    //outputStream.write(str.getBytes());
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        public void setPwm(int value) throws IOException, InterruptedException {
            outputStream.write(0x04);
            Thread.sleep(10);
            outputStream.write((value & 0xF0)>>4);
            Thread.sleep(10);
            outputStream.write(value & 0xF);
            Thread.sleep(10);
        }

    }

}
