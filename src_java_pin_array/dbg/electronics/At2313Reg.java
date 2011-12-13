package dbg.electronics;


public enum At2313Reg {
    GIMSK (0x3B),
    EIFR  (0x3A),
    TIMSK (0x39),
    TIFR  (0x38),
    SPMCSR(0x37),
    OCR0A (0x36),
    MCUCR (0x35),
    MCUSR (0x34),
    TCCR0B(0x33),
    TCNT0 (0x32),
    OSCCAL(0x31),
    TCCR0A(0x30),
    TCCR1A(0x2F),
    TCCR1B(0x2E),
    TCNT1H(0x2D),
    TCNT1L(0x2C),
    OCR1AH(0x2B),
    OCR1AL(0x2A),
    OCR1BH(0x29),
    OCR1BL(0x28),
    CLKPR (0x26),
    ICR1H (0x25),
    ICR1L (0x24),
    GTCCR (0x23),
    TCCR1C(0x22),
    WDTCSR(0x21),
    PCMSK (0x20),
    EEAR  (0x1E),
    EEDR  (0x1D),
    EECR  (0x1C),
    PORTA (0x1B),
    DDRA  (0x1A),
    PINA  (0x19),
    PORTB (0x18),
    DDRB  (0x17),
    PINB  (0x16),
    GPIOR2(0x15),
    GPIOR1(0x14),
    GPIOR0(0x13),
    PORTD (0x12),
    DDRD  (0x11),
    PIND  (0x10),
    USIDR (0x0F),
    USISR (0x0E),
    USICR (0x0D),
    UDR   (0x0C),
    UCSRA (0x0B),
    UCSRB (0x0A),
    UBRRL (0x09),
    ACSR  (0x08),
    UCSRC (0x03),
    UBRRH (0x02),
    DIDR  (0x01);

    private At2313Reg(int code) {
        this.code = (byte)code;
    }

    private byte code;
}
