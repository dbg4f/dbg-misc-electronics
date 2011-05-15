package dbg.hid2pwm.mc;

import dbg.hid2pwm.jna.GenericSerialIo;
import dbg.hid2pwm.StateChangeSink;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;

public class Emulator implements Runnable {

  private static final Logger log = Logger.getLogger(Emulator.class);

  private StateChangeSink sink;

  private Map<String, String> reg = new LinkedHashMap<String, String>();

  private GenericSerialIo io;

  public Emulator(GenericSerialIo io, StateChangeSink sink) {
    this.io = io;
    this.sink = sink;
  }

  public void launch() {

    new Thread(this).start();


  }

  private byte next() throws IOException {
    byte v = io.waitForNextByte();
    log.debug("Read: " + BitUtils.toStr(v));
    return v;
  }

  private void write(byte v) throws IOException {
    log.debug("Write: " + BitUtils.toStr(v));
    io.write(new byte[]{v});
  }

  public void run() {

    log.info("Started");

    while (!Thread.currentThread().isInterrupted()) {

      try {

        byte cmdCode = next();

        log.debug("Cmd=" + BitUtils.toStr(cmdCode));

        switch (cmdCode) {
          case 4:
          case 5:
          case 6:
            byte val = receive();
            write((byte)0);
            log.info("Cmd=" + cmdCode + " val: " + BitUtils.toStr(val));

            reg.put(String.valueOf(cmdCode), BitUtils.toStr(val));

            sink.display(reg.toString());

            break;
        }

      } catch (IOException e) {
        log.error("Failed to read/write: " + e.getMessage(), e);
      }

    }
  }

  public byte receive() throws IOException {
    return BitUtils.toByte(((next() << 4) & 0xF0) + (next() & 0xF));
  }

}
