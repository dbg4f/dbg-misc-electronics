package dbg.electronics.robodrv.mcu;

public enum CommandCode implements CodifierAware {
    ECHO(0x01),
    READ_REG(0x14),
    WRITE_REG(0x15),
    ENABLE_ADC(0x17),
    SWITCH_WATCHDOG(0x18),
    RESET_WATCHDOG(0x19),
    SET_PORT_BITS(0x1B),
    CLEAR_PORT_BITS(0x1C),
    GET_TICK_COUNT 	(0x1D),
    SET_REG_TARGET 	(0x1E),
    DRV_SET_PWM		(0x1F),
    DRV_SET_DIR		(0x20),
    ADC_VALUE		(0x21);

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
