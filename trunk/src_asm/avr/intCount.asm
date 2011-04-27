

.include "tn2313def.inc"

.equ BAUD_RATE = 12 			; 4800 at 1.000000 MHz

.equ PARAMS_BUFFER = $60

.def 	REG_A =	R17
.def 	REG_B =	R18
.def 	REG_C =	R19


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

	rcall	uart_setup
	
	ldi		REG_A,	$56
	rcall	uart_put

	sei									; enable ints

main_loop:

	rcall	uart_get

	mov		REG_A,	REG_C

	rcall	uart_put
	
	rjmp	main_loop


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
; interrupt handler
;----------------------------------------------------------
isr_int0:
	
	inc		REG_C
	
	reti

;----------------------------------------------------------
; interrupt handler
;----------------------------------------------------------
isr_stub:
								
	reti						; do nothing, just return from ISR








