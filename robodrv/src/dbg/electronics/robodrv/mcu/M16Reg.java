package dbg.electronics.robodrv.mcu;

public enum M16Reg implements CodifierAware {

    TWBR    (0x00),
    TWSR    (0x01),
    TWAR    (0x02),
    TWDR    (0x03),
    ADCL    (0x04),
    ADCH    (0x05),
    ADCSRA  (0x06),
    ADMUX   (0x07),
    ACSR    (0x08),
    UBRRL   (0x09),
    UCSRB   (0x0A),
    UCSRA   (0x0B),
    UDR     (0x0C),
    SPCR    (0x0D),
    SPSR    (0x0E),
    SPDR    (0x0F),
    PIND    (0x10),
    DDRD    (0x11),
    PORTD   (0x12),
    PINC    (0x13),
    DDRC    (0x14),
    PORTC   (0x15),
    PINB    (0x16),
    DDRB    (0x17),
    PORTB   (0x18),
    PINA    (0x19),
    DDRA    (0x1A),
    PORTA   (0x1B),
    EECR﻿    (0x1C),
    EEDR﻿    (0x1D),
    EEARL﻿   (0x1E),
    EEARH﻿   (0x1F),
    UBRRH   (0x20),
    WDTCR   (0x21),
    ASSR    (0x22),
    OCR2    (0x23),
    TCNT2   (0x24),
    TCCR2   (0x25),
    ICR1L   (0x26),
    ICR1H   (0x27),
    OCR1BL  (0x28),
    OCR1BH  (0x29),
    OCR1AL  (0x2A),
    OCR1AH  (0x2B),
    TCNT1L  (0x2C),
    TCNT1H  (0x2D),
    TCCR1B  (0x2E),
    TCCR1A  (0x2F),
    SFIOR   (0x30),
    OSCCAL  (0x31),
    TCNT0   (0x32),
    TCCR0   (0x33),
    MCUCSR  (0x34),
    MCUCR   (0x35),
    TWCR    (0x36),
    SPMCR   (0x37),
    TIFR    (0x38),
    TIMSK   (0x39),
    GIFR    (0x3A),
    GICR    (0x3B),
    OCR0    (0x3C),
    SREG    (0x3F);

    private M16Reg(int code) {
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
}
