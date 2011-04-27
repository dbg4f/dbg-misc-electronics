

.include "tn2313def.inc"

.equ F_CPU = 4000000


/*


Q=4000000	 RATE=2400.0	 diff=0.1603	 nearestUBRR=103.0000	 UBRR=103.1667 baud2=2403.8462
Q=4000000	 RATE=4800.0	 diff=0.1603	 nearestUBRR=51.0000	 UBRR=51.0833 baud2=4807.6923
Q=4000000	 RATE=9600.0	 diff=0.1603	 nearestUBRR=25.0000	 UBRR=25.0417 baud2=9615.3846
Q=4000000	 RATE=14400.0	 diff=2.1242	 nearestUBRR=16.0000	 UBRR=16.3611 baud2=14705.8824
Q=4000000	 RATE=19200.0	 diff=0.1603	 nearestUBRR=12.0000	 UBRR=12.0208 baud2=19230.7692
Q=4000000	 RATE=28800.0	 diff=-3.5494	 nearestUBRR=8.0000	 UBRR=7.6806 baud2=27777.7778
Q=4000000	 RATE=38400.0	 diff=-6.9940	 nearestUBRR=6.0000	 UBRR=5.5104 baud2=35714.2857
Q=4000000	 RATE=57600.0	 diff=8.5069	 nearestUBRR=3.0000	 UBRR=3.3403 baud2=62500.0000
Q=4000000	 RATE=76800.0	 diff=8.5069	 nearestUBRR=2.0000	 UBRR=2.2552 baud2=83333.3333
Q=4000000	 RATE=115200.0	 diff=8.5069	 nearestUBRR=1.0000	 UBRR=1.1701 baud2=125000.0000


Q=8867238	 RATE=2400.0	 diff=-0.0356	 nearestUBRR=230.0000	 UBRR=229.9177 baud2=2399.1445
Q=8867238	 RATE=4800.0	 diff=0.3990	 nearestUBRR=114.0000	 UBRR=114.4588 baud2=4819.1511
Q=8867238	 RATE=9600.0	 diff=-0.4665	 nearestUBRR=57.0000	 UBRR=56.7294 baud2=9555.2134
Q=8867238	 RATE=14400.0	 diff=1.2797	 nearestUBRR=37.0000	 UBRR=37.4863 baud2=14584.2730
Q=8867238	 RATE=19200.0	 diff=-0.4665	 nearestUBRR=28.0000	 UBRR=27.8647 baud2=19110.4267
Q=8867238	 RATE=28800.0	 diff=1.2797	 nearestUBRR=18.0000	 UBRR=18.2431 baud2=29168.5461
Q=8867238	 RATE=38400.0	 diff=3.0882	 nearestUBRR=13.0000	 UBRR=13.4324 baud2=39585.8839
Q=8867238	 RATE=57600.0	 diff=-3.7843	 nearestUBRR=9.0000	 UBRR=8.6216 baud2=55420.2375
Q=8867238	 RATE=76800.0	 diff=3.0882	 nearestUBRR=6.0000	 UBRR=6.2162 baud2=79171.7679
Q=8867238	 RATE=115200.0	 diff=-3.7843	 nearestUBRR=4.0000	 UBRR=3.8108 baud2=110840.4750

Q=1000000	 RATE=2400.0	 diff=0.1603	 nearestUBRR=25.0000	 UBRR=25.0417 baud2=2403.8462
Q=1000000	 RATE=4800.0	 diff=0.1603	 nearestUBRR=12.0000	 UBRR=12.0208 baud2=4807.6923
Q=1000000	 RATE=9600.0	 diff=-6.9940	 nearestUBRR=6.0000	 UBRR=5.5104 baud2=8928.5714
Q=1000000	 RATE=14400.0	 diff=8.5069	 nearestUBRR=3.0000	 UBRR=3.3403 baud2=15625.0000
Q=1000000	 RATE=19200.0	 diff=8.5069	 nearestUBRR=2.0000	 UBRR=2.2552 baud2=20833.3333
Q=1000000	 RATE=28800.0	 diff=8.5069	 nearestUBRR=1.0000	 UBRR=1.1701 baud2=31250.0000
Q=1000000	 RATE=38400.0	 diff=-18.6198	 nearestUBRR=1.0000	 UBRR=0.6276 baud2=31250.0000
Q=1000000	 RATE=57600.0	 diff=8.5069	 nearestUBRR=0.0000	 UBRR=0.0851 baud2=62500.0000
Q=1000000	 RATE=76800.0	 diff=-18.6198	 nearestUBRR=0.0000	 UBRR=-0.1862 baud2=62500.0000
Q=1000000	 RATE=115200.0	 diff=-45.7465	 nearestUBRR=0.0000	 UBRR=-0.4575 baud2=62500.0000


*/

//.equ BAUD_RATE = 25 			; 9600 at 4 MHz

.equ BAUD_RATE = 12 			; 4800 at 1.000000 MHz

.equ PARAMS_BUFFER = $60


.equ MASK_OUT = 0x01
.equ MASK_SCK = 0x08
.equ MASK_RST = 0x40
.equ MASK_IN  = 0x02

.def 	REG_A =	R17
.def 	REG_B =	R18
.def 	REG_C =	R19


.equ BIT_OUT = 0
.equ BIT_SCK = 3
.equ BIT_RST = 5
.equ BIT_IN  = 1


.def	PR_IN0 = R20
.def	PR_IN1 = R21
.def	PR_IN2 = R22
.def	PR_IN3 = R23

