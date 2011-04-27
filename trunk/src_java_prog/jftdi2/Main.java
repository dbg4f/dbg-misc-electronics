/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jftdi2;


import java.io.IOException;
import java.util.Set;
import java.util.TooManyListenersException;
import static java.lang.Thread.sleep;

import jd2xx.JD2XX;
import jd2xx.JD2XXEventListener;
import jd2xx.JD2XXEvent;
import jd2xx.JD2XX.ProgramData;
import jprog.comm.*;

/**
 *
 * @author Bogdel
 */
public class Main implements CommandSender, DeviceWrapper {

    JD2XX jd;

   private int exchgByte(int value) throws IOException {

       int result = 0;

       for (int i = 7; i >= 0; i--) {
           boolean bitValue = (((value >> i) & 1) != 0);
           result |= exchgBit(bitValue) ? (1 << i) : 0;
       }

       return result;
   }

   private int[] toInt(byte[] input) {
       int[] out = new int[input.length];
       int i = 0;
       for (int in : input) {
           out[i++] = in;
       }
       return out;
   } 

   private int[] io(int[] input) throws IOException {
       int[] out = new int[input.length];
       int i = 0;
       for (int in : input) {
           out[i++] = exchgByte(in);
       }
       return out;
   }


   private boolean exchgBit(boolean bit) throws IOException {

       int data = bit ? 1 : 0;

       boolean b1 = exchg(0, data, 0);
       boolean b2 = exchg(1, data, 0);
       boolean b3 = exchg(0, data, 0);

       return b2;

   }


   private void delay(long msec) {
       try {
           sleep(msec);
       } catch (InterruptedException e) {
           // skip
       }
   }

   private boolean exchg(int sck, int mosi, int rst, long delayMsec) throws IOException {
       delay(delayMsec);
       return exchg(sck, mosi, rst);       
   }
   private boolean exchg(int sck, int mosi, int rst) throws IOException {
       /*
 6         - CBUS0  SCK
 7         - CBUS1  MOSI
 9         - CBUS2  RST
10         - CBUS3  MISO(<-)

        */

       // 1,1,1,0 - sck,mosi,rst,0

       int data = 0x70;

       data |= rst  != 0 ? 0x04 : 0x00;
       data |= mosi != 0 ? 0x02 : 0x00;
       data |= sck  != 0 ? 0x01 : 0x00;

       jd.setBitMode(data, 0x20);

       int result = jd.getBitMode();

       //System.out.println("io = " + HEX.formatByte(data)+ "/" + HEX.formatByte(result) + "=" + sck + "" + mosi + "" + rst + " " + (result & 0x08));
       //LOG.logger.debug("io = " + HEX.formatByte(data)+ "/" + HEX.formatByte(result) + "=" + sck + "" + mosi + "" + rst + " " + (result & 0x08));

       return (result & 0x08) != 0;

   }

   private void startProg() throws IOException {
       /*

     1. Power-up sequence:
     Apply power between VCC and GND while RESET and SCK are set to ?0?. In
     some systems, the programmer can not guarantee that SCK is held low during
     power-up. In this case, RESET must be given a positive pulse of at least two
     CPU clock cycles duration after SCK has been set to 0.

       */

       long delay = 30;

       exchg(1, 0, 1, delay);    // sck, out, rst
       exchg(1, 0, 0, delay);
       exchg(0, 0, 1, delay);
       exchg(0, 0, 0, delay);

       /*
       2. Wait for at least 20 ms and enable serial programming by sending the Programming
       Enable serial instruction to pin MOSI.
       */
   }

    private static byte[] readFile(String fileName) throws IOException {
        HEX hex = new HEX();
        return hex.readFile(fileName);
    }

   private void prog() throws IOException {

       startProg();

       int[] out = io(toInt(Commands.SYNC));

       System.out.println("dump = " + HEX.dumpHex(out));

       Commands c = new Commands(this); 

       c.sync();

       HEX.dumpHex(c.readSignature());

       LOG.logger.info("signature = " + HEX.dumpHex(c.readSignature()));

       //System.out.println("out = " + Arrays.asList((int[])out));

       byte[] code = readFile("C:\\Projects\\repos\\mc\\mdrv\\default\\mdrv.hex");

       LOG.logger.info("code = " + HEX.dumpHex(code));

       c.erase();

       writeEeprom(code, c);

       //readTest(c);

       //c.writeFuse(HEX.parseHexByte("EC"));

       readExtraCfg(c);
   }

