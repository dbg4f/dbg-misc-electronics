package dbg.electronics.robodrv.mcu;


import junit.framework.TestCase;

public class McuReportDecoderTest extends TestCase {

    private byte[] adcValues;
    
    McuReportListener listener = new McuReportListener() {
        @Override
        public void onAdcValue(int channel, byte value) {
            adcValues[channel] = value;
        }

        @Override
        public void onCounterUpdate(byte value) {
        }

        @Override
        public void onCommandResponse(byte sequence, byte[] params) {
        }

        @Override
        public void onMcuReset() {
        }
    };

    @Override
    public void setUp() throws Exception {
        adcValues = new byte[3];
    }

    @Override
    public void tearDown() throws Exception {
        adcValues = null;
    }

    public void testDecode() throws Exception {

        int[] TEST_SET = new int[] {0x01, 0x83, 0xF8, 0x06, 0x51, 0x01, 0x7B, 0xF1, 0x08, 0x51, 0x10, 0x83, 0xF8, 0xD4};

        McuReportDecoder decoder = new McuReportDecoder();

        decoder.setReportListener(listener);

        for (int b : TEST_SET) {
            decoder.onNextByte((byte) b);
        }

        assertEquals((byte)0x10, adcValues[0]);
        assertEquals((byte)0x83, adcValues[1]);
        assertEquals((byte)0xF8, adcValues[2]);

    }

    public void testCandidate() {
        ReportCandidate candidate = new ReportCandidate();

        candidate.onNextByte((byte)0x51);
        candidate.onNextByte((byte)0x01);
        candidate.onNextByte((byte)0x7B);
        candidate.onNextByte((byte)0xF1);
        candidate.onNextByte((byte)0x08);

        assertTrue(candidate.isValidReportReady());
    }
}
