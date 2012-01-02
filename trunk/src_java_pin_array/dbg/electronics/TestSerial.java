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


    private void readInputJs() throws IOException, InterruptedException, McCommunicationException {

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

                int pwm = range(buf[12], 0, 128, 0, 255, true);

                int percent = range(buf[12], 0, 128, 0, 100, true);


                //System.out.println("P " + pwm + " " + percent);
                System.out.println(String.format("%s - JS: pwm %02X (%d)", new SimpleDateFormat("mm:ss:SSS").format(new Date()), pwm, pwm));


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
            target = targetMax - target + targetMin;
        }

        return target;

    }


    public static void main(String[] args) throws IOException, InterruptedException, McCommunicationException {

        final TestSerial serial = new TestSerial();

        Socket socket = new Socket("127.0.0.1", 4444);

        //testMcConn(socket);


        McConnection mc = new McConnection(socket);

        setPwmFreq(mc, false);

        //new Thread(new In(socket)).start();

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

    private static void setPB5(McConnection mc, boolean value) throws IOException, McCommunicationException {

        byte pb = mc.readReg(At2313Reg.PORTB);

        mc.writeReg(At2313Reg.PORTB, value ? (byte)(pb | (1<<5)) : (byte)(~(1<<5) & pb));
    }

    private static void setPwmFreq(McConnection mc, boolean normal) throws IOException, McCommunicationException {

        byte pb = mc.readReg(At2313Reg.TCCR0B);

/*

TCCR0B [0..2]

CS 2,1,0

0 0 1 clk I/O  /    (No prescaling)
0 1 0 clk I/O  /8  (From prescaler)

         */


        mc.writeReg(At2313Reg.TCCR0B, normal ? (byte)((pb & 0xF8) | 1) : (byte)((pb & 0xF8) | 2));
    }


    private static void testMcConn(Socket socket) throws IOException, McCommunicationException, InterruptedException {
        McConnection mc = new McConnection(socket);

        byte resp;

        mc.send(McCommand.ECHO, (byte)0x23);

        mc.send(McCommand.SET_FINAL_PWM1, (byte)0x7F);
        mc.send(McCommand.SET_PWM0, (byte)0x7F);

        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);
        touchPwm(mc);


        if (true) {
            System.exit(0);
            return;
        }

        //Thread.sleep(2000);

        setPB5(mc, true);

        turn(mc, 6, false, 10);

        Thread.sleep(1000);

        turn(mc, 6, true, 10);

        mc.send(McCommand.SET_PWM0, (byte)0x7F);

        setPB5(mc, false);


        System.out.println("Exit");

        System.exit(0);

        //System.out.println("resp = " + resp);

        //socket.close();
    }

    private static void touchPwm(McConnection mc) throws IOException, McCommunicationException, InterruptedException {
        System.out.println("Turn on");

        boolean pb5En = true;


        setPB5(mc, pb5En);


        mc.send(McCommand.SET_PWM0, (byte)0x10);
        mc.send(McCommand.SET_PWM1, (byte)0x10);

        Thread.sleep(3000);

        mc.send(McCommand.SET_PWM0, (byte)0x7F);
        mc.send(McCommand.SET_PWM1, (byte)0x7F);

        Thread.sleep(1000);

        mc.send(McCommand.SET_PWM0, (byte)0xF0);
        mc.send(McCommand.SET_PWM1, (byte)0xF0);

        Thread.sleep(3000);

        setPB5(mc, !pb5En);
    }

    private static void turn(McConnection mc, int steps, boolean dir, int pwm) throws IOException, McCommunicationException, InterruptedException {

        mc.send(McCommand.SET_PWM0, (byte)0x7F);

        mc.send(McCommand.SCHEDULE_PWM1, (byte)0);

        mc.send(McCommand.SCHEDULE_PWM1, (byte) steps);

        mc.send(McCommand.SET_PWM0, dir ? (byte)(0xFF- pwm) : (byte)pwm);

        int resp = mc.send(McCommand.GET_INT_COUNTER);

        resp = mc.send(McCommand.GET_INT_COUNTER);
        Thread.sleep(30);
        resp = mc.send(McCommand.GET_INT_COUNTER);
        Thread.sleep(30);
        resp = mc.send(McCommand.GET_INT_COUNTER);
        Thread.sleep(30);
        resp = mc.send(McCommand.GET_INT_COUNTER);
        Thread.sleep(30);
        resp = mc.send(McCommand.GET_INT_COUNTER);
        Thread.sleep(30);
        resp = mc.send(McCommand.GET_INT_COUNTER);
        Thread.sleep(30);
        resp = mc.send(McCommand.GET_INT_COUNTER);

        Thread.sleep(1000);

        resp = mc.send(McCommand.GET_INT_COUNTER);

        mc.send(McCommand.SET_PWM0, (byte)0x7F);


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

                    System.out.println(String.format("%s - in %02X (%d)", new SimpleDateFormat("mm:ss:SSS").format(new Date()), res, res));

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

        //Socket socket;

        OutputStream outputStream;

        McConnection mc;

        public Out(Socket socket) throws IOException, McCommunicationException {
            //this.socket = socket;
            mc = new McConnection(socket);
            mc.send(McCommand.ECHO, (byte)0x11);
        }

        public void run() {
            try {

                //outputStream = socket.getOutputStream();


                System.out.println("Out started");

                String str = null;

                InputStreamReader reader = new InputStreamReader(System.in);

                BufferedReader bufferedReader = new BufferedReader(reader);

                while ((str = bufferedReader.readLine()) != null) {

                    try {


                        String[] tokens = str.trim().split("\\s");

                        for (String token : tokens) {

                            if (token.startsWith("w")) {
                                Thread.sleep(Integer.valueOf(token.substring(1)));
                            }
                            else if (token.length() == 4) {

                                int cmd = Integer.parseInt(token.substring(0, 2), 16);
                                int value = Integer.parseInt(token.substring(2), 16);

                                System.out.println(String.format("cmd %02X value = %02X", cmd, value));

                                sendCommand((byte)cmd, (byte)value);
                                //setPwm(value);
                                //writeRaw(value);
                            }
                            else {
                                System.out.println("token not recognized: " + token);
                            }
                        }


                        //int value = Integer.valueOf(str);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //outputStream.write(str.getBytes());
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        public void setPwm(int value) throws IOException, InterruptedException, McCommunicationException {

            //sendCommand((byte)0x04,(byte) value);

            mc.send(McCommand.SET_PWM0, (byte)value);


           /*
            outputStream.write(0x04);
            Thread.sleep(1);
            outputStream.write((value & 0xF0) >> 4);
            Thread.sleep(1);
            outputStream.write(value & 0xF);
            Thread.sleep(1);*/
        }

        public void sendCommand(byte code, byte value) throws IOException {

            byte[] outBytes = new byte[] {0x55, 0x02, code, value, 0x00};

            outBytes[4] = crc_calc(outBytes[0], outBytes[1], outBytes[2], outBytes[3]);

            outputStream.write(outBytes);
        }

        public void writeRaw(int value) throws IOException, InterruptedException {
            outputStream.write(value);
        }


    }

    static byte crc_calc(byte... content) {
        byte crc = (byte) 0xFF;

        for (byte b : content) {
            crc = crc_update(crc, b);
        }

        return crc;
    }

    static byte crc_update(byte crc, byte data) {

        int i;

        crc ^= data;

        for (i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0x00) {
                crc = (byte) ((crc << 1) ^ 0xE5);
            } else {
                crc <<= 1;
            }
        }

        return crc;
    }


}
/*

0x01 echo
0x04 [param] set pwm0
0x05 [param] set pwm1
0x10 get int_c
0x11 schedule pwm1 off
0x12 cancel schedule pwm1
0x13 get schedule status
0x14 [reg] read reg
0x15 [reg, value] write reg
0x16 [param] set pwm1 final value

*/