    private static void writeEeprom(byte[] code, Commands cmd) {
        code = HEX.align32(code);
        LOG.logger.info("Write code = " + HEX.dumpHex(code));
        cmd.writeProgramMemory(code);
        LOG.logger.info("Write complete");
    }


    private static void readTest(Commands cmd) {
        byte[] mem = cmd.readProgramMemory(512);

        String dump = HEX.dumpHex(mem);

        LOG.logger.info("dump = " + dump);

        System.out.println("dump = " + dump);
    }

    

    public byte[] process(byte[] command) {

        byte[] resultBytes;

        try {
            int[] result = io(toInt(command));

            resultBytes = new byte[result.length];

            for (int i=0; i<result.length; i++) {
                resultBytes[i] = (byte) (result[i] & 0xFF);
            }

            return resultBytes;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, TooManyListenersException {

        Main m = new Main();
        m.setup();
        //m.prog();
        m.pwm();


    }

    private void setup() throws IOException {
        jd = new JD2XX();
        jd.open(0);
    }


  private static int res;

  private final Object synch = new Object();

  public void write(int pwm1, int pwm2, int dir) throws IOException, InterruptedException {

    LOG.logger.info("Write = " + HEX.dumpHex(new byte[]{(byte)pwm1, (byte)pwm2, (byte)dir}));

    res = -1;

    jd.write(pwm1);
    sleep(20);
    jd.write(pwm2);
    sleep(20);
    jd.write(dir);
    //sleep(20);

    synchronized (synch) {
      synch.wait(30);  
    }


    if (res == -1) {
      throw new IOException("Timeout waiting response byte");
    }

    int sum = ((pwm1 + pwm2 + dir) % 0x100);
    if (res != sum) {
      throw new IOException("Counter mismatch: sum="+sum+", res="+res);
    }

    //LOG.logger.info("res11 = " + res);

  }

  public void echo(int value) throws IOException, InterruptedException {

    LOG.logger.info("Echo = " + HEX.dumpHex(new byte[]{(byte)value}));

    res = -1;

    jd.write(1);
    sleep(20);
    jd.write(value);

    synchronized (synch) {
      synch.wait(30);
    }

    if (res == -1) {
      throw new IOException("Timeout waiting response byte");
    }

    if (res != value) {
      //throw new IOException("Counter mismatch: value="+value+", res="+res);
      LOG.logger.error("Counter mismatch: value="+value+", res="+res);
    }

    //LOG.logger.info("res11 = " + res);

  }

  public void writePwm0(int value) throws IOException, InterruptedException {

    LOG.logger.info("write pwm0 = " + HEX.dumpHex(new byte[]{(byte)value}));

    res = -1;

    jd.write(4);
    sleep(30);
    jd.write((value >> 4) & 0x0F);    
    sleep(30);
    jd.write(value & 0x0F);

    synchronized (synch) {
      synch.wait(30);
    }

    if (res == -1) {
      throw new IOException("Timeout waiting response byte");
    }

    if ((res & 0xF) != (value & 0xF)) {
      //throw new IOException("Counter mismatch: value="+value+", res="+res);
      //LOG.logger.error("PWM0 state 4 bits mismatch: value="+value+", res="+res);
    }

    //LOG.logger.info("res11 = " + res);

  }

  public int readPwm0() throws IOException, InterruptedException {

    res = -1;

    jd.write(7);

    synchronized (synch) {
      synch.wait(30);
    }

    if (res == -1) {
      throw new IOException("Timeout waiting response byte");
    }

    LOG.logger.info("read pwm0 = " + HEX.dumpHex(new byte[]{(byte)res}));

    return res;

  }

  private void pwm() throws IOException, TooManyListenersException, InterruptedException {

        int baudRate = 57600;

        //jd.setBaudRate(4800);
        //jd.setBaudRate(38400);
        jd.setBaudRate(baudRate);

        LOG.logger.info("speed = " + baudRate);

        jd.addEventListener(
                new JD2XXEventListener() {
                    public void jd2xxEvent(JD2XXEvent ev) {
                        JD2XX jo = (JD2XX) ev.getSource();
                        int et = ev.getEventType();
                        try {
                            if ((et & JD2XX.EVENT_RXCHAR) != 0) {
                                int r = jo.getQueueStatus();
                              byte[] rxArray = jo.read(r);
                              String rxText = HEX.hexLine(rxArray);
                              //System.out.println("RX event: " + /*new String(jo.read(r))*/  rxText);


                              LOG.logger.info("RX = " + rxText);

                              if (res == -1) {
                                res = rxArray[0];
                                synchronized (synch) {
                                  synch.notify();
                                }

                              }

                            } else if ((et & JD2XX.EVENT_MODEM_STATUS) != 0) {
                                System.out.println("Modem status event");
                            }
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );

        jd.notifyOnEvent(JD2XX.EVENT_RXCHAR | JD2XX.EVENT_MODEM_STATUS, true);
        //ret = jd.write(msg.getBytes());
        //System.out.println(ret + " bytes sent.");


        LOG.logger.info("do reset");
        sleep(1000);

        /*
        jd.write(4);
        sleep(30);
        jd.write(1);
        sleep(30);
        jd.write(1);
        sleep(2000);
        //jd.write(7);
        //sleep(2000);
         */
    /*
         for (int i=0; i<20; i++) {
           //byte b = 2;
           byte[] txBuf = {(byte)(4 & 0xFF)};
           int ret = jd.write(txBuf);
           String txText = HEX.dumpHex(txBuf);
           //System.out.println("i=" + i + txText);
           LOG.logger.info("TX buf = " + txText);

           sleep(30);
         }


        sleep(20000);
        
      */

      //sleep(200000);

    for (;;) {
      new StatefulDriver(this).work();  
    }

    
         //System.exit(0);

        //sleep(2000);

        //ret = jd.write(/*msg.getBytes()*/new byte[]{0x01});
        //System.out.println(ret + " bytes sent.");

        //sleep(3000);

    }




    public static void main2(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        
        Set<String> a;
        
        JD2XX jd = new JD2XX();
        
        Object[] devs = jd.listDevicesBySerialNumber();
        
        System.out.println("devs=" + devs.length);
        
        for (int i=0; i<devs.length; ++i) {
            System.out.println(devs[i]);
        }
            
        devs = jd.listDevicesByLocation();
        for (int i=0; i<devs.length; ++i) {
            System.out.println(Integer.toHexString((Integer)devs[i]));
        }

        jd.open(0);

        
        jd.setBaudRate(19200);
        jd.setDataCharacteristics(8, JD2XX.STOP_BITS_1, JD2XX.PARITY_NONE);
        jd.setFlowControl(JD2XX.FLOW_NONE, 0, 0);
        jd.setTimeouts(1000, 1000);

        int type = jd.getDeviceInfo().type;

        System.out.println("type = " + type);

        String msg = "Hello Duke.";
        msg += "The message is 'Fiat experimentum in corpore vili'";
        int ret = jd.write(msg.getBytes());
        System.out.println(ret + " bytes sent.");
        
        ProgramData pd = jd.eeRead();
        System.out.println("data = " + pd.toString());

        if (true) {
            return;
        }


/*
        System.out.println("Set RTS");
        jd.setRts();
        Thread.sleep(1000);
        
        System.out.println("Clr RTS");
        jd.clrRts();
        Thread.sleep(1000);
        

        System.out.println("Set RTS");
        jd.setRts();
        Thread.sleep(1000);
        
        System.out.println("Clr RTS");
        jd.clrRts();
        Thread.sleep(1000);
  */
  

        int bm = jd.getBitMode();
        
        System.out.println("bm = " + bm);


        for (;;) {
            jd.setBitMode(0xF0, 0x20);
            sleep(50);
            jd.setBitMode(0xFF, 0x20);
            sleep(50);


            if (false) {
                break;
            }
        }


        jd.setBitMode(0xF0, 0x20);
        sleep(1000);
        jd.setBitMode(0xFF, 0x20);
        sleep(1000);
        jd.setBitMode(0xF0, 0x20);               
        sleep(1000);

        bm = jd.getBitMode();
        
        System.out.println("bm = " + bm);

        jd.setBitMode(0x00, 0x20);

        bm = jd.getBitMode();

        System.out.println("bm = " + bm);

      
        //ins.close();
        //outs.jd2xx.close();
        //outs.close();

   
        
    }


  private static void readExtraCfg(Commands cmd) {
      String signature = HEX.hexLine(cmd.readSignature());
      LOG.logger.info("signature = " + signature);

      String fuse = HEX.formatByte(cmd.readFuse());
      LOG.logger.info("fuse = " + fuse);

      String fuseHi = HEX.formatByte(cmd.readFuseHigh());
      LOG.logger.info("fuseHi = " + fuseHi);

      String fuseExt = HEX.formatByte(cmd.readFuseExt());
      LOG.logger.info("fuseExt = " + fuseExt);

      String lock = HEX.formatByte(cmd.readLock());
      LOG.logger.info("lock = " + lock);
  }

}
