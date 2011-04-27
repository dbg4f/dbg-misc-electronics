;------------------------------------
; This is a AVR uC part of cruise
; control console, which is going
; to be used for visual monitoring
; of builds
;
; Authors: Dmitri Bogdel, Anton Keks
;------------------------------------

.NOLIST
.INCLUDE "1200def.inc"
.LIST

;------------------------------------
; input lines: clock and data
;------------------------------------
.EQU INP_CLK	= PORTD1
.EQU INP_DAT	= PORTD0

;------------------------------------
; temorary registers
;------------------------------------
.DEF TEMP0		= R16
.DEF TEMP2		= R19
.DEF TEMP3		= R20

.DEF BITS_COUNTER 		= R25
.DEF CURRENT_BITMASK	= R17

;------------------------------------
; LED bitmaps in registers. 
; One bit maps to one LED: green or red
; (8*4 = 32 bits = 16 red + 16 green LEDs)
;------------------------------------
.DEF LEDPINS0	= R21
.DEF LEDPINS1	= R22
.DEF LEDPINS2	= R23
.DEF LEDPINS3	= R24


;------------------------------------
; Use 8 rows and 4 columns
; Row is PORTB
; Column is bits 0..3 of PORTD
; (4..5 bits of PORTD used for communication)
; Column is used for "-" signal to LEDS
; Row is used for "+" signal to LEDS
;------------------------------------

.EQU MATRIX_COL0_MASK = ~0b0001000
.EQU MATRIX_COL1_MASK = ~0b0010000
.EQU MATRIX_COL2_MASK = ~0b0100000
.EQU MATRIX_COL3_MASK = ~0b1000000

;------------------------------------
; data port
;------------------------------------
.EQU DATA_PORT_IN		= PIND ; port D (use bits 4..5)

.EQU DATA_PORT_OUT_ROW	= PORTB ; port B
.EQU DATA_PORT_OUT_COL	= PORTD ; port D (use bits 0..3)

.EQU COL_MASK			= 0xF	; bits 0..3

;------------------------------------
; CODE SECTION
;------------------------------------
	rjmp main
	reti
	reti
	reti
	
;------------------------------------
procWaitCLKwhileUp:
	sbi	 PORTD,2
	sbic DATA_PORT_IN, INP_CLK
	rjmp procWaitCLKwhileUp
	cbi	 PORTD,2
	ret
;------------------------------------
procWaitCLKwhileDown:
	sbi	 PORTD,2
	sbis DATA_PORT_IN, INP_CLK
	rjmp procWaitCLKwhileDown
	cbi	 PORTD,2
	ret
;------------------------------------
procDelay:	
	ldi	TEMP3, 0x1
externalLoop:
	ldi	TEMP2, 0xFF
internalLoop:
	dec TEMP2	
	brne internalLoop	
	dec TEMP3
	brne externalLoop
	ret
;------------------------------------
procInit:
	; set PORT B to out direction
	ldi		TEMP0, 0xFF
	out		DDRB, TEMP0

	; set PORT D to out (3..6 bits) and input (0..1 bits)
	ldi		TEMP0, 0b1111100
	out		DDRD, TEMP0

	ret

;------------------------------------

main:

	; initial data for debug
	ldi	LEDPINS0, 0b11111111
	ldi	LEDPINS1, 0b11111111
	ldi	LEDPINS2, 0b11111111
	ldi	LEDPINS3, 0b11111111
	cbi	 PORTD,2

	; initialization

	rcall procInit

;------------------------------------
; blink 4 x 8 matrix
blinkLEDs:
	
	; check if it is time to receive new state
	; if CLK is low, next insruction will be skipped
	sbic DATA_PORT_IN, INP_CLK
	rjmp receiveState
	
	; blink 		
	ldi	TEMP0, 				MATRIX_COL0_MASK
	out DATA_PORT_OUT_COL, 	TEMP0
	out DATA_PORT_OUT_ROW, 	LEDPINS0
	rcall procDelay

	ldi	TEMP0, 				MATRIX_COL1_MASK
	out DATA_PORT_OUT_COL, 	TEMP0
	out DATA_PORT_OUT_ROW, 	LEDPINS1
	rcall procDelay
	
	ldi	TEMP0, 				MATRIX_COL2_MASK
	out DATA_PORT_OUT_COL, 	TEMP0
	out DATA_PORT_OUT_ROW, 	LEDPINS2
	rcall procDelay
	
	ldi	TEMP0, 				MATRIX_COL3_MASK
	out DATA_PORT_OUT_COL, 	TEMP0
	out DATA_PORT_OUT_ROW, 	LEDPINS3
	rcall procDelay
	
	rjmp blinkLEDs


