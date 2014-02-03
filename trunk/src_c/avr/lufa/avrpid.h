#ifndef _AVRPID_H_
#define _AVRPID_H_

	#include <avr/io.h>
    #include <avr/wdt.h>
    #include <avr/interrupt.h>
    #include <avr/power.h>
    #include <stdlib.h>

    void AVRPID_onClock(uint8_t currentPosition);
    
    void AVRPID_setCommand(uint8_t command);
    
    void AVRPID_setupTimer(void);

#endif
