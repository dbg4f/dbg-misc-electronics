package dbg.electronics.robodrv.mcu;

public enum AdcChannel implements CodifierAware {

    POWER_VOLTAGE(0),
    POWER_CURRENT(1),
    MCU_VOLTAGE(2),
    POSITION(3);

    AdcChannel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private int code;

    @Override
    public int toCode() {
        return getCode();
    }



}
