sudo avrdude -c avr109 -p m16 -P /dev/ttyUSB0 -U flash:w:mdrv.hex -B 19200 -v
