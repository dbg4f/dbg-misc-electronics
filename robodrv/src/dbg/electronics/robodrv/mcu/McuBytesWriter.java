package dbg.electronics.robodrv.mcu;

import java.io.IOException;

public interface McuBytesWriter {

    void write(byte[] command) throws IOException;

}
