package dbg.electronics.robodrv.mcu;

public enum M32U4Reg implements CodifierAware {

    PORTB   (0x01),
    DDRB    (0x02),
    PORTC   (0x03),
    DDRC    (0x04),
    PORTD   (0x05),
    DDRD    (0x06),
    PORTE   (0x07),
    DDRE    (0x08),
    PORTF   (0x09),
    DDRF    (0x0A),

    TCCR1A  (0x0B),
    TCCR3A  (0x0C),
    TCCR1B  (0x0D),
    TCCR3B  (0x0E),
    OCR1AH  (0x0F),
    OCR1AL  (0x10),
    OCR1BH  (0x11),
    OCR1BL  (0x12);




    private M32U4Reg(int code) {
        this.code = (byte)code;
    }

    private byte code;

    public byte getCode() {
        return code;
    }

    @Override
    public int toCode() {
        return getCode();
    }

    public static M32U4Reg dirRegForPort(M32U4Reg portReg) {

        switch (portReg) {

            case PORTB:
                return M32U4Reg.DDRB;
            case PORTC:
                return M32U4Reg.DDRC;
            case PORTD:
                return M32U4Reg.DDRD;
            case PORTE:
                return M32U4Reg.DDRE;
            case PORTF:
                return M32U4Reg.DDRF;

            default:
                throw new IllegalArgumentException("No direction register for " + portReg);
        }
    }

}
