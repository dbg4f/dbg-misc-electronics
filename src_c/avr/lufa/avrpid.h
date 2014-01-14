#ifndef _AVRPID_H_
#define _AVRPID_H_

	#include <avr/io.h>
    #include <avr/wdt.h>
    #include <avr/interrupt.h>
    #include <avr/power.h>

    uint8_t AVRPID_get_value(int8_t error);

#endif
