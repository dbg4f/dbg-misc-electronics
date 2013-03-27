package dbg.electronics.robodrv.mcu;


import junit.framework.TestCase;

public class McuReportDecoderTest extends TestCase {

    private byte[] adcValues;
    private byte counter;
    private byte seq;
    private byte[] response;
    
    McuReportListener listener = new McuReportListener() {
        @Override
        public void onAdcValue(int channel, byte value) {
            adcValues[channel] = value;
        }

        @Override
        public void onCounterUpdate(byte value) {
            counter = value;
        }

        @Override
        public void onCommandResponse(byte sequence, byte[] params) {
            seq = sequence;
            response = params;
        }

        @Override
        public void onMcuReset() {
        }
    };

    @Override
    public void setUp() throws Exception {
        adcValues = new byte[4];
        seq = -1;
        response = null;
        counter = -1;
    }

    @Override
    public void tearDown() throws Exception {
        adcValues = null;
    }

    public void testDecode() throws Exception {

        int[] TEST_SET = new int[] {0x01, 0x83, 0xF8, 0x06, 0x51, 0x06, 0x7C, 0xF0, 0x61, 0xB0};

        McuReportDecoder decoder = new McuReportDecoder();

        decoder.setReportListener(listener);

        for (int b : TEST_SET) {
            decoder.onNextByte((byte) b);
        }

        assertEquals((byte)0x06, adcValues[0]);
        assertEquals((byte)0x7C, adcValues[1]);
        assertEquals((byte)0xF0, adcValues[2]);
        assertEquals((byte)0x61, adcValues[3]);

    }

    public void testCandidateAdc() {
        ReportCandidate candidate = new ReportCandidate();

        candidate.onNextByte((byte)0x51);
        candidate.onNextByte((byte)0x06);
        candidate.onNextByte((byte)0x7C);
        candidate.onNextByte((byte)0xF0);
        candidate.onNextByte((byte)0x61);
        candidate.onNextByte((byte)0xB0);

        assertTrue(candidate.isValidReportReady());
    }

    public void testCandidateCounter() {
        ReportCandidate candidate = new ReportCandidate();

        candidate.onNextByte((byte)0x52);
        candidate.onNextByte((byte)0x06);
        candidate.onNextByte((byte)0x38);

        assertTrue(candidate.isValidReportReady());
    }

    public void testCandidateResponse() {
        ReportCandidate candidate = new ReportCandidate();

        candidate.onNextByte((byte)0x55);
        candidate.onNextByte((byte)0x03);
        candidate.onNextByte((byte)0x01);
        candidate.onNextByte((byte)0x00);
        candidate.onNextByte((byte)0x00);
        candidate.onNextByte((byte)0x1C);

        assertTrue(candidate.isValidReportReady());
    }
}
