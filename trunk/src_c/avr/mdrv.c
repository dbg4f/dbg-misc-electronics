#include <avr/io.h>
#include <avr/interrupt.h>


/* UART Buffer Defines */
#define USART_RX_BUFFER_SIZE 8     /* 2,4,8,16,32,64,128 or 256 bytes */
#define USART_TX_BUFFER_SIZE 8     /* 2,4,8,16,32,64,128 or 256 bytes */

#define USART_RX_BUFFER_MASK ( USART_RX_BUFFER_SIZE - 1 )
#define USART_TX_BUFFER_MASK ( USART_TX_BUFFER_SIZE - 1 )

#if ( USART_RX_BUFFER_SIZE & USART_RX_BUFFER_MASK )
	#error RX buffer size is not a power of 2
#endif
#if ( USART_TX_BUFFER_SIZE & USART_TX_BUFFER_MASK )
	#error TX buffer size is not a power of 2
#endif

/* Static Variables */
static unsigned char 			USART_RxBuf[USART_RX_BUFFER_SIZE];
static volatile unsigned char 	USART_RxHead;
static volatile unsigned char 	USART_RxTail;
static unsigned char 			USART_TxBuf[USART_TX_BUFFER_SIZE];
static volatile unsigned char 	USART_TxHead;
static volatile unsigned char 	USART_TxTail;

void 			UART0_init_noint (unsigned char baudrate);
unsigned char 	UART0_wait_read (void);
void 			UART0_wait_send (unsigned char data);
unsigned char 	crc_update(unsigned char crc, unsigned char data);

unsigned char 	send_with_crc(unsigned char crc, unsigned char data);
void 			send_resp2(unsigned char byte1, unsigned char byte2); 
void 			exec_ext_command(unsigned char cmd, unsigned char param);


static unsigned char 	INTC_on;
static unsigned char 	INTC_target;
static unsigned char 	INTC_counter;

void 			USART0_Init( unsigned int baudrate );
unsigned char 	USART0_Receive();
void 			USART0_Transmit( unsigned char data );
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
	
	//unsigned char pwm_value;

	//unsigned char pwmH;	
	//unsigned char pwmL;

	//unsigned char dir;

	//unsigned char check_counter = 0;

	//unsigned char command_code;
	//unsigned char command_value;

	unsigned char crc;
	unsigned char value;
	unsigned char counter;
	unsigned char i;

	unsigned char length = 0;
	unsigned char command = 0;
	unsigned char parameter = 0;
	unsigned char ext_crc;		
	
	
	//USART0_Init(8);   /* Set the baudrate to 57600 bps using a 4.0 MHz crystal, U2X = 1*/
	//USART0_Init(12);   /* Set the baudrate to 19,200 bps using a 4.0 MHz crystal */

	UART0_init_noint(12); // 4M, U2X=0, 19200, 0.2%

	PWM_setup();

	DIR_setup();

	INTC_init();

	AIN_init();

	//sei();           /* Enable interrupts => enable UART interrupts */

	send_resp2(0x55, 0x33);	

	send_resp2(0xAA, 0x55);		
	
	
	for (;;)
	{
		crc 	= 0xFF;

		command = 0;
		parameter = 0;
		
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
			
			if (length == 2)
			{
				// parameter
				value 	= UART0_wait_read();
				crc 	= crc_update(crc, value);
				parameter = value;
			}
	
			// crc from input
			ext_crc = UART0_wait_read();
			

			if (ext_crc == crc) 
			{
				
				// forward validated input
				exec_ext_command(command, parameter);

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
			send_resp2(0x55, 0xBB);
		}


	}




