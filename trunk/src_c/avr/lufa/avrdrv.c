#include "avrdrv.h"


void DRV_setPwm(uint8_t channel, uint8_t value)
{
	if (channel == 0) 
	{
		OCR1AL = value;
	}
	else if (channel == 1)
	{
		OCR1BL = value;
	}
	
}

void DRV_setDirection(uint8_t channel, uint8_t value)
{
	if (channel == 0) 
	{
		//PORTE.6 = value;
	}
	else if (channel == 1)
	{
		//PORTB.4 = value;
	}

}

void DRV_init(void)
{
	
	/*
	 
    OC1A, board pins: ~9;   PE6: pin 7
    OC1B, board pin: ~10;   PB4: pin 8


     DRV:
      7  8  ~9  ~10
    GND
     
	 */
	
	TCCR1A = 0b10100001;
	TCCR1B = 0b00000001;
	
	OCR1AH = 0;
	OCR1AL = 0;
	
	OCR1BH = 0;
	OCR1BL = 0;
	
	DDRE = 0b01000000;
	DDRB = 0b01110000;
	
}
