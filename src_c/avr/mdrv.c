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

/* Prototypes */
void 			USART0_Init( unsigned int baudrate );
unsigned char 	USART0_Receive();
void 			USART0_Transmit( unsigned char data );
void 			PWM0_set(unsigned char value); 
void 			PWM1_set(unsigned char value); 
void 			DIR_set(unsigned char value); 

unsigned char	PWM0_get(); 
unsigned char	PWM1_get(); 
unsigned char	DIR_get(); 

void 			DIR_setup();
void 			PWM_setup();






int main()
{
	
	unsigned char pwm_value;

	unsigned char pwmH;	
	unsigned char pwmL;

	//unsigned char dir;

	unsigned char check_counter = 0;

	unsigned char command_code;
	unsigned char command_value;
	
	USART0_Init(8);   /* Set the baudrate to 57600 bps using a 4.0 MHz crystal, U2X = 1*/
	//USART0_Init(12);   /* Set the baudrate to 19,200 bps using a 4.0 MHz crystal */

	PWM_setup();

	DIR_setup();

	sei();           /* Enable interrupts => enable UART interrupts */

	USART0_Transmit(0xA5);
	USART0_Transmit(0xA5);
	
	USART0_Transmit(0x26);
	USART0_Transmit(0x09);
	USART0_Transmit(0x10);
	USART0_Transmit(0x02);

	for(;;)        /* Forever */
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
		// unknown command
		else 
		{
			USART0_Transmit(command_code); 
		}
		


		//pwm0 = USART0_Receive();
		//pwm0L = USART0_Receive();
		//pwm1 = USART0_Receive();
		//pwm1L = USART0_Receive();
		
		//dir  = USART0_Receive();

		//pwm0 = ((pwm0H & 0x7F) << 1);// + (pwm0L & 0x7);
		//pwm1 = ((pwm1H & 0x7F) << 1);// + (pwm1L & 0x7);

		//PWM0_set(pwm0);
		//PWM1_set(pwm1);
		//DIR_set(dir);		
		

		//USART0_Receive();
		
		//USART0_Transmit(pwm0 + pwm1 + dir); 
	}

	return 0;
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


