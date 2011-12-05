package dbg.electronics;

public class CrcTest {

    public static void main(String[] args) {
        System.out.println(String.format(" crc %02X", crc( 0x55,  0x02,  0x55,  0x33)));
        System.out.println(String.format(" crc %02X", crc( 0x55,  0x02,  0xAA,  0x55)));
    }

    private static int crc(int... content) {
        int crc =  0xFF;

        for (int b : content) {
            crc = crc(crc, b);
        }

        return crc;
    }

    private static int crc(int crc, int data) {
        int i;
        //
        crc ^= data;
        for (i = 0; i < 8; i++) {
            if ((crc & 0x80) == 0) {
                crc =  ((crc << 1) ^ 0xE5);
            } else {
                crc <<= 1;
            }

        }
        return crc;

    }


}
