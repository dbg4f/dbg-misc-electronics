#include "avrpid.h"
#include "avrdrv.h"

#define PID_CHANNEL 1

// TODO: ~1 second
#define REG_TIMEOUT_PER_CYCELE 100 

#define MAX_REVERSALS_PER_CYCLE 3

uint8_t currentCommand = 0;
uint8_t savedPwm = 0;

int16_t timeoutCounter = REG_TIMEOUT_PER_CYCELE;
int8_t reverseCounter = MAX_REVERSALS_PER_CYCLE;


void set_pwm(uint8_t value) 
{	
	DRV_setPwm(PID_CHANNEL, value);	
	savedPwm = value;
}

void AVRPID_onClock(uint8_t currentPosition)
{
	// NB! beware of ISR restrictions

	int16_t currentError = currentCommand - currentPosition;
		
	if (timeoutCounter <= 0 || reverseCounter <= 0) 
	{
		if (savedPwm != 0) 
		{
            set_pwm(0);
        }
    }
    else 
    {
		
	}	

	
	
	
	

}


void reset_counters(void)
{
	timeoutCounter = REG_TIMEOUT_PER_CYCELE;
	reverseCounter = MAX_REVERSALS_PER_CYCLE;
}

void AVRPID_setCommand(uint8_t command)
{
	
	if (command != currentCommand)
	{
		reset_counters();
	}
	
	currentCommand = command;
	
}

void AVRPID_setupTimer(void)
{
	
}

