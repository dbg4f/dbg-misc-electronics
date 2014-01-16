#include "avrpid.h"
#include "avrdrv.h"

#define PID_CHANNEL 1

uint8_t currentCommand = 0;

void AVRPID_onClock(uint8_t currentPosition)
{
	// NB! beware of ISR restrictions

	
	
	DRV_setPwm(PID_CHANNEL, 0);
	

}


void AVRPID_setCommand(uint8_t command)
{
	currentCommand = command;
}
