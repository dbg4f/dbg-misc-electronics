#include <avr/io.h>
#include <avr/interrupt.h>


void 			UART0_init_noint (unsigned char baudrate);
unsigned char 	UART0_wait_read (void);
void 			UART0_wait_send (unsigned char data);
unsigned char 	crc_update(unsigned char crc, unsigned char data);

unsigned char 	send_with_crc(unsigned char crc, unsigned char data);
void 			send_resp2(unsigned char byte1, unsigned char byte2);
void exec_ext_command(unsigned char cmd, unsigned char param, unsigned char param2);
void read_reg(unsigned char reg);
void write_reg(unsigned char reg, unsigned char value);


static unsigned char 	INTC_on;
static unsigned char 	INTC_target;
static unsigned char 	INTC_counter;
static unsigned char 	PWM1_stop = 0;

void 			USART0_Init( unsigned int baudrate );
void 			PWM0_set(unsigned char value);
void 			PWM1_set(unsigned char value);
void 			DIR_set(unsigned char value);

unsigned char	PWM0_get();
unsigned char	PWM1_get();
unsigned char	DIR_get();
unsigned char	AIN_get();

void 			DIR_setup();
void 			PWM_setup();
void 			INTC_init();
void 			INTC_cancel();
void 			INTC_start(unsigned char target);
void 			AIN_init();
unsigned char 	merge(unsigned char vH, unsigned char vL);






