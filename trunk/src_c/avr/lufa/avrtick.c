#include "avrtick.h"

uint16_t counter = 0;

uint16_t TICK_getCounter(void)
{
	return counter;
}

void TICK_init(void)
{
	TCCR0A=0x00; 
	TCCR0B|=(1<<CS01)|(1<<CS00);  // prescaler 64	
    TCNT0=0x00;
	TIMSK0=(1<<TOIE0);
}

void TICK_onTimer(void)
{
	counter++;
}

ISR(TIMER0_OVF_vect) {
    TICK_onTimer();
}
