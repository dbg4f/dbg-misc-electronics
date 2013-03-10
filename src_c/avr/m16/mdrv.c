
//TODO:
// Vsig (ADC3) periodic sending
// Sonar (timer) input and sending
// Steering PWM in internal mode
// Write register using bitmask

#define F_CPU 7372800

#define BAUDRATE 19200
//#define BAUDRATE 115200

#define UART_CALC_BAUDRATE(baudRate) ((uint32_t)((F_CPU) + ((uint32_t)baudRate * 8UL)) / ((uint32_t)(baudRate) * 16UL) - 1)

#include <stdint.h>
#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/boot.h>
#include <avr/pgmspace.h>
#include <avr/eeprom.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <util/delay.h>

#define UART_BAUD_HIGH	UBRRH
#define UART_BAUD_LOW	UBRRL
#define UART_STATUS	UCSRA
#define UART_TXREADY	UDRE
#define UART_RXREADY	RXC
#define UART_DOUBLE	U2X
#define UART_CTRL	UCSRB
#define UART_CTRL_DATA	((1<<TXEN) | (1<<RXEN))
#define UART_CTRL2	UCSRC
#define UART_CTRL2_DATA	((1<<URSEL) | (1<<UCSZ1) | (1<<UCSZ0))
#define UART_DATA	UDR



#define CMD_L1_ECHO 		    0x01
#define CMD_L1_READ_REG 	    0x14
#define CMD_L1_WRITE_REG 	    0x15
#define CMD_L1_READ_ADC0 	    0x16
#define CMD_L1_ENABLE_ADC 	    0x17
#define CMD_L1_SWITCH_WATCHDOG 	0x18
#define CMD_L1_RESET_WATCHDOG 	0x19
#define CMD_L1_WRITE_REG_MASK 	0x1A

#define RESP_UNKNOWN_CMD	0xEE
#define RESP_OK	            0xAA

#define RESP_ERROR_CRC_MISMATCH  0x03
#define RESP_ERROR_NOT_IN_SYNC   0x55

#define RESP_RESET_MARKER_A0   0x55
#define RESP_RESET_MARKER_A1   0x33
#define RESP_RESET_MARKER_B0   0xAA
#define RESP_RESET_MARKER_B1   0x55

#define START_PACKET_MARKER     0x55
#define START_ASYNC_MARKER_ADC  0x51
#define START_ASYNC_MARKER_CT   0x52


#define ADC_BUFFER_SIZE         16
#define ADC_BUFFER_DIV_SHIFT    4

#define ADC_CHANNELS_IN_USE     3

#define TX_MAX_BUFFER_SIZE      20

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_adc_buffer
{
    uint8_t valuations[ADC_BUFFER_SIZE];
    uint8_t index;
} ADC_BUFFER, *PADC_BUFFER;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_adc_context
{
    ADC_BUFFER adc_buffers[ADC_CHANNELS_IN_USE];
    uint8_t avg_values[ADC_CHANNELS_IN_USE];
    uint8_t adc_buf_index;
    uint8_t valuations_count;
} ADC_CONTEXT, *PADC_CONTEXT;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_counter_context
{
    uint8_t ct_value;
    uint8_t updated;
} CT_CONTEXT, *PCT_CONTEXT;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_response_context
{
    uint8_t response[TX_MAX_BUFFER_SIZE];
    uint8_t length;
    uint8_t updated;
} RESP_CONTEXT, *PRESP_CONTEXT;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_tx_buffer
{
    uint8_t content[TX_MAX_BUFFER_SIZE];
    uint8_t index;
    uint8_t size;
} TX_BUFFER, *PTX_BUFFER;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_tx_context
{
    uint8_t enabled;
    TX_BUFFER adc_tx_buf;
    TX_BUFFER ct_tx_buf;
    TX_BUFFER resp_tx_buf;
    PTX_BUFFER current_buffer;
} TX_CONTEXT, *PTX_CONTEXT;

static ADC_CONTEXT adc_context;
static TX_CONTEXT tx_context;
static CT_CONTEXT ct_context;
static RESP_CONTEXT resp_context;

static uint8_t adc0_valueL;
static uint8_t adc0_valueH;

