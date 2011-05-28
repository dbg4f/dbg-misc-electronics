package dbg.hid2pwm;

import dbg.hid2pwm.jni.Device;
import dbg.hid2pwm.jni.StickPolling;
import dbg.hid2pwm.ui.ConsoleInputSource;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 28.05.2011
 * Time: 16:27:18
 * To change this template use File | Settings | File Templates.
 */
public class Host {

  private List<Runnable> threads;
  private Device usbDevice;
  private StateChangeSink sink;
  private StickPolling polling;
  private ConsoleInputSource console;

  public void setThreads(List<Runnable> threads) {
    this.threads = threads;
  }

  public void setUsbDevice(Device usbDevice) {
    this.usbDevice = usbDevice;
  }

  public void setSink(StateChangeSink sink) {
    this.sink = sink;
  }

  public void setPolling(StickPolling polling) {
    this.polling = polling;
  }

  public void setConsole(ConsoleInputSource console) {
    this.console = console;
  }

  @Override
  public String toString() {
    return "Host{" +
            "threads=" + threads +
            ", usbDevice=" + usbDevice +
            '}';
  }
}
