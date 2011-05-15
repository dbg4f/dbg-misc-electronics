package dbg.hid2pwm.jna;

import java.io.IOException;

import org.apache.log4j.Logger;

public class Main {

  private static final Logger log = Logger.getLogger(Main.class);

  private GenericSerialIo io;

  public Main() throws IOException {
    io = new GenericSerialIo("COM1", "9600,n,8,1");
  }
  /*
  public static void main(String[] args) throws IOException, InterruptedException {

    Main m = new Main();

    m.startReading();

    for (int i=0; i<3; i++) {
     // m.write(hPort, "AB".getBytes("ASCII"));
    //  Thread.sleep(500);
    }


    //m.klib.CloseHandle(hPort);

  }
  
  public void startReading() {

    new Thread(new Runnable() {
      public void run() {
        try {
          while (!Thread.currentThread().isInterrupted()) {
            io.waitForNextByte();
          }          
        } catch (IOException e) {
          log.error("Exception while reading: " + e.getMessage(), e);            
        }
      }
    }).start();

  }
    */

}
