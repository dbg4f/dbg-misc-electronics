package jprog.comm;

import java.util.*;
import java.io.IOException;

public class ProgAlgorithm implements CommandSender {

    public static final byte BIT_OUT = 0x01;
    public static final byte BIT_SCK = 0x08;
    public static final byte BIT_RST = 0x40;
    public static final byte BIT_IN = 0x02;

    protected IO io;

    public ProgAlgorithm(IO io) {
        this.io = io;
    }

    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] unbox(List commands) {
        byte[] bytes = new byte[commands.size()];
        int i = 0;
        for (Iterator iterator = commands.iterator(); iterator.hasNext();) {
            Byte cmd = (Byte) iterator.next();
            bytes[i++] = cmd.byteValue();
        }
        return bytes;
    }

    public String decode(byte[] rawStream) {
        StringBuffer buf = new StringBuffer();
        for (byte cmd : rawStream) {
            buf.append("RST=").append(((cmd & BIT_RST) != 0) ? "1" : "0").append(" ");
            buf.append("SCK=").append(((cmd & BIT_SCK) != 0) ? "1" : "0").append(" ");
            buf.append("OUT=").append(((cmd & BIT_OUT) != 0) ? "1" : "0").append(" ");
            buf.append("IN =").append(((cmd & BIT_IN) != 0) ? "1" : "0").append("\n");

        }
        return buf.toString();
    }
    
    private void expandByte(byte b, List commands) {

        for (int i = 7; i >= 0; i--) {
            int bitValue = (b >> i) & 1;
            progSendBit(bitValue != 0, commands);
        }


    }

    public void start() throws IOException, InterruptedException {
        /*

      1. Power-up sequence:
      Apply power between VCC and GND while RESET and SCK are set to �0�. In
      some systems, the programmer can not guarantee that SCK is held low during
      power-up. In this case, RESET must be given a positive pulse of at least two
      CPU clock cycles duration after SCK has been set to 0.

        */

        long delay = 30;

        exchgNow(1, 0, 1, delay);
        exchgNow(0, 0, 1, delay);
        exchgNow(1, 0, 0, delay);
        exchgNow(0, 0, 0, delay);

        /*
        2. Wait for at least 20 ms and enable serial programming by sending the Programming
        Enable serial instruction to pin MOSI.
        */



    }

    void progSendBit(boolean b, List commands) {

        exchg(0, b ? 1 : 0, 0, commands);
        exchg(0, b ? 1 : 0, 1, commands);
        exchg(0, b ? 1 : 0, 0, commands);

    }

    void exchg(int rst, int out, int sck, List commands) {

        byte b = 0;

        b = (byte) (b | (rst != 0 ? BIT_RST : 0));
        b = (byte) (b | (out != 0 ? BIT_OUT : 0));
        b = (byte) (b | (sck != 0 ? BIT_SCK : 0));

        commands.add(new Byte(b));
    }


    void exchgNow(int rst, int out, int sck, long delay) throws IOException, InterruptedException {
        List bytes = new ArrayList();
        exchg(rst, out, sck, bytes);
        byte[] unboxedCmd = unbox(bytes);
        for (int i=0; i<unboxedCmd.length; i++) {
            blockingIO(new byte[] {unboxedCmd[i]}, 1000);
        }
        
        sleep(delay);
    }

    private void blockingIO(byte[] bytes, int i) {
        //To change body of created methods use File | Settings | File Templates.
    }

/*
  byte setBit(byte src, boolean bit, byte mask) {
      // TODO: convert
      return (byte) (bit ? (src | mask) : (src & (~mask)));
  }
*/

    public void addBatchCommand(byte[] cmd, List commands) {
        for (int i = 0; i < cmd.length; i++) {
            expandByte(cmd[i], commands);
        }
    }

    public byte[] readResponse(byte[] response) throws IOException {
        if (response.length % 24 != 0) {
            throw new IOException("Response cannot be splitted with length: " + response.length);
        }
        
        byte[] result = new  byte[response.length / 24];
        /*
        for (int i=result.length-1; i>=0; i--) {
            result[result.length - i - 1] = 0;
            for(int j=7; j>=0; j--) {
                int offset = response.length - (i*24 + j*3 + 1);
                boolean bitValue = ((response[offset] & BIT_IN) != 0);
                result[result.length - i - 1] |= bitValue ? (1 << j) : 0;
            }
        }
        */
        for (int i=result.length-1; i>=0; i--) {
            result[result.length-1-i] = 0;
            for(int j=7; j>=0; j--) {
                int offset = response.length - (i*24 + j*3 + 2);
                boolean bitValue = ((response[offset] & BIT_IN) != 0);
                result[result.length-1-i] |= bitValue ? (1 << j) : 0;
            }
        }
        
        
        return result;
    }
    


    private void dumpBin(byte[] commands) {
        for (int i = 0; i < commands.length; i++) {
            byte command = commands[i];
            System.out.println(
                    "RST=" + (((command & BIT_RST) != 0) ? "1" : "0") + " " +
                    "SCK=" + (((command & BIT_SCK) != 0) ? "1" : "0") + " " +
                    "OUT=" + (((command & BIT_OUT) != 0) ? "1" : "0"));

           if ((i + 1) % 3 == 0) {
               System.out.println("");
           }
        }
    }


    
    public byte[] process(byte[] command) {

        try {

            LOG.logger.info("Start command = " + HEX.dumpHex(command));

            List commands = new ArrayList();

            addBatchCommand(command, commands);

            byte[] unboxedCommand = unbox(commands);
            
            List response = new ArrayList();
            
            //LOG.logger.info("Raw command(" + unboxedCommand.length + ") = " + HEX.dumpHex(unboxedCommand));
            
            //LOG.logger.info("Raw command - decode" + decode(unboxedCommand));
            
            for (int i=0; i<unboxedCommand.length; i++) {
                response.addAll(blockingIO2(new byte[] {unboxedCommand[i]}, 1000));
            }
            
         
            byte[] unboxedResp = unbox(response);

            //LOG.logger.info("Raw response to command(" + unboxedResp.length + ") = " + HEX.dumpHex(unboxedResp));
            
            //LOG.logger.info("Raw response - decode" + decode(unboxedResp));

            byte[] parsedResp = readResponse(unboxedResp);

            LOG.logger.info("Parsed response = " + HEX.dumpHex(parsedResp));

            return parsedResp;
            
        } catch (Exception ex) {
            LOG.logger.error("io2", ex);
            throw new RuntimeException(ex);
        }
    }

    private Collection blockingIO2(byte[] bytes, int i) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


}


