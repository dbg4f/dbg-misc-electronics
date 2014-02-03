#ifndef _AVRTICK_H_
#define _AVRTICK_H_

	#include <avr/io.h>
    #include <avr/wdt.h>
    #include <avr/interrupt.h>
    #include <avr/power.h>

    uint16_t TICK_getCounter(void);

    void TICK_init(void);

	void TICK_onTimer(void);


#endif
