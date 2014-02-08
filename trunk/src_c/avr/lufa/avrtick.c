#include "avrtick.h"
#include "USBtoSerial.h"
#include "avrpid.h"

#define ADC_POSITION_CHANNEL 0

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
	AVRPID_onClock(ADC_getValue(ADC_POSITION_CHANNEL));
}

ISR(TIMER0_OVF_vect) {
    TICK_onTimer();
}
