
#define F_CPU 7372800

#define BAUDRATE 19200

#define UART_CALC_BAUDRATE(baudRate) ((uint32_t)((F_CPU) + ((uint32_t)baudRate * 8UL)) / ((uint32_t)(baudRate) * 16UL) - 1)

#include <stdint.h>
#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/boot.h>
#include <avr/pgmspace.h>
#include <avr/eeprom.h>
#include <avr/interrupt.h>
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



#define CMD_L1_ECHO 		0x01
#define CMD_L1_READ_REG 	0x14
#define CMD_L1_WRITE_REG 	0x15

#define RESP_UNKNOWN_CMD	0xEE


// -----------------------------------------------------------------------------------------------------------------
static void sendchar(uint8_t data)
{
	while (!(UART_STATUS & (1<<UART_TXREADY)));
	UART_DATA = data;
}

// -----------------------------------------------------------------------------------------------------------------
static uint8_t recvchar(void)
{
	while (!(UART_STATUS & (1<<UART_RXREADY)));
	return UART_DATA;
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
static uint8_t send_with_crc(uint8_t crc, uint8_t data)
{
	sendchar(data);
	return crc_update(crc, data);
}

// -----------------------------------------------------------------------------------------------------------------

static void send_resp2(uint8_t byte1, uint8_t byte2)
{
	uint8_t crc = 0xFF;
	crc = send_with_crc(crc, 0x55);
	crc = send_with_crc(crc, 0x02);
	crc = send_with_crc(crc, byte1);
	crc = send_with_crc(crc, byte2);
	crc = send_with_crc(crc, crc);
}

// -----------------------------------------------------------------------------------------------------------------

static void serial_init(void)
{
	// Set baud rate
	UART_BAUD_HIGH = (UART_CALC_BAUDRATE(BAUDRATE)>>8) & 0xFF;
	UART_BAUD_LOW = (UART_CALC_BAUDRATE(BAUDRATE) & 0xFF);

	UART_CTRL = UART_CTRL_DATA;
	UART_CTRL2 = UART_CTRL2_DATA;
	
}

#define CASE_RD(REG_NAME, REG_SEL) case REG_SEL: res=REG_NAME ; break;

// -----------------------------------------------------------------------------------------------------------------
void read_reg(uint8_t reg)
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
			
	default : found = 0;
	}

	if (found)
	{
		send_resp2(0xDD, res);
	}
	else
	{
		send_resp2(0xAC, res);
	}


}

#define CASE_WR(REG_NAME, REG_SEL) case REG_SEL: REG_NAME=value; value=REG_NAME ; break;

// -----------------------------------------------------------------------------------------------------------------
void write_reg(uint8_t reg, uint8_t value)
{

	char found = 1;

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
				
	default : found = 0;
	}

	if (found)
	{
		send_resp2(0xDA, value);
	}
	else
	{
		send_resp2(0xDC, value);
	}

}



// -----------------------------------------------------------------------------------------------------------------
void exec_ext_command(uint8_t cmd, uint8_t param, uint8_t param2)
{

	switch (cmd) 
	{
		case CMD_L1_ECHO: 
			send_resp2(param, param);
			break;
			
		case CMD_L1_READ_REG:
			read_reg(param);
			break;
		
		case CMD_L1_WRITE_REG:
			write_reg(param, param2);
			
		default:
			send_resp2(RESP_UNKNOWN_CMD, param);
	}
/*
	if (cmd == CMD_L1_ECHO)
	{
		// echo
		send_resp2(param, param);
	}
	else if (cmd == CMD_L1_READ_REG)
	{
		read_reg(param);
	}
	else if (cmd == CMD_L1_WRITE_REG)
	{
		write_reg(param, param2);
	}	
	else
	{
		// unknown command
		send_resp2(RESP_UNKNOWN_CMD, param);
	}
*/

}



// -----------------------------------------------------------------------------------------------------------------

int main(void)
{
		
	uint8_t crc;
	uint8_t value;

	uint8_t length = 0;
	uint8_t command = 0;
	uint8_t parameter = 0;
	uint8_t parameter2 = 0;
	uint8_t ext_crc;	
	
	serial_init();
		
	send_resp2(0x55, 0x33);

	send_resp2(0xAA, 0x55);
	
	for (;;)
	{
		crc 	= 0xFF;

		command = 0;
		parameter = 0;
		parameter2 = 0;

		// marker
		value 	= recvchar();
		crc 	= crc_update(crc, value);

		if (value == 0x55)
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
				// parameter
				value 	= recvchar();
				crc 	= crc_update(crc, value);
				parameter = value;
			}

			if (length >= 3)
			{
				// parameter2
				value 	= recvchar();
				crc 	= crc_update(crc, value);
				parameter2 = value;
			}

			// crc from input
			ext_crc = recvchar();


			if (ext_crc == crc)
			{

				// forward validated input
				exec_ext_command(command, parameter, parameter2);

			}
			else
			{
				// crc not matched
				send_resp2(0x03, crc);
			}


		}
		else
		{
			// not in sync
			send_resp2(0x55, value);
		}

	}
	
	
	
}

