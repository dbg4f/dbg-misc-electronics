package dbg.electronics.robodrv.emulator;

import dbg.electronics.robodrv.mcu.CommandCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static dbg.electronics.robodrv.mcu.M16Reg.*;

public class McEmulator {

    private static final int RESP_ERROR_CRC_MISMATCH = 0x03;
    private static final int START_PACKET_MARKER = 0x55;
    private static final int RESP_ERROR_NOT_IN_SYNC = 0x55;

    final InputStream inputStream;
    final OutputStream outputStream;

    public McEmulator(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }


    void read_reg(int reg, int sequence) throws IOException {
        int res = 0x55;
        send_resp2(0xDD, res, sequence);
    }

    // -----------------------------------------------------------------------------------------------------------------
    void write_reg(int reg, int value, int mask, int sequence) throws IOException {
        send_resp2(0xDA, value, sequence);
    }

    // -----------------------------------------------------------------------------------------------------------------
    void set_port_bits(int reg, int mask, int sequence) throws IOException {

        send_resp2(0xDA, mask, sequence);

    }

    // -----------------------------------------------------------------------------------------------------------------
    void clear_port_bits(int reg, int mask, int sequence) throws IOException {
        send_resp2(0xDA, mask, sequence);
    }

    // -----------------------------------------------------------------------------------------------------------------
    void exec_ext_command(int cmd, int param, int param2, int param3, int sequence) throws IOException {

        switch (cmd) {
            case 0x01: // ECHO
                send_resp2(param, param, sequence);
                break;

            case 0x14: //CMD_L1_READ_REG:
                read_reg(param, sequence);
                break;

            case 0x15: //CMD_L1_WRITE_REG:
                write_reg(param, param2, 0xFF, sequence);
                break;

            case 0x1B: //CMD_L1_SET_PORT_BITS:
                set_port_bits(param, param2, sequence);
                break;

            case 0x1C: //CMD_L1_CLEAR_PORT_BITS:
                clear_port_bits(param, param2, sequence);
                break;

            case 0x17: //CMD_L1_ENABLE_ADC:
                break;

            default:
                send_resp2(0xEE, param, sequence);
        }

    }

    int send_with_crc(int crc, int data) throws IOException {
        sendchar(data);
        return crc_update(crc, data);
    }

    private void sendchar(int data) throws IOException {
        outputStream.write(data);
    }

    int crc_update(int crc, int data) {
        int i;
        //
        crc ^= data;
        for (i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0)
                crc = (crc << 1) ^ 0xE5;
            else
                crc <<= 1;
        }
        return crc;
    }


// -----------------------------------------------------------------------------------------------------------------

    void tx_send_resp2_immediately(int byte1, int byte2, int sequence) throws IOException {
        int crc = 0xFF;
        crc = send_with_crc(crc, START_PACKET_MARKER);
        crc = send_with_crc(crc, 0x03);
        crc = send_with_crc(crc, sequence);
        crc = send_with_crc(crc, byte1);
        crc = send_with_crc(crc, byte2);
        crc = send_with_crc(crc, crc);
    }

// -----------------------------------------------------------------------------------------------------------------


    void send_resp2(int byte1, int byte2, int sequence) throws IOException {
        tx_send_resp2_immediately(byte1, byte2, sequence);
    }


    int main() throws IOException {

        int crc;
        int value;

        int length = 0;
        int sequence = 0;
        int command = 0;
        int parameter = 0;
        int parameter2 = 0;
        int parameter3 = 0;
        int ext_crc;


        for (; ; ) {
            crc = 0xFF;


            sequence = 0;
            command = 0;
            parameter = 0;
            parameter2 = 0;
            parameter3 = 0;

            // marker
            value = recvchar();
            crc = crc_update(crc, value);

            if (value == 'S') {
                sendchar('R');
                sendchar('D');
                sendchar('R');
                sendchar('V');
                sendchar('0');
                sendchar('2');
            } else if (value == START_PACKET_MARKER) {
                // length
                value = recvchar();
                crc = crc_update(crc, value);

                length = value;

                if (length >= 1) {
                    // command
                    value = recvchar();
                    crc = crc_update(crc, value);
                    command = value;

                }

                if (length >= 2) {
                    // command
                    value = recvchar();
                    crc = crc_update(crc, value);
                    sequence = value;

                }

                if (length >= 3) {
                    // parameter
                    value = recvchar();
                    crc = crc_update(crc, value);
                    parameter = value;
                }

                if (length >= 4) {
                    // parameter2
                    value = recvchar();
                    crc = crc_update(crc, value);
                    parameter2 = value;
                }

                if (length >= 5) {
                    // parameter2
                    value = recvchar();
                    crc = crc_update(crc, value);
                    parameter3 = value;
                }

                // crc from input
                ext_crc = recvchar();


                if (ext_crc == crc) {

                    // forward validated input
                    exec_ext_command(command, parameter, parameter2, parameter3, sequence);

                } else {
                    // crc not matched
                    send_resp2(RESP_ERROR_CRC_MISMATCH, crc, sequence);
                }


            } else {
                // not in sync
                send_resp2(RESP_ERROR_NOT_IN_SYNC, value, sequence);
            }

        }


    }

    private int recvchar() throws IOException {
        return inputStream.read();
    }


}
