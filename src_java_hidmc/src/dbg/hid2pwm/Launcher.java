package dbg.hid2pwm;

import dbg.hid2pwm.jni.Device;
import dbg.hid2pwm.InputState;
import dbg.hid2pwm.mc.McSerialWire;
import dbg.hid2pwm.mc.Emulator;
import dbg.hid2pwm.mc.BitUtils;
import dbg.hid2pwm.jna.GenericSerialIo;
import dbg.hid2pwm.ui.StickPanel;
import org.apache.log4j.Logger;

public class Launcher {

  private static final Logger log = Logger.getLogger(Launcher.class);

  public static void main(String[] args) throws Exception {

    final StickPanel panel = new StickPanel();

    new Thread(new Runnable() {

      public void run() {
        try {
          startPolling(panel);
        } catch (Exception e) {
          log.error("Polling failed: " + e.getMessage(), e);
        }
      }
    }).start();

  }

  private static void startPolling(StickPanel panel) throws Exception {
    Device d = new Device();

    d.init();

    byte[] rep = d.report();

    System.out.println("rep. = " + rep.length);

    String repText = reportText(rep);

    System.out.println(repText);

    GenericSerialIo io = new GenericSerialIo("COM7", "57600,n,8,1");

    //new Emulator(new GenericSerialIo("COM7", "57600,n,8,1"), panel).launch();

    McSerialWire wire = new McSerialWire(io);

    StateChangeSink sink = new CompositeSink(panel, wire);

    while (!Thread.currentThread().isInterrupted()) {
      byte[] arr = d.report();
      String newText = reportText(arr);

      if (!newText.equalsIgnoreCase(repText)) {
        //System.out.println(newText);
        repText = newText;
        sink.trigger(new InputState(translate(arr[2]), translate(arr[1])));
      }

      Thread.sleep(10);
    }
  }


  public static int translate(byte src) {
    
    //int value = (src & 0xFF) - 0x80;

    int v = (src & 0xFF);

    int sign = (v & 0x80);
    int val = (v & 0x7F);

    if (v == 0) {
      val = 1;
    }
     
    //System.out.println("src = " + BitUtils.toStr(src) + " sgn=" + sign + " val=" + Integer.toHexString(val));
    return sign == 0 ? val-0x80 : val;

  }


  private static String reportText(byte[] arr) {
    StringBuffer buf = new StringBuffer();

    for (byte b : arr) {
      buf.append(String.format(" %1$02X", b));
    }

    return buf.toString();
  }

}
