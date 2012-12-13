sudo avrdude -c avr109 -p m16 -P /dev/ttyUSB0 -U flash:r:mdrv.hex -B 19200 -v

