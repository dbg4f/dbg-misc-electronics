package dbg.hid2pwm.jna;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.StdCallLibrary;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 23.02.2011
 * Time: 21:33:28
 * To change this template use File | Settings | File Templates.
 */
public interface ExtKernel32 extends StdCallLibrary {
  

  ExtKernel32 INSTANCE = (ExtKernel32) Native.loadLibrary("kernel32", ExtKernel32.class, W32APIOptions.UNICODE_OPTIONS);

  public static class DCB extends Structure {
    public WinDef.DWORD DCBlength;      /* sizeof(DCB)                     */
    public WinDef.DWORD BaudRate;       /* Baudrate at which running       */
    public WinDef.DWORD fBinary;     /* Binary Mode (skip EOF check)    */
    public WinDef.DWORD fParity;     /* Enable parity checking          */
    public WinDef.DWORD fOutxCtsFlow; /* CTS handshaking on output       */
    public WinDef.DWORD fOutxDsrFlow; /* DSR handshaking on output       */
    public WinDef.DWORD fDtrControl;  /* DTR Flow control                */
    public WinDef.DWORD fDsrSensitivity; /* DSR Sensitivity              */
    public WinDef.DWORD fTXContinueOnXoff; /* Continue TX when Xoff sent */
    public WinDef.DWORD fOutX;       /* Enable output X-ON/X-OFF        */
    public WinDef.DWORD fInX;        /* Enable input X-ON/X-OFF         */
    public WinDef.DWORD fErrorChar;  /* Enable Err Replacement          */
    public WinDef.DWORD fNull;       /* Enable Null stripping           */
    public WinDef.DWORD fRtsControl;  /* Rts Flow control                */
    public WinDef.DWORD fAbortOnError; /* Abort all reads and writes on Error */
    public WinDef.DWORD fDummy2;     /* Reserved                        */
    public WinDef.WORD wReserved;       /* Not currently used              */
    public WinDef.WORD XonLim;          /* Transmit X-ON threshold         */
    public WinDef.WORD XoffLim;         /* Transmit X-OFF threshold        */
    public byte ByteSize;        /* Number of bits/byte, 4-8        */
    public byte Parity;          /* 0-4=None,Odd,Even,Mark,Space    */
    public byte StopBits;        /* 0,1,2 = 1, 1.5, 2               */
    public char XonChar;         /* Tx and Rx X-ON character        */
    public char XoffChar;        /* Tx and Rx X-OFF character       */
    public char ErrorChar;       /* Error replacement char          */
    public char EofChar;         /* End of Input character          */
    public char EvtChar;         /* Received Event character        */
    public WinDef.WORD wReserved1;      /* Fill for now.                   */


    @Override
    public String toString() {
      return "DCB{" +
              "DCBlength=" + DCBlength +
              ", BaudRate=" + BaudRate +
              ", fBinary=" + fBinary +
              ", fParity=" + fParity +
              ", fOutxCtsFlow=" + fOutxCtsFlow +
              ", fOutxDsrFlow=" + fOutxDsrFlow +
              ", fDtrControl=" + fDtrControl +
              ", fDsrSensitivity=" + fDsrSensitivity +
              ", fTXContinueOnXoff=" + fTXContinueOnXoff +
              ", fOutX=" + fOutX +
              ", fInX=" + fInX +
              ", fErrorChar=" + fErrorChar +
              ", fNull=" + fNull +
              ", fRtsControl=" + fRtsControl +
              ", fAbortOnError=" + fAbortOnError +
              ", fDummy2=" + fDummy2 +
              ", wReserved=" + wReserved +
              ", XonLim=" + XonLim +
              ", XoffLim=" + XoffLim +
              ", ByteSize=" + ByteSize +
              ", Parity=" + Parity +
              ", StopBits=" + StopBits +
              ", XonChar=" + XonChar +
              ", XoffChar=" + XoffChar +
              ", ErrorChar=" + ErrorChar +
              ", EofChar=" + EofChar +
              ", EvtChar=" + EvtChar +
              ", wReserved1=" + wReserved1 +
              '}';
    }

  }

  public static class COMMTIMEOUTS extends Structure {
    public WinDef.DWORD ReadIntervalTimeout;          /* Maximum time between read chars. */
    public WinDef.DWORD ReadTotalTimeoutMultiplier;   /* Multiplier of characters.        */
    public WinDef.DWORD ReadTotalTimeoutConstant;     /* Constant in milliseconds.        */
    public WinDef.DWORD WriteTotalTimeoutMultiplier;  /* Multiplier of characters.        */
    public WinDef.DWORD WriteTotalTimeoutConstant;    /* Constant in milliseconds.        */

    @Override
    public String toString() {
      return "COMMTIMEOUTS{" +
              "ReadIntervalTimeout=" + ReadIntervalTimeout +
              ", ReadTotalTimeoutMultiplier=" + ReadTotalTimeoutMultiplier +
              ", ReadTotalTimeoutConstant=" + ReadTotalTimeoutConstant +
              ", WriteTotalTimeoutMultiplier=" + WriteTotalTimeoutMultiplier +
              ", WriteTotalTimeoutConstant=" + WriteTotalTimeoutConstant +
              '}';
    }
  }

  boolean BuildCommDCB(String formatStr, DCB dcb);

  boolean SetCommState(WinNT.HANDLE hPort, DCB dcb);

  boolean GetCommTimeouts(WinNT.HANDLE hPort, COMMTIMEOUTS timeCommtimeouts);

  boolean SetCommTimeouts(WinNT.HANDLE hPort, COMMTIMEOUTS timeCommtimeouts);

  boolean ReadFile(WinNT.HANDLE handle, ByteBuffer bytes, int i, IntByReference intByReference, WinBase.OVERLAPPED overlapped);

  boolean GetOverlappedResult(WinNT.HANDLE handle, WinBase.OVERLAPPED overlapped, IntByReference intByReference, boolean wait);

}
