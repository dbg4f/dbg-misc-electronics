package jprog.comm;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 15.12.2007
 * Time: 15:57:43
 * To change this template use File | Settings | File Templates.
 */
public class HEX {

    //public byte[] read(String data) {

    //}

    private static String HEXTABLE = "0123456789ABCDEF";

    public static String hexLine(byte[] rawData) {
        String line = "";
        for (int i = 0; i < rawData.length; i++) {
            line += HEX.formatByte(rawData[i]);
        }
        return line;
    }

    public static byte[] parseData(String rawData) {
        byte[] values = new byte[rawData.length()/2];
        for (int i = 0; i < rawData.length(); i+=2) {
             values[i/2] = parseHexByte(rawData.substring(i, i+2));
        }
        return values;
    }

    public static byte parseHexByte(String byteValue) {
        return Integer.valueOf(byteValue, 16).byteValue();
    }

    public static String formatByte(int value) {
        return formatByte((byte)value);
    }
    public static String formatByte(byte value) {

        if (value >= 0) {
            return String.valueOf(HEXTABLE.charAt((value & 0xF0) >> 4)) + String.valueOf(HEXTABLE.charAt(value % 16));
        }

        return Integer.toHexString((value & 0x7F) | 0x80).toUpperCase();
    }

    public static byte[] align32(byte[] array) {
        int bytesToAdd = 32 - (array.length % 32);
        byte[] aligned = new byte[array.length + bytesToAdd];
        Arrays.fill(aligned,  (byte) 0);
        System.arraycopy(array, 0, aligned, 0, array.length);
        return aligned;
    }

    public static String dumpHex(byte[] array) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            byte b = array[i];
            buf.append(HEX.formatByte(b)).append(" ");
            if (((i + 1) % 8) == 0) {
                buf.append(" ");
            }
            if (((i + 1) % 16) == 0) {
                buf.append("\n");
            }
        }
        return buf.toString();
    }

    public static String dumpHex(int[] array) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            int b = array[i];
            buf.append(HEX.formatByte(b)).append(" ");
            if (((i + 1) % 8) == 0) {
                buf.append(" ");
            }
            if (((i + 1) % 16) == 0) {
                buf.append("\n");
            }
        }
        return buf.toString();
    }


    public byte[] readFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String record = null;

        ArrayList list = new ArrayList();

        int len = 0;

        while((record = reader.readLine()) != null) {
            HexRecord rec = new HexRecord(record);
            if (rec.getType().equalsIgnoreCase("00")) {
                len += rec.getDataLength();
                list.add(rec);
            }
        }




        byte[] buf = new byte[len];
        int i = 0;
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            HexRecord hexRecord = (HexRecord) iterator.next();
            byte[] data = hexRecord.getData();
            for (int j = 0; j < data.length; j++) {
                buf[i++] = data[j];
            }
        }

        return buf;
    }


    private static void convertToBin() throws IOException {
        HEX h = new HEX();
        byte[] content = h.readFile("C:\\Projects\\misc\\mc\\serialprog\\serialprog.hex");
        FileOutputStream outFile = new FileOutputStream("out.hex.bin");
        outFile.write(content);
        outFile.close();
        System.out.println("len = " + content.length);
    }
}
