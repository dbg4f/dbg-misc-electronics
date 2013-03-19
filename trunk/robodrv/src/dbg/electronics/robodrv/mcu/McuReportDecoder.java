package dbg.electronics.robodrv.mcu;

public class McuReportDecoder implements McuBytesListener {

    class ReportCandidate {

        final ReportMarker marker;

        boolean reportReady = false;

        McuReport report;

        ReportCandidate(ReportMarker marker) {
            this.marker = marker;
        }

        void onNextByte(byte nextByte) {

        }

        boolean isValidReportReady() {
            return reportReady;
        }

        McuReport getReport() {
            return report;
        }

    }

    private McuReportListener reportListener;

    public void setReportListener(McuReportListener reportListener) {
        this.reportListener = reportListener;
    }

    @Override
    public void onNextByte(byte nextByte) {

    }



}
