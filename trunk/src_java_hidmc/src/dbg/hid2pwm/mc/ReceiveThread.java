package dbg.hid2pwm.mc;

import dbg.hid2pwm.jna.GenericSerialIo;

import java.util.concurrent.*;

import org.apache.log4j.Logger;

public class ReceiveThread implements Runnable {

  private static final Logger log = Logger.getLogger(ReceiveThread.class);

  private GenericSerialIo io;

  private BlockingQueue<Byte> rxBuffer = new LinkedBlockingQueue<Byte>();


  public ReceiveThread(GenericSerialIo io) {
    this.io = io;
  }

  public void clear() {
    rxBuffer.clear();
  }

  public byte next() throws InterruptedException {
    return rxBuffer.take();
  }

  public Byte poll() throws InterruptedException {
    return rxBuffer.poll(1, TimeUnit.SECONDS);
  }

  public void launch() {
    new Thread(this).start();
  }

  public void run() {

    try {
      while (!Thread.currentThread().isInterrupted()) {
        rxBuffer.add(io.waitForNextByte());
      }
    } catch (Exception e) {
      log.error("Exception while reading: " + e.getMessage(), e);
    }

  }
}
