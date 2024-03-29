package dbg.electronics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class McConnection {

    private InputStream in;
    private OutputStream out;

    private Socket socket;


    private McWatchdog watchdog = new McWatchdog(this);

    private McLogger logger = ConsoleLogger.getInstance();


    public McConnection(Socket socket) throws IOException {
        fromSocket(socket);
        watchdog.launch();
    }

    private void fromSocket(Socket socket) throws IOException {
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.socket = socket;
    }


    private void reInit() throws IOException {
        if (socket != null) {
            socket.close();
            Socket socketNew = new Socket(socket.getInetAddress(), socket.getPort());
            fromSocket(socketNew);
        }

    }


    public McConnection(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        watchdog.launch();
    }

    public byte send(McCommand cmd, byte... params) throws IOException, McCommunicationException {
        try {
            return send2(cmd, params);
        }
        catch (Exception e) {

            restart();
            return send(cmd, params);
        }

    }

    public void restart() throws IOException, McCommunicationException {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        sinkIn();
        reInit();
        send(McCommand.ECHO, (byte) 0x22);
    }

    public byte send2(McCommand cmd, byte... params) throws IOException, McCommunicationException {

        // TODO: check command params count

        sinkIn();

        byte[] pack = new byte[params.length + 4];

        pack[0] = 0x55;
        pack[1] = (byte)(params.length + 1);
        pack[2] = cmd.getCode();

        System.arraycopy(params, 0, pack, 3, params.length);

        crc_put(pack);

        watchdog.enter();

        logger.debug("Sending ", pack);

        out.write(pack);

        if (cmd.getCommandType() == McCommandType.NO_RESPONSE) {
            watchdog.leave();
            return -1;
        }

        byte marker = (byte)in.read();

        if (marker != 0x55) {
            watchdog.leave();
            throw new McCommunicationException("Marker expected to be 0x55, but received " + String.format("%02X", marker));
        }

        byte len = (byte)in.read();

        byte[] response = new byte[len];

        for (int i= 0; i<len; i++) {
            response[i] = (byte)in.read();
        }

        byte crc = (byte)in.read();

        watchdog.leave();

        byte respCrc = crc_calc(marker, len, response);

        if (crc != respCrc) {
            throw new McCommunicationException("CRC error " + String.format("message %02X calc %02X", crc, respCrc));
        }

        logger.info("Command " + cmd.name() + " ", response);

        // TODO: check command result

        return response.length >= 2 ? response[1] : -1;

    }

    private void sinkIn() throws IOException {

        int len = in.available();

        byte[] sink = new byte[len];

        if (len > 0) {

            for (int i=0; i<len ;i++) {
                sink[i] = (byte)in.read();
            }

            logger.info("Sinking ", sink);

        }

    }

    public byte readReg(At2313Reg reg) throws IOException, McCommunicationException {
        return send(McCommand.READ_REG, reg.getCode());
    }

    public void writeReg(At2313Reg reg, byte value) throws IOException, McCommunicationException {
        send(McCommand.WRITE_REG, reg.getCode(), value);
    }

    public byte readReg(AtMega16Reg reg) throws IOException, McCommunicationException {
        return send(McCommand.CMD_L1_READ_REG, reg.getCode());
    }

    public void writeReg(AtMega16Reg reg, byte value) throws IOException, McCommunicationException {
        send(McCommand.CMD_L1_WRITE_REG, reg.getCode(), value);
    }

    private byte crc_calc(byte marker, byte len, byte... content) {
        byte crc = (byte) 0xFF;

        crc = crc_update(crc, marker);

        crc = crc_update(crc, len);

        for (byte b : content) {
            crc = crc_update(crc, b);
        }

        return crc;
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

        for (int i=0; i<content.length - 1; i++) {
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
