package dbg.electronics.robodrv.mcu;

import java.util.ArrayList;
import java.util.List;

public class McuReportDecoder implements McuBytesListener {

    public static final int ADC_CHANNELS_IN_USE = 3;

    private McuReportListener reportListener;

    private ReportCandidate mainReportCandidate;

    private List<ReportCandidate> parallelCandidates = new ArrayList<ReportCandidate>();

    private boolean inSync = true;

    public void setReportListener(McuReportListener reportListener) {
        this.reportListener = reportListener;
    }

    @Override
    public void onNextByte(byte nextByte) {


        if (inSync) {

            inSynchDecode(nextByte);

        }
        else {
            parallelDecode(nextByte);
        }

    }

    private void inSynchDecode(byte nextByte) {

        mainReportCandidate.onNextByte(nextByte);

        if (mainReportCandidate.isWrongWay()) {

            inSync = false;

            parallelDecode(nextByte);

        }
        else if (mainReportCandidate.isValidReportReady()) {
            forwardReport(mainReportCandidate.report);
            mainReportCandidate.reset();
        }
    }

    private void parallelDecode(byte nextByte) {

        parallelCandidates.add(new ReportCandidate());

        List<ReportCandidate> copyOfCandidates = new ArrayList<ReportCandidate>(parallelCandidates);

        for (ReportCandidate candidate : copyOfCandidates) {

            candidate.onNextByte(nextByte);

            if (candidate.isWrongWay()) {
                parallelCandidates.remove(candidate);
            }

            if (candidate.isValidReportReady()) {
                forwardReport(candidate.report);
                mainReportCandidate = candidate;
                mainReportCandidate.reset();
                inSync = true;
                parallelCandidates.clear();
                break;
            }

        }
        
    }

    private void forwardReport(McuReport report) {

        if (report.getMarker() == ReportMarker.ADC_UPDATE) {

            for (int i=0; i<report.getParams().length; i++) {
                reportListener.onAdcValue(i, report.getParams()[i]);
            }

        }
        else if (report.getMarker() == ReportMarker.COUNTER_UPDATE) {
            reportListener.onCounterUpdate(report.getParams()[0]);
        }
        else if (report.getMarker() == ReportMarker.COMMAND_RESPONSE) {
            reportListener.onCommandResponse(report.getSequence(), report.getParams());
        }

    }



}
