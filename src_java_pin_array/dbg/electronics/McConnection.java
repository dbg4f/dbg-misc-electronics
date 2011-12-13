package dbg.electronics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 12/13/11
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class McConnection {

    private InputStream in;
    private OutputStream out;

    private McWatchdog watchdog = new McWatchdog();

    public McConnection(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        watchdog.launch();
    }

    public byte send(McCommand cmd, byte... params) throws IOException, McCommunicationException {


        byte[] pack = new byte[params.length + 4];

        pack[0] = 0x55;
        pack[1] = (byte)(params.length - 1);
        pack[2] = cmd.getCode();

        System.arraycopy(params, 0, pack, 3, params.length - 2 - 3);

        crc_put(pack);

        watchdog.enter();

        out.write(pack);

        int marker = in.read();

        if (marker != 0x55) {
            throw new McCommunicationException("Marker expected to be 0x55, but received " + String.format("%02X", marker));
        }




        return -1;

    }

    public byte readReg(At2313Reg reg) {
        return -1;
    }

    public void writeReg(At2313Reg reg, byte value) {

    }

    private byte crc_calc(byte... content) {
        byte crc = (byte) 0xFF;

        for (byte b : content) {
            crc = crc_update(crc, b);
        }

        return crc;
    }

    private void crc_put(byte[] content) {

        byte crc = (byte) 0xFF;

        for (int i=0; i<content.length - 2; i++) {
            crc = crc_update(crc, content[i]);
        }

        content[content.length - 1] = crc;

    }

    private byte crc_update(byte crc, byte data) {

        int i;

        crc ^= data;

        for (i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0x00) {
                crc = (byte) ((crc << 1) ^ 0xE5);
            } else {
                crc <<= 1;
            }
        }

        return crc;
    }



}
