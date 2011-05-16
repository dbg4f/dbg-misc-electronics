package dbg.hid2pwm.jni;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 17.02.2011
 * Time: 21:18:45
 * To change this template use File | Settings | File Templates.
 */
public class Device {

  native int findDevice(short vendorId, short deviceId);

  native void prepareTransfer();

  native void readReport();

  native byte[] getReport();

  static {
    System.setProperty("java.library.path", ".");
    System.loadLibrary("hid2pwmc");
  }

  public void init() throws Exception {
    // 0583 A00B

    int handle = findDevice((short)0x0583, (short)0xA00B);

    if (handle == 0) {
      throw new RuntimeException("Device not found");
    }

    prepareTransfer();

  }

  public byte[] report() {
    readReport();
    return getReport();
  }

}
