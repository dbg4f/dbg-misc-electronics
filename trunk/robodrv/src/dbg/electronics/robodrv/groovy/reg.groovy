t.write("DDRD",   "00110000")
t.write("TCCR1A", "10100001")
t.write("TCCR1B", "00001001")
t.write("OCR1AH", "00000000")
t.write("OCR1AL", "10000000")
t.write("OCR1BH", "00000000")
t.write("OCR1BL", "10000000")
t.write("DDRB",   "00011000")



//drive
t.write("OCR1AL", "10000000")
t.write("PORTB",  "00010000")

// OCR1A, PB.4 - front
// OCR1B, PB.3 - back
