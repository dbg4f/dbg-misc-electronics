

.include "tn2313def.inc"

.equ BAUD_RATE = 12 			; 4800 at 1.000000 MHz

.equ PARAMS_BUFFER = $60

.def 	REG_A =	R17
.def 	REG_B =	R18
.def 	REG_C =	R19

.def 	REG_COUNT 		= R20		; INT0 events cout until stop PWM 
.def 	REG_COUNT_TMP 	= R21		; INT0 events cout until stop PWM 

.cseg

	rjmp	main
	rjmp	isr_int0
	rjmp	isr_stub
	rjmp	isr_stub
	rjmp	isr_stub
	rjmp	isr_stub
	rjmp	isr_stub
	rjmp	isr_stub
	rjmp	isr_stub
	rjmp	isr_stub
	rjmp	isr_stub


main:

	ldi		r16,	low(RAMEND)	
	out		SPL,	r16

	rcall	common_setup
	rcall	uart_setup
	rcall	pwm_setup
	
	ldi		REG_A,	$55
	rcall	uart_put

	
	rcall	uart_get
	cpi 	REG_A, 0xFF
	brne	main_loop
	
	rcall	int0_setup

pwm_stepping:

	ldi		REG_A,		0
	out		OCR0A,		REG_A
	
	rcall	uart_get
	mov		REG_COUNT,	REG_A

	rcall	uart_get
	out		OCR0A,		REG_A

	clr		REG_COUNT_TMP

	sei									; enable ints

wait_stepping_complete:

	cp		REG_COUNT_TMP,	REG_COUNT	
	brlo	wait_stepping_complete		; wait counter

send_counter:
	mov		REG_A,	REG_COUNT_TMP		; send content of counter
	rcall	uart_put
	
	rcall	uart_get					; ask next actions
	
	cpi		REG_A,	0xFF				; 0xFF - reset stepping
	breq	pwm_stepping		
	
	rjmp	send_counter				; re-read counter
	

	

	rjmp	pwm_stepping	

main_loop:

	rcall	uart_get

	out		OCR0A,	REG_A

	in		REG_A,	TCNT0

	rcall	uart_put
	
	rjmp	main_loop

;----------------------------------------------------------
; function
;----------------------------------------------------------
common_setup:
	
	ldi		r16,	0b11111111		; 1 - output
	out		DDRB,	r16

	ldi		r16,	0b00000000		; 1 - output
	out		DDRD,	r16

	ldi		r16,	0b00000100		; 1 - output
	out		PORTD,	r16


	ldi		r16,	0b10101000
	out		PORTB,	r16

	ret


;----------------------------------------------------------
; function
;----------------------------------------------------------
uart_setup:

	ldi		r16,	0
	out		UBRRH,	r16						; set baud rate generator

	ldi		r16,	BAUD_RATE
	out		UBRRL,	r16						; set baud rate generator

	ldi		r16,	(1<<TXEN)|(1<<RXEN)		; 0b00011000
	out		UCSRB,	r16						; enable UART Tx and Rx 

	ret


;----------------------------------------------------------
; function
;----------------------------------------------------------
pwm_setup:


	
	ldi		REG_A,	0b10000011
	out		TCCR0A,	REG_A

	ldi		REG_A,	0b00000001
	out		TCCR0B,	REG_A

	ldi		REG_A,	0x80
	out		OCR0A,	REG_A	

	ret

;----------------------------------------------------------
; function
;----------------------------------------------------------
int0_setup:
	
	in		REG_A,	MCUCR
	ori		REG_A,	0b00000011 		; INT0 by rising edge
	out		MCUCR,	REG_A

	ldi		REG_A,	0b01000000		; enable INT0
	out		GIMSK,	REG_A
	
	ret

;----------------------------------------------------------
; function
;----------------------------------------------------------
uart_get:
	sbis 	UCSRA,	RXC
	rjmp	uart_get
	in		REG_A,	UDR
	ret

;----------------------------------------------------------
; function
;----------------------------------------------------------
uart_put:
	sbis 	UCSRA,	UDRE	
	rjmp	uart_put
	out		UDR,	REG_A
	ret




;----------------------------------------------------------
; function
;----------------------------------------------------------
delay:
	
	push	REG_C
	ldi		REG_C, 0xFF
	