;------------------------------------
receiveState:

	; set WD to maximum timeout (1.9 - 6.0 s)
	wdr
	ldi	TEMP0, 0b00001111
	out	WDTCR, TEMP0

	; turn off all leds
	ldi	TEMP0, 0xFF
	out DATA_PORT_OUT_COL, TEMP0


	rcall procWaitCLKwhileUp

;------------------------------------
	ldi BITS_COUNTER, 0x8
	ldi CURRENT_BITMASK, 0x01


nextBitPins0:
	
	; wait hight level at clock
	rcall procWaitCLKwhileDown

	; now clock is high level => read data
	
	; clear bit	in pins state 
	mov	TEMP0, CURRENT_BITMASK
	com TEMP0
	and LEDPINS0, TEMP0
	
	; skip next command if bit cleared
	sbic DATA_PORT_IN, INP_DAT
	; set bit, skipped if bit cleared
	or	LEDPINS0, CURRENT_BITMASK
		
	; wait for end of bit transmission
	rcall procWaitCLKwhileUp
	
	; prepare for next iteration:
	; shift mask
	lsl CURRENT_BITMASK
	; decrease bit counter
	dec BITS_COUNTER
	
	; iterate 8 bits in pin state register
	brne nextBitPins0
	
;------------------------------------
	ldi BITS_COUNTER, 0x8
	ldi CURRENT_BITMASK, 0x01


nextBitPins1:
	
	; wait hight level at clock
	rcall procWaitCLKwhileDown

	; now clock is hight level => read data
	
	; clear bit	in pins state 
	mov	TEMP0, CURRENT_BITMASK
	com TEMP0
	and LEDPINS1, TEMP0
	
	; skip next command if bit cleared
	sbic DATA_PORT_IN, INP_DAT
	; set bit, skipped if bit cleared
	or	LEDPINS1, CURRENT_BITMASK
		
	; wait for end of bit transmission
	rcall procWaitCLKwhileUp
	
	; prepare for next iteration:
	; shift mask
	lsl CURRENT_BITMASK
	; decrease bit counter
	dec BITS_COUNTER
	
	; iterate 8 bits in pin state register
	brne nextBitPins1
;------------------------------------
	ldi BITS_COUNTER, 0x8
	ldi CURRENT_BITMASK, 0x01


nextBitPins2:
	
	; wait hight level at clock
	rcall procWaitCLKwhileDown

	; now clock is hight level => read data
	
	; clear bit	in pins state 
	mov	TEMP0, CURRENT_BITMASK
	com TEMP0
	and LEDPINS2, TEMP0
	
	; skip next command if bit cleared
	sbic DATA_PORT_IN, INP_DAT
	; set bit, skipped if bit cleared
	or	LEDPINS2, CURRENT_BITMASK
		
	; wait for end of bit transmission
	rcall procWaitCLKwhileUp
	
	; prepare for next iteration:
	; shift mask
	lsl CURRENT_BITMASK
	; decrease bit counter
	dec BITS_COUNTER
	
	; iterate 8 bits in pin state register
	brne nextBitPins2

;------------------------------------
	ldi BITS_COUNTER, 0x8
	ldi CURRENT_BITMASK, 0x01


nextBitPins3:
	
	; wait hight level at clock
	rcall procWaitCLKwhileDown

	; now clock is hight level => read data
	
	; clear bit	in pins state 
	mov	TEMP0, CURRENT_BITMASK
	com TEMP0
	and LEDPINS3, TEMP0
	
	; skip next command if bit cleared
	sbic DATA_PORT_IN, INP_DAT
	; set bit, skipped if bit cleared
	or	LEDPINS3, CURRENT_BITMASK
		
	; wait for end of bit transmission
	rcall procWaitCLKwhileUp
	
	; prepare for next iteration:
	; shift mask
	lsl CURRENT_BITMASK
	; decrease bit counter
	dec BITS_COUNTER
	
	; iterate 8 bits in pin state register
	brne nextBitPins3
;------------------------------------
	
	; reset WD and disable it
	wdr
	ldi	TEMP0, 0
	out	WDTCR, TEMP0

	rjmp blinkLEDs


;------------------------------------
; end of program
;------------------------------------