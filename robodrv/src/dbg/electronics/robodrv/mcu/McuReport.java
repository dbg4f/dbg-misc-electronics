package dbg.electronics.robodrv.mcu;

public class McuReport {

   private ReportMarker marker;

   private byte sequence;

   private byte[] params;

   public McuReport(ReportCandidate candidate) {

        marker = candidate.marker;
        sequence = candidate.sequence;
        params = new byte[params.length];
        for (int i=0; i<params.length; i++) {
            params[i] = candidate.paramsList.get(i);
        }
    }

    public ReportMarker getMarker() {
        return marker;
    }

    public byte getSequence() {
        return sequence;
    }

    public byte[] getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "McuReport{" +
                "marker=" + marker +
                ", sequence=" + sequence +
                ", params=" + McuUtils.bytesToString(params) +
                '}';
    }
}
