package dbg.electronics.robodrv.mcu;

public enum ReportMarker {

    COMMAND_RESPONSE(0x55),
    ADC_UPDATE(0x51),
    COUNTER_UPDATE(0x52);

    ReportMarker(int code) {
        this.code = (byte)code;
    }

    private byte code;

    public static boolean isValidMarker(byte value) {
        return fromByte(value) != null;
    }

    public static ReportMarker fromByte(byte value) {
        for (ReportMarker marker : ReportMarker.values()) {
            if (marker.getCode() == value) {
                return marker;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
