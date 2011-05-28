package dbg.hid2pwm.mc;

import dbg.hid2pwm.jna.SerialIo;

import java.util.concurrent.*;

import org.apache.log4j.Logger;

public class ReceiveThread implements Runnable {

  private static final Logger log = Logger.getLogger(ReceiveThread.class);

  private SerialIo io;

  private BlockingQueue<Byte> rxBuffer = new LinkedBlockingQueue<Byte>();

  public void setIo(SerialIo io) {
    this.io = io;
  }

  public void clear() {
    rxBuffer.clear();
  }

  public byte blockingNext() throws InterruptedException {
    return rxBuffer.take();
  }

  public Byte getNetWithTimeout() throws InterruptedException {
    return rxBuffer.poll(1, TimeUnit.SECONDS);
  }

  public void init() {
    new Thread(this).start();
    log.info("Rx thread started");
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
