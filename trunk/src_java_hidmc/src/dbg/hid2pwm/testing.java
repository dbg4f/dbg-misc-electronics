package dbg.hid2pwm;

import dbg.hid2pwm.jna.GenericSerialIo;
import dbg.hid2pwm.mc.McSerialWire;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 16.05.2011
 * Time: 21:42:52
 * To change this template use File | Settings | File Templates.
 */
class Testing {

  private static final Logger log = Logger.getLogger(Testing.class);


  public static void main(String[] args) throws IOException, InterruptedException {
    testIntLine();
  }

  public static void testIntLine() throws IOException, InterruptedException {

    GenericSerialIo io = new GenericSerialIo("COM7", "57600,n,8,1");

    McSerialWire wire = new McSerialWire(io);
    
    wire.schedulePwm1((byte)5);

    byte c = wire.getIntC();

    log.info("Counter = " + c);

    while (!Thread.currentThread().isInterrupted()) {
      byte c1 = wire.getIntC();

      if (c != c1) {
        c = c1;
        log.info("Changed = " + c);
      }
      
      Thread.sleep(1000);
    }



  }
  



}
