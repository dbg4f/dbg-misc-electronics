// Martin Thomas 4/2008

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <util/delay.h>

#define LED_PORT   PORTB
#define LED_DDR    DDRB
#define LED_BIT    PB2

#define BT_PORT    PORTC
#define BT_DDR     DDRC
#define BT_PIN     PINC
#define BT_BIT     PC7

int main( void ) 
{
	
	while(1);
	

	return 0; /* never reached */
}
