# Устанавливаем необходимые для быстрой прошивки загрузчика fuse-биты: кварц > 8 МГц, встроенный делитель на 8 выключен:
avrdude -c ftbb -P ft0 -p m8 -B 9600 -U hfuse:w:0xdd:m -U lfuse:w:0xff:m
# Заливаем загрузчик:
avrdude -c ftbb -P ft0 -p ATMega168 -B 19200 -U flash:w:ATmegaBOOT_168_diecimila.hex
