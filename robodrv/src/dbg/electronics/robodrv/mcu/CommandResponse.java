package dbg.electronics.robodrv.mcu;

public class CommandResponse {
    final byte sequence;
    final byte[] params;
    private long execTime;
    CommandResponse(byte sequence, byte[] params) {
        this.sequence = sequence;
        this.params = params;
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

    @Override
    public String toString() {
        return "Response{" +
                "sequence=" + sequence +
                ", execTime=" + execTime +
                ", params=" + McuUtils.bytesToString(params) +
                '}';
    }
}
