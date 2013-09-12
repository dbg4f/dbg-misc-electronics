package dbg.electronics.robodrv.util;

public class BinUtils {

    public static int asNumber(String value) {

        int number = 0;

        int rank = 0;
        for (int i = value.length() - 1; i>=0; i--) {
            char charAt = value.charAt(i);
            if (charAt == '1') {
                number += (1 << rank);
            }
            else if (charAt != '0') {
                throw new IllegalArgumentException("Bit line must contain 1 or 0, but found " + charAt + " at " + i);
            }
            rank++;

        }

        return number;

    }

    public static String asString(int value, int padding) {
        return String.format("%" + padding + "s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    public static int bitMask(int bit) {
        return 1 << bit;
    }


}
