package dbg.hid2pwm.jni;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 17.02.2011
 * Time: 21:18:45
 * To change this template use File | Settings | File Templates.
 */
public class Device {

    private static final Logger log = Logger.getLogger(Device.class);

  native int findDevice(short vendorId, short deviceId);

  native void prepareTransfer();

  native void readReport();

  native byte[] getReport();

  private String vendorId = "0583";
  private String deviceId = "A00B";

  public void setVendorId(String vendorId) {
    this.vendorId = vendorId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public void init() throws Exception {

    System.setProperty("java.library.path", ".");
    System.loadLibrary("hid2pwmc");

    // 0583 A00B

    short vid = (short)Integer.parseInt(vendorId, 16);
    short dev = (short)Integer.parseInt(deviceId, 16);

    int handle = findDevice(vid, dev);

    if (handle == 0) {
      throw new RuntimeException("Device not found: " + toString());
    }

    log.info("Device found: " + toString());

    prepareTransfer();

  }

  public byte[] report() {
    readReport();
    return getReport();
  }

  @Override
  public String toString() {
    return "Device{" +
            "vendorId='" + vendorId + '\'' +
            ", deviceId='" + deviceId + '\'' +
            '}';
  }
}
