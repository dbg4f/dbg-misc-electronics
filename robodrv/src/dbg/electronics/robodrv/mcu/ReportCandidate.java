package dbg.electronics.robodrv.mcu;

import java.util.ArrayList;
import java.util.List;

import static dbg.electronics.robodrv.mcu.McuUtils.crcUpdate;

public class ReportCandidate {

    ReportMarker marker;
    int bytesCounter;
    int expectedLength;
    byte sequence;
    boolean reportReady;
    boolean wrongWay;
    McuReport report;
    List<Byte> paramsList = new ArrayList<Byte>();

    public ReportCandidate() {
        reset();
    }

    void reset() {
        marker = null;
        bytesCounter = 0;
        expectedLength = 0;
        sequence = 0;
        reportReady = false;
        wrongWay = false;
        report = null;
        paramsList.clear();
    }

    void onNextByte(byte nextByte) {

        bytesCounter++;

        if (reportReady || wrongWay) {
            return;
        }

        if (bytesCounter == 1) {

            if (!ReportMarker.isValidMarker(nextByte)) {
                wrongWay = true;
            }
            else {

                marker = ReportMarker.fromByte(nextByte);

                if (marker == ReportMarker.ADC_UPDATE) {
                    expectedLength = McuReportDecoder.ADC_CHANNELS_IN_USE;
                }
                else if (marker == ReportMarker.COUNTER_UPDATE) {
                    expectedLength = 1;
                }

            }
        }
        else if (bytesCounter == 2 && marker == ReportMarker.COMMAND_RESPONSE) {
            expectedLength = nextByte;
        }
        else if (bytesCounter == 3 && marker == ReportMarker.COMMAND_RESPONSE) {
            sequence = nextByte;
        }
        else if (expectedLength > paramsList.size()) {
            paramsList.add(nextByte);
        }
        else {

            if (crc() != nextByte) {
                wrongWay = true;
            } else {
                reportReady = true;
                report = new McuReport(this);
            }

        }

    }

    private byte crc() {

        byte crc = (byte) 0xFF;

        crc = crcUpdate(crc, marker.getCode());

        if (marker == ReportMarker.COMMAND_RESPONSE) {
            crc = crcUpdate(crc, (byte) expectedLength);
            crc = crcUpdate(crc, sequence);
        }

        for (byte param : paramsList) {
            crc = crcUpdate(crc, param);
        }
        
        return crc;
    }

    boolean isValidReportReady() {
        return reportReady;
    }

    boolean isWrongWay() {
        return wrongWay;
    }

    McuReport getReport() {
        return report;
    }

    @Override
    public String toString() {
        return "ReportCandidate{" +
                "marker=" + marker +
                ", bytesCounter=" + bytesCounter +
                ", expectedLength=" + expectedLength +
                ", sequence=" + sequence +
                ", reportReady=" + reportReady +
                ", wrongWay=" + wrongWay +
                ", report=" + report +
                ", paramsList=" + paramsList +
                '}';
    }
}
