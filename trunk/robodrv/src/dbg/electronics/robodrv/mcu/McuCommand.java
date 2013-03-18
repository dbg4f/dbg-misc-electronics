package dbg.electronics.robodrv.mcu;

import java.util.concurrent.atomic.AtomicLong;

import static dbg.electronics.robodrv.mcu.McuUtils.crcPutToTail;

public class McuCommand {

    /*
    #define CMD_L1_ECHO 		    0x01
#define CMD_L1_READ_REG 	    0x14
#define CMD_L1_WRITE_REG 	    0x15
#define CMD_L1_READ_ADC0 	    0x16
#define CMD_L1_ENABLE_ADC 	    0x17
#define CMD_L1_SWITCH_WATCHDOG 	0x18
#define CMD_L1_RESET_WATCHDOG 	0x19
#define CMD_L1_WRITE_REG_MASK 	0x1A

#define RESP_UNKNOWN_CMD	0xEE
#define RESP_OK	            0xAA

#define RESP_ERROR_CRC_MISMATCH  0x03
#define RESP_ERROR_NOT_IN_SYNC   0x55

#define RESP_RESET_MARKER_A0   0x55
#define RESP_RESET_MARKER_A1   0x33
#define RESP_RESET_MARKER_B0   0xAA
#define RESP_RESET_MARKER_B1   0x55

#define START_PACKET_MARKER     0x55
#define START_ASYNC_MARKER_ADC  0x51
#define START_ASYNC_MARKER_CT   0x52


out:
marker,length,command,sequence,params...,crc

in:
packet marker,length,sequence,param1,param2,crc
adc marker,values[3..4],crc
ct marker,value,crc



    crc = send_with_crc(crc, START_PACKET_MARKER);
    crc = send_with_crc(crc, 0x03);
    crc = send_with_crc(crc, sequence);
    crc = send_with_crc(crc, byte1);
    crc = send_with_crc(crc, byte2);
    crc = send_with_crc(crc, crc);

    p_resp_context->response[0] = 3; // length of resp bytes
    p_resp_context->response[1] = sequence;
    p_resp_context->response[2] = byte1;
    p_resp_context->response[3] = byte2;


     */


    public static final byte START_PACKET_MARKER = 0x55;

    private static AtomicLong sequenceSource = new AtomicLong(0);

    private CodifierAware cmd;
    private byte sequence;
    private byte[] params;
    private byte[] rawCommand;

    private McuCommand(CodifierAware cmd,  int...  params) {

        this.cmd = cmd;
        this.sequence = nextSequence();
        this.params = new byte[params.length];
        rawCommand = new byte[params.length + 5];

        rawCommand[0] = START_PACKET_MARKER;
        rawCommand[1] = (byte)(params.length + 4);
        rawCommand[2] = (byte)cmd.toCode();
        rawCommand[3] = this.sequence;

        for (int i=0; i<params.length; i++) {
            rawCommand[i + 4] = (byte) params[i];
        }

        crcPutToTail(rawCommand);

    }

    public static McuCommand createCommand(CodifierAware code, int... params) {
        return new McuCommand(code, params);
    }

    private byte nextSequence() {
        return (byte)(sequenceSource.incrementAndGet() & 0xFF);
    }

    public String toRawBytesString() {
        return McuUtils.bytesToString(rawCommand);
    }

    @Override
    public String toString() {
        return String.format("%s [%03d] %s", cmd.toString(), sequence & 0xFF, McuUtils.bytesToString(params));
    }



}
