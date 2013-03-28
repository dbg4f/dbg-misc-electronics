package dbg.electronics.robodrv.graphics;

public class TimeSeries {

    public final long time;
    public final int value;

    public TimeSeries(long time, int value) {
        this.time = time;
        this.value = value;
    }

    public TimeSeries(int value) {
        this.value = value;
        this.time = System.currentTimeMillis();
    }

    public TimeSeries(String textLine) {
        String[] splitted = textLine.split("\\s");
        this.time = Long.valueOf(splitted[0]);
        this.value = Integer.valueOf(splitted[1]) & 0xFF;
    }
}
