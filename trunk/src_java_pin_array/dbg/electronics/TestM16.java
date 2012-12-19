package dbg.electronics;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: dmitri
 * Date: 12/15/12
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestM16 {


    public static void main(String[] args) throws IOException, McCommunicationException {

        Socket socket = new Socket("127.0.0.1", 4444);

        McConnection mc = new McConnection(socket);

        mc.send(McCommand.CMD_L1_ECHO, (byte)0x55);

        mc.send(McCommand.CMD_L1_ENABLE_ADC);


        //mc.readReg(AtMega16Reg.ADCSRA);
        //mc.readReg(AtMega16Reg.ADCH);
        //mc.readReg(AtMega16Reg.ADCL);
        mc.readReg(AtMega16Reg.ADMUX);

        setRegBit(mc, AtMega16Reg.SREG, 7, true);

        setRegBit(mc, AtMega16Reg.DDRD, 7, true);
        setRegBit(mc, AtMega16Reg.PORTD, 7, true);



        for (int i=0; 1<2; i++) {
            mc.send(McCommand.CMD_L1_READ_ADC0);
            //mc.readReg(AtMega16Reg.ADCH);
            //mc.readReg(AtMega16Reg.ADCL);
            //mc.readReg(AtMega16Reg.ADCSRA);
        }

    }

    public static void setRegBit(McConnection mc, AtMega16Reg reg, int bit, boolean value) throws IOException, McCommunicationException {

        byte pb = mc.readReg(reg);

        mc.writeReg(reg, value ? (byte)(pb | (1<<bit)) : (byte)(~(1<<bit) & pb));
    }



}
