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

    public int getResult(){
        if (params.length >= 1) {
            return params[0];
        }
        else {
            throw new IllegalArgumentException("No result params in response");
        }
    }

    public int getResultWord(){
        if (params.length >= 2) {
            return (params[0]&0xFF) * 0x100 + (params[1]&0xFF);
        }
        else {
            throw new IllegalArgumentException("No result params in response");
        }
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
