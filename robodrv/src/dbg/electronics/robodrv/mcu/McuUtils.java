package dbg.electronics.robodrv.mcu;

public class McuUtils {

    public static void crcPutToTail(byte[] content) {

        byte crc = (byte) 0xFF;

        for (int i=0; i<content.length - 1; i++) {
            crc = crcUpdate(crc, content[i]);
        }

        content[content.length - 1] = crc;

    }

    public static byte crcUpdate(byte crc, byte data) {

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

    public static String bytesToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte oneByte : bytes) {
            builder.append(String.format("0x%02X(%03d) ", oneByte, oneByte & 0xFF));
        }
        return builder.toString();
    }
}