/*
	for(;;)        
	{
		
		command_code = (USART0_Receive() & 0xF);
		

		// echo
		if (command_code == 1) 
		{
			command_value = USART0_Receive();
			USART0_Transmit(command_value); 
		}
		// add counter
		else if (command_code == 2) 
		{
			command_value = USART0_Receive();
			check_counter += command_value;
			USART0_Transmit(check_counter); 
		}
		// reset counter
		else if (command_code == 3) 
		{
			command_value = USART0_Receive();
			check_counter = 0;
			USART0_Transmit(check_counter); 
		}
		// set pwm1
		else if (command_code == 4) 
		{
			pwmH = USART0_Receive();
			pwmL = USART0_Receive();
			pwm_value = ((pwmH << 4) & 0xF0) + (pwmL & 0xF);
			//pwm_value = USART0_Receive();
			PWM0_set(pwm_value);
			USART0_Transmit(pwm_value); 
		}
		// set pwm2
		else if (command_code == 5) 
		{
			pwmH = USART0_Receive();
			pwmL = USART0_Receive();
			pwm_value = ((pwmH << 4) & 0xF0) + (pwmL & 0xF);
			//pwm_value = USART0_Receive();
			PWM1_set(pwm_value);
			USART0_Transmit(pwm_value); 
		}
		// set dir
		else if (command_code == 6) 
		{
			pwmH = USART0_Receive();
			pwmL = USART0_Receive();
			pwm_value = ((pwmH << 4) & 0xF0) + (pwmL & 0xF);
			//pwm_value = USART0_Receive();
			DIR_set(pwm_value);
			USART0_Transmit(pwm_value); 
		}
		// get pwm1
		else if (command_code == 7) 
		{			
			pwm_value = PWM0_get();
			USART0_Transmit(pwm_value >> 4); 
			USART0_Transmit(pwm_value); 
		}
		// get pwm2
		else if (command_code == 8) 
		{
			pwm_value = PWM1_get();
			USART0_Transmit(pwm_value >> 4); 
			USART0_Transmit(pwm_value); 
		}
		// get dir
		else if (command_code == 9) 
		{
			pwm_value = DIR_get();
			USART0_Transmit(pwm_value >> 4); 
			USART0_Transmit(pwm_value); 

		}
		// get interrupts counter
		else if (command_code == 10) 
		{
			USART0_Transmit(INTC_counter >> 4); 
			USART0_Transmit(INTC_counter); 

		}
		// schedule PWM1 off
		else if (command_code == 11) 
		{			
			pwmH = USART0_Receive();
			pwmL = USART0_Receive();
			INTC_start(merge(pwmH, pwmL));
			USART0_Transmit(INTC_counter); 
		}
		// cancel schedule PWM1
		else if (command_code == 12) 
		{			
			INTC_cancel();
			USART0_Transmit(INTC_counter); 
		}
		// get schedule status
		else if (command_code == 13) 
		{						
			USART0_Transmit(INTC_on); 
		}
		// get AIN value
		else if (command_code == 14) 
		{						
			pwm_value = AIN_get();
			USART0_Transmit(pwm_value >> 4); 
			USART0_Transmit(pwm_value); 
		}
		// unknown command
		else 
		{
			USART0_Transmit(command_code); 
		}
		

	}
*/
	return 0;
}


void exec_ext_command(unsigned char cmd, unsigned char param)
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

}

unsigned char merge(unsigned char vH, unsigned char vL)
{
	return ((vH << 4) & 0xF0) + (vL & 0xF);
}


unsigned char getH(unsigned char v)
{
	return v >> 4;
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


/* Initialize USART */
void USART0_Init(unsigned int baudrate)
{
	unsigned char x;

	/* Set the baud rate */
	UBRRH = (unsigned char) (baudrate>>8);                  
	UBRRL = (unsigned char) (baudrate);
	
	/* Enable UART receiver and transmitter */
	UCSRB |= _BV(TXEN) | _BV(RXEN) | _BV(RXCIE) | _BV(UDRIE);
	UCSRA |= _BV(U2X);
		
	/* Flush receive buffer */
	x = 0; 			    

	USART_RxTail = x;
	USART_RxHead = x;
	USART_TxTail = x;
	USART_TxHead = x;
}

/* Interrupt handlers */

ISR(USART_RX_vect) 
{
	unsigned char data;
	unsigned char tmphead;

	/* Read the received data */
	data = UDR;                 
	
	/* Calculate buffer index */
	tmphead = (USART_RxHead + 1) & USART_RX_BUFFER_MASK;
	
	USART_RxHead = tmphead;      /* Store new index */

	if (tmphead == USART_RxTail)
	{
		/* ERROR! Receive buffer overflow */
	}
	
	USART_RxBuf[tmphead] = data; /* Store received data in buffer */
}

ISR(USART_UDRE_vect) 
{
	unsigned char tmptail;

	/* Check if all data is transmitted */
	if ( USART_TxHead != USART_TxTail )
	{
		/* Calculate buffer index */
		tmptail = ( USART_TxTail + 1 ) & USART_TX_BUFFER_MASK;
		USART_TxTail = tmptail;      /* Store new index */
	
		UDR = USART_TxBuf[tmptail];  /* Start transmition */
	}
	else
	{
		UCSRB &= ~(1<<UDRIE);         /* Disable UDRE interrupt */
	}
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
		PWM1_set(0);
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

/* Read and write functions */
unsigned char USART0_Receive()
{
	unsigned char tmptail;
	
	while (USART_RxHead == USART_RxTail);  /* Wait for incomming data */
		

	tmptail = (USART_RxTail + 1) & USART_RX_BUFFER_MASK;/* Calculate buffer index */
	
	USART_RxTail = tmptail;                /* Store new index */
	
	return USART_RxBuf[tmptail];           /* Return data */
}

void USART0_Transmit( unsigned char data )
{
	unsigned char tmphead;
	/* Calculate buffer index */
	tmphead = ( USART_TxHead + 1 ) & USART_TX_BUFFER_MASK; /* Wait for free space in buffer */
	
	
	while ( tmphead == USART_TxTail );


	USART_TxBuf[tmphead] = data;           /* Store data in buffer */
	USART_TxHead = tmphead;                /* Store new index */

	UCSRB |= (1<<UDRIE);                    /* Enable UDRE interrupt */
}

unsigned char DataInReceiveBuffer()
{
	return ( USART_RxHead != USART_RxTail ); /* Return 0 (FALSE) if the receive buffer is empty */
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
