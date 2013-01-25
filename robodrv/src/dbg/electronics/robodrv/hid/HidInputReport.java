package dbg.electronics.robodrv.hid;


            /*
            struct input_event {
    struct timeval time;    // size depends on architecture
    __u16 type;
    __u16 code;
    __s32 value;
};
             */



public class HidInputReport {

    public static final int HID_REPORT_SIZE = 24;

    private long type;
    private long code;
    private long value;


    public HidInputReport(int[] rawReport) {

        if (rawReport.length != HID_REPORT_SIZE) {
            throw new IllegalArgumentException("HID raw report must have " + HID_REPORT_SIZE + " bytes, but provided " + rawReport.length);
        }

        value += rawReport[HID_REPORT_SIZE - 1] << 8*3;
        value += rawReport[HID_REPORT_SIZE - 2] << 8*2;
        value += rawReport[HID_REPORT_SIZE - 3] << 8;
        value += rawReport[HID_REPORT_SIZE - 4];

        code += rawReport[HID_REPORT_SIZE - 5] << 8;
        code += rawReport[HID_REPORT_SIZE - 6];

        type += rawReport[HID_REPORT_SIZE - 7] << 8;
        type += rawReport[HID_REPORT_SIZE - 8];


    }

    public long getType() {
        return type;
    }

    public long getCode() {
        return code;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "HidInputReport{" +
                " type=" + formatType() +
                ", code=" + formatCode() +
                ", value=" + formatValue() +
                '}';
    }

    public String formatValue() {
        return String.format("%08X", value);
    }

    public String formatCode() {
        return String.format("%04X", code);
    }

    public String formatType() {
        return String.format("%04X", type);
    }
}