delay_next:
	nop
	dec		REG_C
	cpi		REG_C, 0
	brne	delay_next

	pop		REG_C
	
	ret


;----------------------------------------------------------
; interrupt handler
;----------------------------------------------------------
isr_int0:
	
	inc		REG_COUNT_TMP

	cp		REG_COUNT_TMP,	REG_COUNT	

	brlo	exit_isr_int0

	ldi		REG_A,		0
	out		OCR0A,		REG_A
		
exit_isr_int0:
	
	reti

;----------------------------------------------------------
; interrupt handler
;----------------------------------------------------------
isr_stub:
								
	reti						; do nothing, just return from ISR








/*

  TIMSK = 0x00;  // no Timer interrupts enabled
  DDRB = 0xFF;   // set all PortB pins as outputs
  PORTB = 0x00;  // all PORTB output pins Off

  // start up Timer0 in Fast PWM Mode at 122Hz to drive Green LED on output OC0A:
  //   8-bit Timer0 OC0A (PB2, pin 14) is set up for Fast PWM mode
  //   Fclk = Clock = 8MHz
  //   Prescale = 256
  //   OCR0A = 0 (to start out -- this value will increase to increase brightness of Green LED)
  //   F = Fclk / (Prescale * 256) = 122Hz
  TCCR0A = 0b10000011;  // COM0A1:0=10 for non-inverting PWM
                        // COM0B1:0=00 to disconnect OC0B
                        // bits 3:2 are unused
                        // WGM01:0=11 for Fast PWM Mode with TOP=FF (WGM02=0 in TCCR0B)
  TCCR0B = 0b00000100;  // FOC0A=0 (no force compare)
                        // F0C0B=0 (no force compare)
                        // bits 5:4 are unused
                        // WGM02=0 for Fast PWM Mode with TOP=FF (WGM01:0=11 in TCCR0A)
                        // CS02:00=100 for divide by 256 prescaler (this starts Timer0)
  OCR0A = 0;  // start with minimum brightness for Green LED on OC0A (PB2, pin 14)

  // start up Timer1 in Fast PWM Mode at 122Hz to drive Blue LED on output OC1A:
  //   16-bit Timer1 OC1A (PB3, pin 15) is set up for Fast PWM mode, with 8-bit resolution
  //   Fclk = Clock = 8MHz
  //   Prescale = 256
  //   TOP = 255
  //   OCR1A =  0 (to start out -- this value will increase to increase brightness of Blue LED)
  //   F = Fclk / (Prescale * (TOP+1) ) = 122Hz
  TCCR1A = 0b10000001;  // COM1A1:0=10 for non-inverting PWM
                        // COM1B1:0=00 to disconnect OC1B
                        // bits 3:2 are unused
                        // WGM11:10=01 for Fast PWM Mode with 8-bit resolution (WGM13:12=01 in TCCR1B)
  TCCR1B = 0b00001100;  // ICNC1=0 (no Noise Canceller)
                        // ICES1=0 (don't care about Input Capture Edge)
                        // bit 5 is unused
                        // WGM13:12=01 for Fast PWM Mode with 8-bit resolution (WGM11:10=01 in TCCR1A)
                        // CS12:10=100 for divide by 256 prescaler (this starts Timer1)
  TCCR1C = 0b00000000;  // FOC1A=0 (no Force Output Compare for Channel A)
                        // FOC1B=0 (no Force Output Compare for Channel B)
                        // bits 5:0 are unused
  OCR1A = 0;  // start with minimum brightness for Blue LED on OC1A (PB3, pin 15)

  int index = 0;
  // send the entire 2-minute sequence 15 times so that it lasts about 30 minutes
  for (int count=0; count<15; count++) {
    // send all rgbElements to LEDs (when both fadeTime =0 and holdTime = 0 it signifies last rgbElement in lightTab)
    do {
      sendrgbElement(index);   // send one rgbElement to LEDs
      index++;                 // increment to point to next rgbElement in lightTab
    } while ( !( (pgm_read_word(&lightTab[index].fadeTime) == 0 ) && (pgm_read_word(&lightTab[index].holdTime) == 0 ) ) );
    index = 0;
  }


*/

