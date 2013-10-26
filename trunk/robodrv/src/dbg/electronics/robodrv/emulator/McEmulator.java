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

    private boolean adcEnabled;

    final InputStream inputStream;
    final OutputStream outputStream;

    public McEmulator(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public static int ADC_BUFFER_SIZE         = 16;
    public static int ADC_BUFFER_DIV_SHIFT    = 4;

    public static int ADC_CHANNELS_IN_USE     = 4;
    public static int TX_MAX_BUFFER_SIZE      = 20;

    public static int  START_ASYNC_MARKER_ADC  = 0x51;

    class PADC_BUFFER
    {
        int valuations[];
        int index;
    }

    class PTX_BUFFER
    {
        int content[];
        int index;
        int size;
    };


    class PADC_CONTEXT {
        PADC_BUFFER adc_buffers[];
        int avg_values[];
        int adc_buf_index;
        int valuations_count;
    }

    class PTX_CONTEXT
    {
        int enabled;
        PTX_BUFFER adc_tx_buf;
        PTX_BUFFER ct_tx_buf;
        PTX_BUFFER resp_tx_buf;
        PTX_BUFFER current_buffer;
    } ;


    void send_adc_snapshot(PTX_BUFFER p_tx_buf) throws IOException {

        for (int i=0; i<p_tx_buf.content.length; i++){
            sendchar(p_tx_buf.content[i]);
        }

    }

    public void sendAdc(int[] adcValues) throws IOException {
        if (adcEnabled) {
            PTX_BUFFER buffer = new PTX_BUFFER();
            tx_adc_snapshot(buffer, adcValues);
            send_adc_snapshot(buffer);
        }
    }

    void tx_adc_snapshot(PTX_BUFFER p_tx_buf, int[] avg_values)
    {
        int crc = 0xFF;

        p_tx_buf.index = 0;
        p_tx_buf.size = ADC_CHANNELS_IN_USE + 2; // start marker + avg bytes + crc
        p_tx_buf.content = new int[p_tx_buf.size];
        p_tx_buf.content[0] = START_ASYNC_MARKER_ADC;

        crc = crc_update(crc, p_tx_buf.content[0]);

        int i;
        for (i=0; i<ADC_CHANNELS_IN_USE; i++)
        {
            p_tx_buf.content[i+1] = avg_values[i];
            crc = crc_update(crc, p_tx_buf.content[i+1]);
        }

        p_tx_buf.content[i+1] = crc;

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
                adcEnabled = true;
                System.out.println("ADC enabled");
                break;

            default:
                send_resp2(0xEE, param, sequence);
        }

    }

    int send_with_crc(int crc, int data) throws IOException {
        sendchar(data);
        return crc_update(crc, data);
    }

    private synchronized void sendchar(int data) {
        //System.out.println("OUT = " + String.format("%02X", data));
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(0);
        }
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


                // TODO: enable crc checks
                if (/*ext_crc == crc*/true) {

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
        int read = inputStream.read();
        //System.out.println("IN = " + String.format("%02X", read));
        return read;
    }


}
