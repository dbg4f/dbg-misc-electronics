package dbg.electronics.robodrv.mcu;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static dbg.electronics.robodrv.mcu.ProtocolState.IN_SYNC;
import static dbg.electronics.robodrv.mcu.ProtocolState.NOT_IN_SYNC;

public class McuReportDecoder implements McuBytesListener {

    private static final Logger log = Logger.getLogger(McuReportDecoder.class);

    public static final int ADC_CHANNELS_IN_USE = 4;

    private McuReportListener reportListener;

    private ChannelStatusListener<ProtocolState> statusListener;

    private ReportCandidate mainReportCandidate = new ReportCandidate();

    private List<ReportCandidate> parallelCandidates = new ArrayList<ReportCandidate>();

    private ProtocolState syncState = NOT_IN_SYNC;

    public void setReportListener(McuReportListener reportListener) {
        this.reportListener = reportListener;
    }

    public void setStatusListener(ChannelStatusListener<ProtocolState> statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void onNextByte(byte nextByte) {

        log.debug(String.format("Next byte %02X", nextByte));

        if (syncState == IN_SYNC) {

            inSynchDecode(nextByte);

        }
        else {
            parallelDecode(nextByte);
        }

    }

    private void inSynchDecode(byte nextByte) {

        mainReportCandidate.onNextByte(nextByte);

        if (mainReportCandidate.isWrongWay()) {

            log.info(String.format("Out of sync %02X", nextByte));

            changeState(NOT_IN_SYNC);

            parallelDecode(nextByte);

        }
        else if (mainReportCandidate.isValidReportReady()) {
            forwardReport(mainReportCandidate.report);
            mainReportCandidate.reset();
        }
    }

    private void changeState(ProtocolState sync) {
        syncState = sync;
        statusListener.onStatusChanged(syncState);
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
                changeState(IN_SYNC);
                parallelCandidates.clear();
                log.info(String.format("Sync restored %02X", nextByte));
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
