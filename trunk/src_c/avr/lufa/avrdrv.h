#ifndef _AVRDRV_H_
#define _AVRDRV_H_

	#include <avr/io.h>
    #include <avr/wdt.h>
    #include <avr/interrupt.h>
    #include <avr/power.h>

    void DRV_setPwm(uint8_t channel, uint8_t value);

    void DRV_setDirection(uint8_t channel, uint8_t value);

    void DRV_init(void);

#endif