int main()
{
	unsigned char crc;
	unsigned char value;

	unsigned char length = 0;
	unsigned char command = 0;
	unsigned char parameter = 0;
	unsigned char parameter2 = 0;
	unsigned char ext_crc;


	UART0_init_noint(12); // 4M, U2X=0, 19200, 0.2%

	PWM_setup();

	DIR_setup();

	INTC_init();

	AIN_init();

	sei();           /* Enable interrupts => enable UART interrupts */

	send_resp2(0x55, 0x33);

	send_resp2(0xAA, 0x55);


	for (;;)
	{
		crc 	= 0xFF;

		command = 0;
		parameter = 0;
		parameter2 = 0;

		// marker
		value 	= UART0_wait_read();
		crc 	= crc_update(crc, value);

		if (value == 0x55)
		{
			// length
			value 	= UART0_wait_read();
			crc 	= crc_update(crc, value);

			length = value;

			if (length >= 1)
			{
				// command
				value 	= UART0_wait_read();
				crc 	= crc_update(crc, value);
				command = value;

			}

			if (length >= 2)
			{
				// parameter
				value 	= UART0_wait_read();
				crc 	= crc_update(crc, value);
				parameter = value;
			}

			if (length >= 3)
			{
				// parameter2
				value 	= UART0_wait_read();
				crc 	= crc_update(crc, value);
				parameter2 = value;
			}

			// crc from input
			ext_crc = UART0_wait_read();


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


	return 0;
}


void exec_ext_command(unsigned char cmd, unsigned char param, unsigned char param2)
{
	if (cmd == 0x01)
	{
		// echo
		send_resp2(param, param);
	}
	else if (cmd == 0x04)
	{
		PWM0_set(param);
		send_resp2(0x14, param);
	}
	else if (cmd == 0x05)
	{
		PWM1_set(param);
		send_resp2(0x15, param);
	}
	// get interrupts counter
	else if (cmd == 0x10)
	{
		send_resp2(0x15, INTC_counter);
	}
	// schedule PWM1 off
	else if (cmd == 0x11)
	{
		INTC_start(param);
		send_resp2(0x11, INTC_counter);
	}
	// cancel schedule PWM1
	else if (cmd == 0x12)
	{
		INTC_cancel();
		send_resp2(0x12, INTC_counter);
	}
	// get schedule status
	else if (cmd == 0x13)
	{
		send_resp2(0x11, INTC_on);
	}
	else if (cmd == 0x14)
	{
		read_reg(param);
	}
	else if (cmd == 0x15)
	{
		write_reg(param, param2);
	}
	else if (cmd == 0x16)
	{
		// set final PWM value
		PWM1_stop = param;
	}
	else
	{
		// unknown command
		send_resp2(0xEE, param);
	}


}


void read_reg(unsigned char reg)
{
	unsigned char res = 0x00;

	char found = 1;

	switch (reg)
	{
case     0x3B:	res=GIMSK ; break;
case     0x3A:	res=EIFR  ; break;
case     0x39:	res=TIMSK ; break;
case     0x38:	res=TIFR  ; break;
case     0x37:	res=SPMCSR; break;
case     0x36:	res=OCR0A ; break;
case     0x35:	res=MCUCR ; break;
case     0x34:	res=MCUSR ; break;
case     0x33:	res=TCCR0B; break;
case     0x32:	res=TCNT0 ; break;
case     0x31:	res=OSCCAL; break;
case     0x30:	res=TCCR0A; break;
case     0x2F:	res=TCCR1A; break;
case     0x2E:	res=TCCR1B; break;
case     0x2D:	res=TCNT1H; break;
case     0x2C:	res=TCNT1L; break;
case     0x2B:	res=OCR1AH; break;
case     0x2A:	res=OCR1AL; break;
case     0x29:	res=OCR1BH; break;
case     0x28:	res=OCR1BL; break;
case     0x26:	res=CLKPR ; break;
case     0x25:	res=ICR1H ; break;
case     0x24:	res=ICR1L ; break;
case     0x23:	res=GTCCR ; break;
case     0x22:	res=TCCR1C; break;
case     0x21:	res=WDTCSR; break;
case     0x20:	res=PCMSK ; break;
case     0x1E:	res=EEAR  ; break;
case     0x1D:	res=EEDR  ; break;
case     0x1C:	res=EECR  ; break;
case     0x1B:	res=PORTA ; break;
case     0x1A:	res=DDRA  ; break;
case     0x19:	res=PINA  ; break;
case     0x18:	res=PORTB ; break;
case     0x17:	res=DDRB  ; break;
case     0x16:	res=PINB  ; break;
case     0x15:	res=GPIOR2; break;
case     0x14:	res=GPIOR1; break;
case     0x13:	res=GPIOR0; break;
case     0x12:	res=PORTD ; break;
case     0x11:	res=DDRD  ; break;
case     0x10:	res=PIND  ; break;
case     0x0F:	res=USIDR ; break;
case     0x0E:	res=USISR ; break;
case     0x0D:	res=USICR ; break;
case     0x0C:	res=UDR   ; break;
case     0x0B:	res=UCSRA ; break;
case     0x0A:	res=UCSRB ; break;
case     0x09:	res=UBRRL ; break;
case     0x08:	res=ACSR  ; break;
case     0x03:	res=UCSRC ; break;
case     0x02:	res=UBRRH ; break;
case     0x01:	res=DIDR  ; break;
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


void write_reg(unsigned char reg, unsigned char value)
{

	char found = 1;

	switch (reg)
	{
case     0x3B:	GIMSK =value; value=GIMSK ;break;
case     0x3A:	EIFR  =value; value=EIFR  ;break;
case     0x39:	TIMSK =value; value=TIMSK ;break;
case     0x38:	TIFR  =value; value=TIFR  ;break;
case     0x37:	SPMCSR=value; value=SPMCSR;break;
case     0x36:	OCR0A =value; value=OCR0A ;break;
case     0x35:	MCUCR =value; value=MCUCR ;break;
case     0x34:	MCUSR =value; value=MCUSR ;break;
case     0x33:	TCCR0B=value; value=TCCR0B;break;
case     0x32:	TCNT0 =value; value=TCNT0 ;break;
case     0x31:	OSCCAL=value; value=OSCCAL;break;
case     0x30:	TCCR0A=value; value=TCCR0A;break;
case     0x2F:	TCCR1A=value; value=TCCR1A;break;
case     0x2E:	TCCR1B=value; value=TCCR1B;break;
case     0x2D:	TCNT1H=value; value=TCNT1H;break;
case     0x2C:	TCNT1L=value; value=TCNT1L;break;
case     0x2B:	OCR1AH=value; value=OCR1AH;break;
case     0x2A:	OCR1AL=value; value=OCR1AL;break;
case     0x29:	OCR1BH=value; value=OCR1BH;break;
case     0x28:	OCR1BL=value; value=OCR1BL;break;
case     0x26:	CLKPR =value; value=CLKPR ;break;
case     0x25:	ICR1H =value; value=ICR1H ;break;
case     0x24:	ICR1L =value; value=ICR1L ;break;
case     0x23:	GTCCR =value; value=GTCCR ;break;
case     0x22:	TCCR1C=value; value=TCCR1C;break;
case     0x21:	WDTCSR=value; value=WDTCSR;break;
case     0x20:	PCMSK =value; value=PCMSK ;break;
case     0x1E:	EEAR  =value; value=EEAR  ;break;
case     0x1D:	EEDR  =value; value=EEDR  ;break;
case     0x1C:	EECR  =value; value=EECR  ;break;
case     0x1B:	PORTA =value; value=PORTA ;break;
case     0x1A:	DDRA  =value; value=DDRA  ;break;
case     0x19:	PINA  =value; value=PINA  ;break;
case     0x18:	PORTB =value; value=PORTB ;break;
case     0x17:	DDRB  =value; value=DDRB  ;break;
case     0x16:	PINB  =value; value=PINB  ;break;
case     0x15:	GPIOR2=value; value=GPIOR2;break;
case     0x14:	GPIOR1=value; value=GPIOR1;break;
case     0x13:	GPIOR0=value; value=GPIOR0;break;
case     0x12:	PORTD =value; value=PORTD ;break;
case     0x11:	DDRD  =value; value=DDRD  ;break;
case     0x10:	PIND  =value; value=PIND  ;break;
case     0x0F:	USIDR =value; value=USIDR ;break;
case     0x0E:	USISR =value; value=USISR ;break;
case     0x0D:	USICR =value; value=USICR ;break;
case     0x0C:	UDR   =value; value=UDR   ;break;
case     0x0B:	UCSRA =value; value=UCSRA ;break;
case     0x0A:	UCSRB =value; value=UCSRB ;break;
case     0x09:	UBRRL =value; value=UBRRL ;break;
case     0x08:	ACSR  =value; value=ACSR  ;break;
case     0x03:	UCSRC =value; value=UCSRC ;break;
case     0x02:	UBRRH =value; value=UBRRH ;break;
case     0x01:	DIDR  =value; value=DIDR  ;break;
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


unsigned char send_with_crc(unsigned char crc, unsigned char data)
{
	UART0_wait_send(data);
	return crc_update(crc, data);
}


void send_resp2(unsigned char byte1, unsigned char byte2)
{
	unsigned char crc = 0xFF;
	crc = send_with_crc(crc, 0x55);
	crc = send_with_crc(crc, 0x02);
	crc = send_with_crc(crc, byte1);
	crc = send_with_crc(crc, byte2);
	crc = send_with_crc(crc, crc);
}


/* Interrupt handlers */

ISR(USART_RX_vect)
{

}

ISR(USART_UDRE_vect)
{

}


ISR(INT0_vect)
{

	INTC_counter++;

	if (INTC_on == 0)
	{
		return;
	}

	if (INTC_counter >= INTC_target)
	{
		INTC_on = 0;
		//PWM1_set(PWM1_stop);
		PWM0_set(PWM1_stop);
	}

}



void INTC_start(unsigned char target)
{
	INTC_on 		= 1;
	INTC_target 	= target;
	INTC_counter 	= 0;
}

void INTC_cancel()
{
	INTC_on 		= 0;
}

void INTC_init()
{
	INTC_on 		= 0;
	INTC_target 	= 0;
	INTC_counter 	= 0;
	//MCUCR 			|= (_BV(ISC01) | _BV(ISC00)); // rising edge of int0 generates int
	MCUCR 			|= (_BV(ISC00)); // any level change at int0 generates int
	GIMSK 			|= _BV(INT0);

}

void AIN_init()
{
	// ACD bit has 0 default value, no changes needed
	// initalization is needed when INT is in use
}


void PWM0_set(unsigned char value)
{
	OCR0A = value;
}

void PWM1_set(unsigned char value)
{
	OCR0B = value;
}


void DIR_set(unsigned char value)
{
	PORTB = value;
}

unsigned char PWM0_get()
{
	return OCR0A;
}

unsigned char PWM1_get()
{
	return OCR0B;
}

unsigned char AIN_get()
{
	return ACSR;
}


unsigned char DIR_get()
{
	return PORTB;
}



void DIR_setup()
{
	DDRB |= (_BV(PB5) | _BV(PB6) | _BV(PB2)); // direction 1 = output PB5-6 (pin17-18, MOSI,MISO) - dir, 2 - OC0A (pin14)
	DDRD |= _BV(PD5); // direction 1 = output , PD5 = OC0B (pin9)
}

void PWM_setup()
{
	// PWM pahse-correct, no prescaling(WGM=001,CS=001)


	TIMSK = 0;

	/*

	TCCR0A

	7,6	COM0A1:0
	5,4	COM0B1:0
	3,2	-
	1,0	WGM01:0

	*/


	TCCR0A 	= _BV(COM0A1) | _BV(COM0B1) | _BV(WGM00)| _BV(WGM01);

	/*

	TCCR0B

	7	FOC0A
	6 	FOC0B
	5,4	-
	3	WGM02
	2-0	CS02:0

	*/

	TCCR0B 	= _BV(CS00);//0b00000001;


	// initial value = off

	OCR0A	= 0x00;
	OCR0B	= 0x00;

}







// -----------------------------------------------------------------------------------------------------------------
/* Initialize UART */
void UART0_init_noint (unsigned char baudrate)
{
  /* Set the baud rate */
  UBRRL = baudrate;

  /* Enable UART receiver and transmitter */
  UCSRB = (1 << RXEN) | (1 << TXEN);

  /* 8 data bits, 1 stop bit */
  UCSRC = (1 << UCSZ1) | (1 << UCSZ0);

}



// -----------------------------------------------------------------------------------------------------------------
/* Read and write functions */
unsigned char UART0_wait_read (void)
{
  /* Wait for incomming data */
  while (!(UCSRA & (1 << RXC)));

  /* Return the data */
  return UDR;
}


// -----------------------------------------------------------------------------------------------------------------
void UART0_wait_send (unsigned char data)
{
  /* Wait for empty transmit buffer */
  while (!(UCSRA & (1 << UDRE)));

  /* Start transmittion */
  UDR = data;
}

// -----------------------------------------------------------------------------------------------------------------
unsigned char crc_update(unsigned char crc, unsigned char data)
{
	unsigned char i;
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
