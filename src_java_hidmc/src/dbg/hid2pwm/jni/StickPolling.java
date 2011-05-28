package dbg.hid2pwm.jni;

import dbg.hid2pwm.StateChangeSink;
import dbg.hid2pwm.InputState;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 28.05.2011
 * Time: 17:24:07
 * To change this template use File | Settings | File Templates.
 */
public class StickPolling implements Runnable {

  private static final Logger log = Logger.getLogger(StickPolling.class);

  private Device usbDevice;

  private StateChangeSink sink;

  private long pollingInterval;

  public void setPollingInterval(long pollingInterval) {
    this.pollingInterval = pollingInterval;
  }

  public void setUsbDevice(Device usbDevice) {
    this.usbDevice = usbDevice;
  }

  public void setSink(StateChangeSink sink) {
    this.sink = sink;
  }

  public void init() {

    Thread thread = new Thread(this);

    thread.start();

    log.info("Started");  
  }

  public void run() {

    try {
      runPolling();
    } catch (Exception e) {
      log.error("Polling failed: " + e.getMessage(), e);
    }

  }

  private void runPolling() throws Exception {

    usbDevice.init();

    byte[] rep = usbDevice.report();

    String repText = reportText(rep);

    while (!Thread.currentThread().isInterrupted()) {
      byte[] arr = usbDevice.report();
      String newText = reportText(arr);

      if (!newText.equalsIgnoreCase(repText)) {        
        repText = newText;
        sink.trigger(new InputState(translate(arr[2]), translate(arr[1])));
      }

      Thread.sleep(pollingInterval);
    }
  }

  private static String reportText(byte[] arr) {
    StringBuffer buf = new StringBuffer();

    for (byte b : arr) {
      buf.append(String.format(" %1$02X", b));
    }

    return buf.toString();
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


}
