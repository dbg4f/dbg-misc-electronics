package dbg.electronics.robodrv.mcu;

public enum CommandCode implements CodifierAware {
    ECHO(0x01),
    READ_REG(0x14),
    WRITE_REG(0x15),
    ENABLE_ADC(0x17),
    SWITCH_WATCHDOG(0x18),
    RESET_WATCHDOG(0x19);

    CommandCode(int code) {
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
