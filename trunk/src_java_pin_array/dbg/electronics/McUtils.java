package dbg.electronics;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 1/4/12
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class McUtils {

    public static int range(int src, int srcMin, int srcMax, int targetMin, int targetMax, boolean invert) {

        if (src > srcMax) {
           src = srcMax;
        }

        if (src < srcMin) {
            src = srcMin;
        }

        int srcLen = srcMax - srcMin;

        int targetLen = targetMax - targetMin;


        int target  = (src - srcMin) * targetLen / srcLen + targetMin;

        if (invert) {
            target = targetMax - target + targetMin;
        }

        return target;

    }

    static byte crc_calc(byte... content) {
        byte crc = (byte) 0xFF;

        for (byte b : content) {
            crc = crc_update(crc, b);
        }

        return crc;
    }

    static byte crc_update(byte crc, byte data) {

        int i;

        crc ^= data;

        for (i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0x00) {
                crc = (byte) ((crc << 1) ^ 0xE5);
            } else {
                crc <<= 1;
            }
        }

        return crc;
    }
}