// -----------------------------------------------------------------------------------------------------------------
static uint8_t adc_buf_avg(PADC_BUFFER p_adc_buffer)
{
    uint16_t avg = 0;
    uint8_t i = 0;
    for (i = 0; i<ADC_BUFFER_SIZE; i++)
    {
        avg += p_adc_buffer->valuations[i];
    }
    return (uint8_t)(avg >> ADC_BUFFER_DIV_SHIFT);
}

// -----------------------------------------------------------------------------------------------------------------
static void adc_buf_init(PADC_BUFFER p_adc_buffer)
{
    uint8_t i = 0;
    for (i = 0; i<ADC_BUFFER_SIZE; i++)
    {
        p_adc_buffer->valuations[i] = 0;
    }
    p_adc_buffer->index = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void adc_buf_add(PADC_BUFFER p_adc_buffer, uint8_t value)
{
    uint8_t i = p_adc_buffer->index + 1;
    if (i == ADC_BUFFER_SIZE)
    {
        i = 0;
    }
    p_adc_buffer->valuations[i] = value;
    p_adc_buffer->index = i;
}


// -----------------------------------------------------------------------------------------------------------------
static void adc_ctx_init(PADC_CONTEXT p_adc_context)
{
    uint8_t i = 0;
    for (i = 0; i<ADC_CHANNELS_IN_USE; i++)
    {
        adc_buf_init(&p_adc_context->adc_buffers[i]);
        p_adc_context->avg_values[i] = 0;
    }

    p_adc_context->adc_buf_index = 0;
    p_adc_context->valuations_count = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void adc_run_next(uint8_t channel)
{

    // enable ADC, int, start, once, div/8
    ADCSRA = (1<<ADEN)|(1<<ADIE)|(1<<ADSC)|(0<<ADATE)|(3<<ADPS0);

    //REFS  -- 0b[01]000101 use AVCC ref
    //ADLAR -- 0b01[1]00101 left alignment
    //MUX   -- 0b0100[0101] channel 5.
    ADMUX = (0b01100000 | (channel & 0b00000111));

}


// -----------------------------------------------------------------------------------------------------------------
static void adc_ctx_new_value(PADC_CONTEXT p_adc_context, uint8_t value)
{
    p_adc_context->valuations_count++;

    uint8_t buf_index = p_adc_context->adc_buf_index;

    adc_buf_add(&p_adc_context->adc_buffers[buf_index], value);

    if (p_adc_context->valuations_count == ADC_BUFFER_SIZE)
    {

        p_adc_context->avg_values[buf_index] = adc_buf_avg(&p_adc_context->adc_buffers[buf_index]);

        buf_index++;

        if (buf_index == ADC_CHANNELS_IN_USE)
        {
            buf_index = 0;
        }

        p_adc_context->adc_buf_index = buf_index;

        p_adc_context->valuations_count = 0;

    }

    adc_run_next(p_adc_context->adc_buf_index);

}

// -----------------------------------------------------------------------------------------------------------------
static void sendchar_immediately(uint8_t data)
{
    UART_DATA = data;
}

// -----------------------------------------------------------------------------------------------------------------
static uint8_t crc_update(uint8_t crc, uint8_t data)
{
    uint8_t i;
    //
    crc ^= data;
    for(i = 0; i < 8; i++)
    {
        if(crc & 0x80)
            crc = (crc << 1) ^ 0xE5;
        else
            crc <<= 1;
    }
    return crc;
}

// -----------------------------------------------------------------------------------------------------------------
static void tx_adc_snapshot(PADC_CONTEXT p_adc_context, PTX_CONTEXT p_tx_context)
{
    PTX_BUFFER p_tx_buf = &p_tx_context->adc_tx_buf;
    uint8_t crc = 0xFF;

    p_tx_buf->index = 0;
    p_tx_buf->size = ADC_CHANNELS_IN_USE + 2; // start marker + avg bytes + crc
    p_tx_buf->content[0] = START_ASYNC_MARKER_ADC;

    crc = crc_update(crc, p_tx_buf->content[0]);

    uint8_t i = 0;
    for (i=0; i<ADC_CHANNELS_IN_USE; i++)
    {
        p_tx_buf->content[i+1] = p_adc_context->avg_values[i];
        crc = crc_update(crc, p_tx_buf->content[i+1]);
    }

    p_tx_buf->content[i+1] = crc;

}

// -----------------------------------------------------------------------------------------------------------------
static void tx_ct_snapshot(PCT_CONTEXT p_ct_context, PTX_CONTEXT p_tx_context)
{
    PTX_BUFFER p_tx_buf = &p_tx_context->ct_tx_buf;
    uint8_t crc = 0xFF;

    p_tx_buf->index = 0;
    p_tx_buf->size = 3; // start marker + ct value + crc

    p_tx_buf->content[0] = START_ASYNC_MARKER_CT;
    crc = crc_update(crc, p_tx_buf->content[0]);

    p_tx_buf->content[1] = p_ct_context->ct_value;
    crc = crc_update(crc, p_tx_buf->content[1]);

    p_tx_buf->content[2] = crc;

    p_ct_context->updated = 0;

}


// -----------------------------------------------------------------------------------------------------------------
static void tx_resp_snapshot(PRESP_CONTEXT p_resp_context, PTX_CONTEXT p_tx_context)
{
    PTX_BUFFER p_tx_buf = &p_tx_context->resp_tx_buf;
    uint8_t crc = 0xFF;

    p_tx_buf->index = 0;
    p_tx_buf->size = p_resp_context->length + 2; // start marker + len + crc
    p_tx_buf->content[0] = START_PACKET_MARKER; // the same marker for synch transfer

    crc = crc_update(crc, p_tx_buf->content[0]);

    uint8_t i = 0;
    for (i=0; i<p_resp_context->length; i++)
    {
        p_tx_buf->content[i+1] = p_resp_context->response[i];
        crc = crc_update(crc, p_tx_buf->content[i+1]);
    }

    p_tx_buf->content[i+1] = crc;

    p_resp_context->updated = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void tx_ctx_send_next(PTX_CONTEXT p_tx_context)
{

    PTX_BUFFER p_tx_buf = p_tx_context->current_buffer;
    uint8_t next_byte = p_tx_buf->content[p_tx_buf->index++];

    if (p_tx_buf->index == p_tx_buf->size)
    {

        // switch once from ADC to CT if new value appears on CT
        if ((p_tx_buf == &p_tx_context->adc_tx_buf) && ct_context.updated)
        {
            tx_ct_snapshot(&ct_context, p_tx_context);
            p_tx_context->current_buffer = &p_tx_context->ct_tx_buf;
        }
        else if (resp_context.updated)
        {
            tx_resp_snapshot(&resp_context, p_tx_context);
            p_tx_context->current_buffer = &p_tx_context->resp_tx_buf;
        }
        else
        {
            tx_adc_snapshot(&adc_context, p_tx_context);
            p_tx_context->current_buffer = &p_tx_context->adc_tx_buf;
        }

    }

    sendchar_immediately(next_byte);
}

// -----------------------------------------------------------------------------------------------------------------
static void ct_ctx_increase(PCT_CONTEXT p_ct_context)
{
    p_ct_context->ct_value++;
    p_ct_context->updated = 1;
}

static void resp_ctx_add(PRESP_CONTEXT p_resp_context, uint8_t byte1, uint8_t byte2, uint8_t sequence)
{
    p_resp_context->response[0] = 3; // length of resp bytes
    p_resp_context->response[1] = sequence;
    p_resp_context->response[2] = byte1;
    p_resp_context->response[3] = byte2;
    p_resp_context->length = 4; // buf length
    p_resp_context->updated = 1;
}

// -----------------------------------------------------------------------------------------------------------------
static void tx_buf_init(PTX_BUFFER p_tx_buffer)
{
    p_tx_buffer->index = 0;
    p_tx_buffer->size = 0;
    uint8_t i = 0;
    for (i=0; i<TX_MAX_BUFFER_SIZE; i++)
    {
        p_tx_buffer->content[i] = 0;
    }
}

// -----------------------------------------------------------------------------------------------------------------
static void tx_ctx_init(PTX_CONTEXT p_tx_context)
{
    p_tx_context->enabled = 1;
    tx_buf_init(&p_tx_context->adc_tx_buf);
    tx_buf_init(&p_tx_context->ct_tx_buf);
    tx_buf_init(&p_tx_context->resp_tx_buf);
    p_tx_context->current_buffer = &p_tx_context->adc_tx_buf; // start with ADC buffer
    tx_adc_snapshot(&adc_context, p_tx_context);
    tx_ctx_send_next(p_tx_context); // start sending, next bytes are sent in ISR
}

// -----------------------------------------------------------------------------------------------------------------
static void ct_ctx_init(PCT_CONTEXT p_ct_context)
{
    p_ct_context->ct_value = 0;
    p_ct_context->updated = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void resp_ctx_init(PRESP_CONTEXT p_resp_context)
{
    p_resp_context->length = 0;
    uint8_t i = 0;
    for (i=0; i<TX_MAX_BUFFER_SIZE; i++)
    {
        p_resp_context->response[i] = 0;
    }
    p_resp_context->updated = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void sendchar(uint8_t data)
{
    while (!(UART_STATUS & (1<<UART_TXREADY)));
   sendchar_immediately(data);
}

// -----------------------------------------------------------------------------------------------------------------
static uint8_t is_rx_char_ready(void)
{
    return (UART_STATUS & (1<<UART_RXREADY));
}

// -----------------------------------------------------------------------------------------------------------------
static uint8_t recvchar(void)
{
    while (!is_rx_char_ready());
    return UART_DATA;
}

// -----------------------------------------------------------------------------------------------------------------
static uint8_t send_with_crc(uint8_t crc, uint8_t data)
{
    sendchar(data);
    return crc_update(crc, data);
}

// -----------------------------------------------------------------------------------------------------------------

static void tx_send_resp2_immediately(uint8_t byte1, uint8_t byte2, uint8_t sequence)
{
    uint8_t crc = 0xFF;
    crc = send_with_crc(crc, START_PACKET_MARKER);
    crc = send_with_crc(crc, 0x03);
    crc = send_with_crc(crc, sequence);
    crc = send_with_crc(crc, byte1);
    crc = send_with_crc(crc, byte2);
    crc = send_with_crc(crc, crc);
}

// -----------------------------------------------------------------------------------------------------------------


static void send_resp2(uint8_t byte1, uint8_t byte2, uint8_t sequence)
{
    if (tx_context.enabled)
    {
        resp_ctx_add(&resp_context, byte1, byte2, sequence);
    }
    else
    {
        tx_send_resp2_immediately(byte1, byte2, sequence);
    }
}
// -----------------------------------------------------------------------------------------------------------------
/*
static void send_resp3(uint8_t byte1, uint8_t byte2, uint8_t byte3)
{
    uint8_t crc = 0xFF;
    crc = send_with_crc(crc, START_PACKET_MARKER);
    crc = send_with_crc(crc, 0x03);
    crc = send_with_crc(crc, byte1);
    crc = send_with_crc(crc, byte2);
    crc = send_with_crc(crc, byte3);
    crc = send_with_crc(crc, crc);
}
*/
// -----------------------------------------------------------------------------------------------------------------

static void serial_init(void)
{
    // Set baud rate
    UART_BAUD_HIGH = (UART_CALC_BAUDRATE(BAUDRATE)>>8) & 0xFF;
    UART_BAUD_LOW = (UART_CALC_BAUDRATE(BAUDRATE) & 0xFF);

    UART_CTRL = UART_CTRL_DATA;
    UART_CTRL2 = UART_CTRL2_DATA;

}


// -----------------------------------------------------------------------------------------------------------------

static void ct_init(void)
{
    ct_ctx_init(&ct_context);

    MCUCR |= (_BV(ISC00)); // any level change at int0 generates int
    GICR |= _BV(INT0);

}

// -----------------------------------------------------------------------------------------------------------------

static void resp_init(void)
{
    resp_ctx_init(&resp_context);
}

// -----------------------------------------------------------------------------------------------------------------

static void adc_init(void)
{

    adc_ctx_init(&adc_context);

    adc_run_next(0);

}

// -----------------------------------------------------------------------------------------------------------------

static void tx_init(void)
{

    tx_ctx_init(&tx_context);

    UCSRB = (1<<RXEN)|(1<<TXEN)|(0<<RXCIE)|(1<<TXCIE)|(0<<UDRIE);

}

// -----------------------------------------------------------------------------------------------------------------
static void switch_watchdog(uint8_t status)
{
    if(status)
    {
        wdt_enable(WDTO_250MS);
    }
    else
    {
        wdt_disable();
    }
}

// -----------------------------------------------------------------------------------------------------------------
static void reset_watchdog(void)
{
    wdt_reset();
}

// -----------------------------------------------------------------------------------------------------------------

static void read_adc0(void)
{

    send_resp2(adc0_valueH, adc0_valueL, 0);

}


#define CASE_RD(REG_NAME, REG_SEL) case REG_SEL: res=REG_NAME ; break;

// -----------------------------------------------------------------------------------------------------------------
void read_reg(uint8_t reg, uint8_t sequence)
{
    uint8_t res = 0x00;

    char found = 1;

    switch (reg)
    {

        CASE_RD(TWBR    ,0x00)
        CASE_RD(TWSR    ,0x01)
        CASE_RD(TWAR    ,0x02)
        CASE_RD(TWDR    ,0x03)
        CASE_RD(ADCL    ,0x04)
        CASE_RD(ADCH    ,0x05)
        CASE_RD(ADCSRA  ,0x06)
        CASE_RD(ADMUX   ,0x07)
        CASE_RD(ACSR    ,0x08)
        CASE_RD(UBRRL   ,0x09)
        CASE_RD(UCSRB   ,0x0A)
        CASE_RD(UCSRA   ,0x0B)
        CASE_RD(UDR     ,0x0C)
        CASE_RD(SPCR    ,0x0D)
        CASE_RD(SPSR    ,0x0E)
        CASE_RD(SPDR    ,0x0F)
        CASE_RD(PIND    ,0x10)
        CASE_RD(DDRD    ,0x11)
        CASE_RD(PORTD   ,0x12)
        CASE_RD(PINC    ,0x13)
        CASE_RD(DDRC    ,0x14)
        CASE_RD(PORTC   ,0x15)
        CASE_RD(PINB    ,0x16)
        CASE_RD(DDRB    ,0x17)
        CASE_RD(PORTB   ,0x18)
        CASE_RD(PINA    ,0x19)
        CASE_RD(DDRA    ,0x1A)
        CASE_RD(PORTA   ,0x1B)
        CASE_RD(EECR	,0x1C)
        CASE_RD(EEDR	,0x1D)
        CASE_RD(EEARL	,0x1E)
        CASE_RD(EEARH	,0x1F)
        //CASE_RD(UCSRC   ,0x20)
        CASE_RD(UBRRH   ,0x20)
        CASE_RD(WDTCR   ,0x21)
        CASE_RD(ASSR    ,0x22)
        CASE_RD(OCR2    ,0x23)
        CASE_RD(TCNT2   ,0x24)
        CASE_RD(TCCR2   ,0x25)
        CASE_RD(ICR1L   ,0x26)
        CASE_RD(ICR1H   ,0x27)
        CASE_RD(OCR1BL  ,0x28)
        CASE_RD(OCR1BH  ,0x29)
        CASE_RD(OCR1AL  ,0x2A)
        CASE_RD(OCR1AH  ,0x2B)
        CASE_RD(TCNT1L  ,0x2C)
        CASE_RD(TCNT1H  ,0x2D)
        CASE_RD(TCCR1B  ,0x2E)
        CASE_RD(TCCR1A  ,0x2F)
        CASE_RD(SFIOR   ,0x30)
        CASE_RD(OSCCAL  ,0x31)
        //CASE_RD(OCDR    ,0x31)
        CASE_RD(TCNT0   ,0x32)
        CASE_RD(TCCR0   ,0x33)
        CASE_RD(MCUCSR  ,0x34)
        CASE_RD(MCUCR   ,0x35)
        CASE_RD(TWCR    ,0x36)
        CASE_RD(SPMCR   ,0x37)
        CASE_RD(TIFR    ,0x38)
        CASE_RD(TIMSK   ,0x39)
        CASE_RD(GIFR    ,0x3A)
        CASE_RD(GICR    ,0x3B)
        CASE_RD(OCR0    ,0x3C)
        CASE_RD(SREG    ,0x3F)

    default :
        found = 0;
    }

    if (found)
    {
        send_resp2(0xDD, res, sequence);
    }
    else
    {
        send_resp2(0xAC, res, sequence);
    }


}

#define CASE_WR(REG_NAME, REG_SEL) case REG_SEL: REG_NAME = value; break;

// -----------------------------------------------------------------------------------------------------------------
void write_reg(uint8_t reg, uint8_t value, uint8_t mask, uint8_t sequence)
{

    char found = 1;

    //uint8_t tmp;

    switch (reg)
    {

        CASE_WR(TWBR    ,0x00)
        CASE_WR(TWSR    ,0x01)
        CASE_WR(TWAR    ,0x02)
        CASE_WR(TWDR    ,0x03)
        CASE_WR(ADCL    ,0x04)
        CASE_WR(ADCH    ,0x05)
        CASE_WR(ADCSRA  ,0x06)
        CASE_WR(ADMUX   ,0x07)
        CASE_WR(ACSR    ,0x08)
        CASE_WR(UBRRL   ,0x09)
        CASE_WR(UCSRB   ,0x0A)
        CASE_WR(UCSRA   ,0x0B)
        CASE_WR(UDR     ,0x0C)
        CASE_WR(SPCR    ,0x0D)
        CASE_WR(SPSR    ,0x0E)
        CASE_WR(SPDR    ,0x0F)
        CASE_WR(PIND    ,0x10)
        CASE_WR(DDRD    ,0x11)
        CASE_WR(PORTD   ,0x12)
        CASE_WR(PINC    ,0x13)
        CASE_WR(DDRC    ,0x14)
        CASE_WR(PORTC   ,0x15)
        CASE_WR(PINB    ,0x16)
        CASE_WR(DDRB    ,0x17)
        CASE_WR(PORTB   ,0x18)
        CASE_WR(PINA    ,0x19)
        CASE_WR(DDRA    ,0x1A)
        CASE_WR(PORTA   ,0x1B)
        CASE_WR(EECR	,0x1C)
        CASE_WR(EEDR	,0x1D)
        CASE_WR(EEARL	,0x1E)
        CASE_WR(EEARH	,0x1F)
        //CASE_WR(UCSRC   ,0x20)
        CASE_WR(UBRRH   ,0x20)
        CASE_WR(WDTCR   ,0x21)
        CASE_WR(ASSR    ,0x22)
        CASE_WR(OCR2    ,0x23)
        CASE_WR(TCNT2   ,0x24)
        CASE_WR(TCCR2   ,0x25)
        CASE_WR(ICR1L   ,0x26)
        CASE_WR(ICR1H   ,0x27)
        CASE_WR(OCR1BL  ,0x28)
        CASE_WR(OCR1BH  ,0x29)
        CASE_WR(OCR1AL  ,0x2A)
        CASE_WR(OCR1AH  ,0x2B)
        CASE_WR(TCNT1L  ,0x2C)
        CASE_WR(TCNT1H  ,0x2D)
        CASE_WR(TCCR1B  ,0x2E)
        CASE_WR(TCCR1A  ,0x2F)
        CASE_WR(SFIOR   ,0x30)
        CASE_WR(OSCCAL  ,0x31)
        //CASE_WR(OCDR    ,0x31)
        CASE_WR(TCNT0   ,0x32)
        CASE_WR(TCCR0   ,0x33)
        CASE_WR(MCUCSR  ,0x34)
        CASE_WR(MCUCR   ,0x35)
        CASE_WR(TWCR    ,0x36)
        CASE_WR(SPMCR   ,0x37)
        CASE_WR(TIFR    ,0x38)
        CASE_WR(TIMSK   ,0x39)
        CASE_WR(GIFR    ,0x3A)
        CASE_WR(GICR    ,0x3B)
        CASE_WR(OCR0    ,0x3C)
        CASE_WR(SREG    ,0x3F)

    default :
        found = 0;
    }

    if (found)
    {
        send_resp2(0xDA, value, sequence);
    }
    else
    {
        send_resp2(0xDC, value, sequence);
    }

}



// -----------------------------------------------------------------------------------------------------------------
void exec_ext_command(uint8_t cmd, uint8_t param, uint8_t param2, uint8_t param3, uint8_t sequence)
{

    switch (cmd)
    {
    case CMD_L1_ECHO:
        send_resp2(param, param, sequence);
        break;

    case CMD_L1_READ_REG:
        read_reg(param, sequence);
        break;

    case CMD_L1_WRITE_REG:
        write_reg(param, param2, 0xFF, sequence);
        break;

    case CMD_L1_WRITE_REG_MASK:
        write_reg(param, param2, param3, sequence);
        break;

    case CMD_L1_READ_ADC0:
        read_adc0();
        break;

    case CMD_L1_ENABLE_ADC:
        adc_init();
        //send_resp2(RESP_OK, 0x00);
        sei();
        tx_init();
        ct_init();
        resp_init();
        break;

    case CMD_L1_SWITCH_WATCHDOG:
        switch_watchdog(param);
        send_resp2(RESP_OK, 0x00, sequence);
        break;

    case CMD_L1_RESET_WATCHDOG:
        reset_watchdog();
        send_resp2(RESP_OK, 0x00, sequence);
        break;

    default:
        send_resp2(RESP_UNKNOWN_CMD, param, sequence);
    }

}

// -----------------------------------------------------------------------------------------------------------------
ISR(INT0_vect)
{
    ct_ctx_increase(&ct_context);
}

// -----------------------------------------------------------------------------------------------------------------
ISR(INT1_vect)
{


}

// -----------------------------------------------------------------------------------------------------------------
ISR(INT2_vect)
{


}

// -----------------------------------------------------------------------------------------------------------------
ISR(USART_TXC_vect)
{
    tx_ctx_send_next(&tx_context);
}

// -----------------------------------------------------------------------------------------------------------------
ISR(ADC_vect)
{
    // NB! sequence and count of ADC readings is important
    adc0_valueL = ADCL;
    adc0_valueH = ADCH;

    adc_ctx_new_value(&adc_context, adc0_valueH);

}



// -----------------------------------------------------------------------------------------------------------------

int main(void)
{

    uint8_t crc;
    uint8_t value;

    uint8_t length = 0;
    uint8_t sequence = 0;
    uint8_t command = 0;
    uint8_t parameter = 0;
    uint8_t parameter2 = 0;
    uint8_t parameter3 = 0;
    uint8_t ext_crc;

    serial_init();

    send_resp2(RESP_RESET_MARKER_A0, RESP_RESET_MARKER_A1, 0);

    send_resp2(RESP_RESET_MARKER_B0, RESP_RESET_MARKER_B1, 0);

    for (;;)
    {
        crc 	= 0xFF;


        sequence = 0;
        command = 0;
        parameter = 0;
        parameter2 = 0;
        parameter3 = 0;

        // marker
        value 	= recvchar();
        crc 	= crc_update(crc, value);

        if (value == 'S')
        {
            sendchar('R');
            sendchar('D');
            sendchar('R');
            sendchar('V');
            sendchar('0');
            sendchar('1');
        }
        else if (value == START_PACKET_MARKER)
        {
            // length
            value 	= recvchar();
            crc 	= crc_update(crc, value);

            length = value;

            if (length >= 1)
            {
                // command
                value 	= recvchar();
                crc 	= crc_update(crc, value);
                command = value;

            }

            if (length >= 2)
            {
                // command
                value 	= recvchar();
                crc 	= crc_update(crc, value);
                sequence = value;

            }

            if (length >= 3)
            {
                // parameter
                value 	= recvchar();
                crc 	= crc_update(crc, value);
                parameter = value;
            }

            if (length >= 4)
            {
                // parameter2
                value 	= recvchar();
                crc 	= crc_update(crc, value);
                parameter2 = value;
            }

            if (length >= 5)
            {
                // parameter2
                value 	= recvchar();
                crc 	= crc_update(crc, value);
                parameter3 = value;
            }

            // crc from input
            ext_crc = recvchar();


            if (ext_crc == crc)
            {

                // forward validated input
                exec_ext_command(command, parameter, parameter2, parameter3, sequence);

            }
            else
            {
                // crc not matched
                send_resp2(RESP_ERROR_CRC_MISMATCH, crc, sequence);
            }


        }
        else
        {
            // not in sync
            send_resp2(RESP_ERROR_NOT_IN_SYNC, value, sequence);
        }

    }



}

