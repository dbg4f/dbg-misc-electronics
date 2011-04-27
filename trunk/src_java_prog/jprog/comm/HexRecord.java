package jprog.comm;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 16.12.2007
 * Time: 14:59:55
 * To change this template use File | Settings | File Templates.
 */
public class HexRecord {

    private int address;
    private String type;
    private int dataLength;
    private byte[] data;
    private byte checkSum;



    public HexRecord(int address, String type, byte[] data) {
        this.address = address;
        this.type = type;
        this.data = data;
        checkSum = 0;
        dataLength = data.length;
        String line = toHexLine();
        byte[] elements = HEX.parseData(line.substring(1));
        checkSum = calculateCheckSum(elements);
    }

    private byte calculateCheckSum(byte[] rawData) {
        byte sum = 0;
        for (int i = 0; i < rawData.length; i++) {
            sum += rawData[i];
        }
        return (byte)(1 + (~sum));
    }

    public HexRecord(String line) {
        dataLength = Integer.valueOf(line.substring(1, 3), 16).intValue();
        address = Integer.valueOf(line.substring(3, 7), 16).intValue();
        type = line.substring(7, 9);
        data = HEX.parseData(line.substring(9, 9 + dataLength*2));
        checkSum = Integer.valueOf(line.substring(9 + dataLength*2, 9 + dataLength*2 + 2), 16).byteValue();
    }


    private String align(String value, int count) {
        while(value.length() < count) {
            value = "0" + value;
        }
        return value;
    }

    public int getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public byte getCheckSum() {
        return checkSum;
    }

    public String toHexLine() {
        StringBuffer buf = new StringBuffer();
        buf.append(":").
                append(align(HEX.formatByte((byte)dataLength), 2)).
                append(align(Integer.toHexString(address).toUpperCase(), 4)).
                append(type);

        for (int i = 0; i < data.length; i++) {
            buf.append(HEX.formatByte(data[i]));
        }

        buf.append(HEX.formatByte(checkSum));

        return buf.toString();
    }


    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("{").append(" address=").append(address).append(" type=").append(type).append(" dataLength=").append(dataLength).append(" checkSum=").append(checkSum).append("}");
        return b.toString();
    }

}
