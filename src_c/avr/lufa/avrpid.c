#include "avrpid.h"
#include "avrdrv.h"

#define PID_CHANNEL 1

// TODO: ~1 second
#define REG_TIMEOUT_PER_CYCELE 1000 

#define MAX_REVERSALS_PER_CYCLE 3

uint8_t currentCommand = 0;
uint8_t savedPwm = 0;
int8_t savedDirection = 0;

int16_t timeoutCounter = REG_TIMEOUT_PER_CYCELE;
int8_t reverseCounter = MAX_REVERSALS_PER_CYCLE;


void set_pwm(uint8_t value) 
{	
	DRV_setPwm(PID_CHANNEL, value);	
	savedPwm = value;
}

void set_direction(uint8_t value) 
{	
	DRV_setDirection(PID_CHANNEL, value);	
	savedDirection = value;
}


int16_t calc_regulator_value(int16_t currentError)
{

	int16_t result;
	
	result = abs(currentError << 5); // x32

	if (result > 255)
	{
		result = 255;
	}
	
	if (currentError < 0) 
	{
		result = (-result);
	}
		
	return result;
	
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
		
		int16_t regValue = calc_regulator_value(currentError);
		
		uint8_t direction = 1;
		
		if (regValue < 0)
		{
			regValue = -regValue;
			direction = 0;
		}
		
		if (direction != savedDirection) 
		{
			reverseCounter--;
		}
	
		set_pwm(regValue);
		set_direction(direction);
		
	
		timeoutCounter--;
		
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

