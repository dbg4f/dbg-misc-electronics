package dbg.hid2pwm.jna;

import org.apache.log4j.Logger;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dbg.hid2pwm.mc.BitUtils;

public class GenericSerialIo {

  private static final Logger log = Logger.getLogger(GenericSerialIo.class);

  private Kernel32 klib = Kernel32.INSTANCE;
  private ExtKernel32 klibExt = ExtKernel32.INSTANCE;
  private WinNT.HANDLE hPort;

  public GenericSerialIo(String portName, String dcbStr) throws IOException {
    hPort = openPort(portName, dcbStr);  
  }

  private WinNT.HANDLE openPort(String portName, String dcbStr) throws IOException {

    WinNT.HANDLE hPort = klib.CreateFile(portName, WinNT.GENERIC_WRITE | 0x80000000, 0, null, WinNT.OPEN_EXISTING, WinNT.FILE_FLAG_OVERLAPPED, null);

    if (WinBase.INVALID_HANDLE_VALUE.equals(hPort)) {
        throw new IOException("Unable to open " + portName + " (" + klib.GetLastError() + ")");
    }

    ExtKernel32.DCB dcb = new ExtKernel32.DCB();

    dcb.DCBlength.setValue(dcb.size());

    boolean res = klibExt.BuildCommDCB(dcbStr, dcb);

    if (!res) {
      throw new IOException("Unable to build DCB " + portName + " (" + klib.GetLastError() + ")");
    }

    log.info("dcb.BaudRate.toString() = " + dcb.BaudRate.toString());

    res = klibExt.SetCommState(hPort, dcb);

    if (!res) {
      throw new IOException("Unable to set DCB " + portName + " (" + klib.GetLastError() + ")");
    }


    log.info("dcb = " + dcb);

    ExtKernel32.COMMTIMEOUTS timeouts = new ExtKernel32.COMMTIMEOUTS();

    res = klibExt.GetCommTimeouts(hPort, timeouts);

    if (!res) {
      throw new IOException("Unable to get timeouts " + portName + " (" + klib.GetLastError() + ")");
    }

    timeouts.ReadIntervalTimeout.setValue(0);
    timeouts.ReadTotalTimeoutConstant.setValue(0);
    timeouts.ReadTotalTimeoutMultiplier.setValue(0);

    res = klibExt.SetCommTimeouts(hPort, timeouts);

    if (!res) {
      throw new IOException("Unable to set timeouts " + portName + " (" + klib.GetLastError() + ")");
    }

    log.info("timeouts = " + timeouts);

    return hPort;
  }

  public void write(byte[] bytes) throws IOException {

    log.debug("About to write " + BitUtils.toStr(bytes[0]) + " bytes");

    WinBase.OVERLAPPED overlapped = new WinBase.OVERLAPPED();

    overlapped.hEvent = klib.CreateEvent(null, true, false, null);


    if (overlapped.hEvent == null) {
      throw new IOException("CreateEvent failed:  (" + klib.GetLastError() + ")");
    }

    IntByReference written = new IntByReference();

    boolean res = klib.WriteFile(hPort, bytes, bytes.length, written, overlapped);

    int err = klib.GetLastError();

    if (!res && err != W32Errors.ERROR_IO_PENDING) {
      throw new IOException("Write failed:  (" + err + ")");
    }
    else {
      log.debug("WriteFile result = " + res + " err = " + err);
    }

    if (!res) {

      int waitResult = klib.WaitForSingleObject(overlapped.hEvent, WinBase.INFINITE);

      log.debug("WFSO res = " + waitResult);

      if (waitResult != WinBase.WAIT_OBJECT_0) {
        throw new IOException("WFSO failed:  (" + err + ")");
      }

    }

    log.debug("written = " + written.getValue());

  }

  public byte waitForNextByte() throws IOException {

    WinBase.OVERLAPPED overlapped;
    IntByReference read = new IntByReference();

    overlapped = new WinBase.OVERLAPPED();

    overlapped.hEvent = klib.CreateEvent(null, true, false, null);

    if (overlapped.hEvent == null) {
      throw new IOException("CreateEvent failed:  (" + klib.GetLastError() + ")");
    }


    ByteBuffer buf  = ByteBuffer.allocateDirect(1).order(ByteOrder.nativeOrder());

    boolean res = klibExt.ReadFile(hPort, buf, 1, read, overlapped);

    int err = klib.GetLastError();

    if (!res && err != W32Errors.ERROR_IO_PENDING) {
      throw new IOException("Read failed:  (" + err + ")");
    }

    if (!res) {

      int waitResult = klib.WaitForSingleObject(overlapped.hEvent, WinBase.INFINITE);

      if (waitResult != WinBase.WAIT_OBJECT_0) {
       throw new IOException("WFSO(read) failed:  (" + waitResult + ")");
      }

    }
    else {
      log.debug("Read with no wait");
    }

    byte readByte = buf.get(0);

    log.debug("read = " + read.getValue() + " " + readByte + " " + (char) readByte);

    return readByte;

  }

}
