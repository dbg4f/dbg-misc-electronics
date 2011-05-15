package dbg.hid2pwm.mc;

import dbg.hid2pwm.StateChangeSink;
import dbg.hid2pwm.InputState;
import dbg.hid2pwm.jna.GenericSerialIo;

import java.io.IOException;

import org.apache.log4j.Logger;

public class McSerialWire implements StateChangeSink, McCommands {

  private static final Logger log = Logger.getLogger(McSerialWire.class);

  private GenericSerialIo io;

  private ReceiveThread rx;

  public McSerialWire(GenericSerialIo io) {
    this.io = io;
    rx = new ReceiveThread(io);
    rx.launch();
  }
 
  public void trigger(InputState inputState) {

    int fwd = inputState.getRangeX() > 0 ? 1 : 0;
    int bck = inputState.getRangeX() >= 0 ? 0 : 1;

    byte pwm0 = BitUtils.toByte(Math.abs(inputState.getRangeX() * 2));

    int left = inputState.getRangeY() >= 0 ? 0 : 1;

    byte pwm1 = BitUtils.toByte(Math.abs(inputState.getRangeY() * 2));

    System.out.println("pwm0 = " + BitUtils.toStr(pwm0) + " pwm1=" + BitUtils.toStr(pwm1));

    try {

      setPwm0(pwm0);

      setPwm1(pwm1);

      setDir(BitUtils.fromBits(0, fwd, bck, 0, 0, left, 0, 0));

      //int bt = pwm0 > 0 ? 1 : 0;

      //setDir(BitUtils.fromBits(bt, bt, bt, bt, bt, bt, bt, bt));


    } catch (Exception e) {
      log.error("Failed to send commands: " + e.getMessage(), e);
    }

  }

  public void display(String msg) {
    
  }

  private byte query(byte cmdCode) throws IOException, InterruptedException {
    rx.clear();
    io.write(new byte[]{cmdCode});
    Byte v1 = rx.poll();
    Byte v2 = rx.poll();
    if (v1 == null || v2 == null) {
      throw new IOException("timeout in query");
    }
    return (byte)((v1 << 4) + (v2 & 0xF));
  }
    
  private void send(byte cmdCode, byte value) throws IOException, InterruptedException {
    log.info("Send=" + cmdCode + " val=" + BitUtils.toStr(value));
    rx.clear();
    io.write(new byte[]{cmdCode});
    Thread.sleep(10);
    io.write(new byte[]{(BitUtils.toByte((value & 0xF0)>>4))});
    Thread.sleep(10);
    io.write(new byte[]{BitUtils.toByte(value & 0xF)});
    if (rx.poll() == null) {
      throw new IOException("timeout in write");
    }
  }

  public void setPwm0(byte value) throws IOException, InterruptedException {
    send((byte)4, value);
  }

  public byte getPwm0() throws IOException, InterruptedException {
    return query((byte)7);
  }


  public void setPwm1(byte value) throws IOException, InterruptedException {
    send((byte)5, value);
  }

  public byte getPwm1() throws IOException, InterruptedException {
    return query((byte)8);
  }

  public void setDir(byte value) throws IOException, InterruptedException {
    send((byte)6, value);
  }

  public byte getDir() throws IOException, InterruptedException {
    return query((byte)9);
  }
}
