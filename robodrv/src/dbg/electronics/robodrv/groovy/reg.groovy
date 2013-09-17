t.write("DDRD",   "00110000")
t.write("TCCR1A", "10100001")
t.write("TCCR1B", "00001001")
t.write("OCR1AH", "0000000")
t.write("OCR1AL", "1000000")
t.write("OCR1BH", "0000000")
t.write("OCR1BL", "1000000")



//drive
t.write("OCR1AL", "10000000")
t.write("PORTB",  "00010000")