.def	PR_OUT0 = R6
.def	PR_OUT1 = R7
.def	PR_OUT2 = R8
.def	PR_OUT3 = R9


; TODO check if these regs usage is ok
;.def reg_a = r16
;.def reg_b = r17
;.def reg_c = r18

.cseg

	rjmp	main
	rjmp	isr_stub
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
	
	ldi		r16,	$55
	rcall	uart_put


	rcall	test_send

main_loop:

	rcall	uart_get4
	
	rcall	parse_in4
	
	rcall	uart_put4
	
	rjmp	main_loop

;----------------------------------------------------------
; function
;----------------------------------------------------------
common_setup:
	
	ldi		r16,	0b11111101
	out		DDRB,	r16

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
parse_in4:
	
	cpi		PR_IN0, 	0xFF
	brne	normal_command
	
	cpi		PR_IN1, 	0xFF
	brne	normal_command
	
	cpi		PR_IN2, 	0xFF
	brne	normal_command
	
	out		PORTB,	 	PR_IN3
	in		PR_OUT3, 	PINB

	rjmp	parse_in4_exit	

normal_command:

	mov		REG_A,		PR_IN0
	rcall	send_byte
	mov		PR_OUT0,	REG_C

	mov		REG_A,		PR_IN1
	rcall	send_byte
	mov		PR_OUT1,	REG_C

	mov		REG_A,		PR_IN2
	rcall	send_byte
	mov		PR_OUT2,	REG_C

	mov		REG_A,		PR_IN3
	rcall	send_byte
	mov		PR_OUT3,	REG_C

parse_in4_exit:
	ret




;----------------------------------------------------------
; function
;----------------------------------------------------------
uart_put:
	sbis 	UCSRA,	UDRE	
	rjmp	uart_put
	out		UDR,	r16
	ret


;----------------------------------------------------------
; function
;----------------------------------------------------------
uart_put4:
	sbis 	UCSRA,	UDRE	
	rjmp	uart_put4
	out		UDR,	PR_OUT0

uart_put4_1:
	sbis 	UCSRA,	UDRE	
	rjmp	uart_put4_1
	out		UDR,	PR_OUT1

uart_put4_2:
	sbis 	UCSRA,	UDRE	
	rjmp	uart_put4_2
	out		UDR,	PR_OUT2

uart_put4_3:
	sbis 	UCSRA,	UDRE	
	rjmp	uart_put4_3
	out		UDR,	PR_OUT3

	ret


;----------------------------------------------------------
; function
;----------------------------------------------------------
uart_get:
	sbis 	UCSRA,	RXC
	rjmp	uart_get
	in		r16,	UDR
	out		PORTB,	r16
	ret


;----------------------------------------------------------
; function
;----------------------------------------------------------
uart_get4:
	sbis 	UCSRA,	RXC
	rjmp	uart_get4
	in		PR_IN0,	UDR
	
uart_get4_1:
	sbis 	UCSRA,	RXC
	rjmp	uart_get4_1
	in		PR_IN1,	UDR
	
uart_get4_2:
	sbis 	UCSRA,	RXC
	rjmp	uart_get4_2
	in		PR_IN2,	UDR
	
uart_get4_3:
	sbis 	UCSRA,	RXC
	rjmp	uart_get4_3
	in		PR_IN3,	UDR
	
	ret


;----------------------------------------------------------
; function
;----------------------------------------------------------
send_byte:

	clr		REG_C

	sbrs	REG_A, 7
	rcall	send_bit_0
	sbrc	REG_A, 7
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<7)
	
	
	sbrs	REG_A, 6
	rcall	send_bit_0
	sbrc	REG_A, 6
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<6)
	
	sbrs	REG_A, 5
	rcall	send_bit_0
	sbrc	REG_A, 5
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<5)
	
	sbrs	REG_A, 4
	rcall	send_bit_0
	sbrc	REG_A, 4
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<4)
	
	sbrs	REG_A, 3
	rcall	send_bit_0
	sbrc	REG_A, 3
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<3)
	
	sbrs	REG_A, 2
	rcall	send_bit_0
	sbrc	REG_A, 2
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<2)
	
	sbrs	REG_A, 1
	rcall	send_bit_0
	sbrc	REG_A, 1
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<1)
	
	sbrs	REG_A, 0
	rcall	send_bit_0
	sbrc	REG_A, 0
	rcall	send_bit_1
	sbrc	REG_B, BIT_IN
	sbr		REG_C, (1<<0)
	
	ret



;----------------------------------------------------------
; function
;----------------------------------------------------------
send_bit_0:
	
	cbi		PORTB, BIT_OUT		; OUT = 0

	rcall	delay

	sbi		PORTB, BIT_SCK		; SCK = 1 
	
	rcall	delay	

	in		REG_B, PINB

	cbi		PORTB, BIT_SCK		; SCK = 0

	ret

;----------------------------------------------------------
; function
;----------------------------------------------------------
send_bit_1:
	
	sbi		PORTB, BIT_OUT		; OUT = 1

	rcall	delay

	sbi		PORTB, BIT_SCK		; SCK = 1 
	
	rcall	delay	

	in		REG_B, PINB

	cbi		PORTB, BIT_SCK		; SCK = 0

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
isr_stub:
								
	reti						; do nothing, just return from ISR








; -----------------------------------
;
;
;TESTS
;
;
;------------------------------------


test_send:

	
	ldi		PR_IN0, 0x00
	ldi		PR_IN1, 0x53
	ldi		PR_IN2, 0x00
	ldi		PR_IN3, 0x00

	rcall	uart_put4

	ret
