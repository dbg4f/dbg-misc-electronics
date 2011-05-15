package dbg.hid2pwm.mc;

public class BitUtils {

  public static byte fromBits(int bit7, int bit6, int bit5, int bit4, int bit3, int bit2, int bit1, int bit0) {
    int value = 0;

    value |= (bit7 != 0 ? 1 << 7 : 0);
    value |= (bit6 != 0 ? 1 << 6 : 0);
    value |= (bit5 != 0 ? 1 << 5 : 0);
    value |= (bit4 != 0 ? 1 << 4 : 0);
    value |= (bit3 != 0 ? 1 << 3 : 0);
    value |= (bit2 != 0 ? 1 << 2 : 0);
    value |= (bit1 != 0 ? 1 << 1 : 0);
    value |= (bit0 != 0 ? 1      : 0);

    return toByte(value);
  }

  public static byte toByte(int value) {
    return (byte)(value & 0xFF);
  }

  

  public static String toStr(byte value) {
    return String.format("%02X", value);
  }

  /*
  public static void main(String[] args) {
    System.out.println("toStr(fromBits(0, 1, 0, 1, 1, 0, 1, 0)) = " + toStr(fromBits(0, 0, 0, 0, 1, 0, 1, 0)));
  }
  */

}


