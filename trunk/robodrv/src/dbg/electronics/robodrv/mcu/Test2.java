package dbg.electronics.robodrv.mcu;

import java.io.IOException;
import java.net.Socket;

public class Test2 {


    public static void main(String[] args) throws IOException, InterruptedException {


        Socket s = new Socket("192.168.219.1", 5555);
        //Socket s = new Socket("127.0.0.1", 5555);

        /*

        2013.11.15 21:27:04.770 [AWT-EventQueue-0] DEBUG McuSocketCommunicator - Write bytes 55 03 01 11 01 5C
        2013.11.15 21:27:04.774 [Thread-5] DEBUG McuReportDecoder - Next byte 55
        2013.11.15 21:27:04.775 [Thread-5] DEBUG McuReportDecoder - Next byte 03
        2013.11.15 21:27:04.776 [Thread-5] DEBUG McuReportDecoder - Next byte 11
        2013.11.15 21:27:04.776 [Thread-5] DEBUG McuReportDecoder - Next byte 01
        2013.11.15 21:27:04.777 [Thread-5] DEBUG McuReportDecoder - Next byte 01
        2013.11.15 21:27:04.778 [Thread-5] DEBUG McuReportDecoder - Next byte 4D

         */

        for (int i=0; i<100000; i++){
            long time = System.currentTimeMillis();
            s.getOutputStream().write(new byte[] {0x55, 0x03, 0x01, 0x11, 0x01, 0x5C});
            //Thread.sleep(2);
            StringBuffer buf = new StringBuffer();
            buf.append(s.getInputStream().read());
            buf.append(s.getInputStream().read());
            buf.append(s.getInputStream().read());
            buf.append(s.getInputStream().read());
            buf.append(s.getInputStream().read());
            buf.append(s.getInputStream().read());
            time = System.currentTimeMillis() - time;
            String x = buf.toString();
            System.out.println(i + " " + x + " " + time);


            if (!x.equals("853171177")) {
                break;
            }

        }






    }

}
