package dbg.electronics.robodrv.mcu;


import junit.framework.TestCase;

public class McuReportDecoderTest extends TestCase {

    McuReportListener listener = new McuReportListener() {
        @Override
        public void onAdcValue(int channel, byte value) {
            System.out.println("channel = " + channel);
            System.out.println("value = " + value);
        }

        @Override
        public void onCounterUpdate(byte value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onCommandResponse(byte sequence, byte[] params) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onMcuReset() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };


    public void testDecode() throws Exception {


        /*

        01 83 F8 06 51 01 7B F1 08 51 10 83 F8 D4


         */

        int[] TEST_SET = new int[] {0x01, 0x83, 0xF8, 0x06, 0x51, 0x01, 0x7B, 0xF1, 0x08, 0x51, 0x10, 0x83, 0xF8, 0xD4};



        McuReportDecoder decoder = new McuReportDecoder();

        decoder.setReportListener(listener);

        ReportCandidate candidate = new ReportCandidate();

        candidate.onNextByte((byte)0x51);
        System.out.println("candidate0 = " + candidate);
        candidate.onNextByte((byte)0x01);
        System.out.println("candidate1 = " + candidate);
        candidate.onNextByte((byte)0x7B);
        System.out.println("candidate2 = " + candidate);
        candidate.onNextByte((byte)0xF1);
        System.out.println("candidate3 = " + candidate);
        candidate.onNextByte((byte)0x08);
        System.out.println("candidate4 = " + candidate);


        for (int b : TEST_SET) {
            decoder.onNextByte((byte) b);
        }


    }
}
