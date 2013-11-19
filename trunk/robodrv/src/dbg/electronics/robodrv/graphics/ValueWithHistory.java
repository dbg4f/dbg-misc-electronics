package dbg.electronics.robodrv.graphics;


import dbg.electronics.robodrv.Range;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ValueWithHistory {

    protected String name;

    protected int currentValue;

    private ArrayList<TimeSeries> timeSeries = new ArrayList<TimeSeries>();

    private List<TimeSeries> snapshot = new ArrayList<TimeSeries>();

    private boolean frozen = false;

    private long maxTimeDepth = 5000;

    private Range timeRange = new Range(0, (int)maxTimeDepth);
    private Range valueRange = new Range(0, 0);

    private String valueFormatString = "%d";

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMaxTimeDepth(long maxTimeDepth) {
        this.maxTimeDepth = maxTimeDepth;
    }

    public void setValueFormatString(String valueFormatString) {
        this.valueFormatString = valueFormatString;
    }

    public void setValueRange(Range valueRange) {
        this.valueRange = valueRange;
    }

    public synchronized void update(int value, long timestamp) {

        timeSeries.add(new TimeSeries(timestamp, value));
        currentValue = value;

        adjustValueRange();

    }

    private void adjustValueRange() {
        if (currentValue > valueRange.max) {
            valueRange = new Range(valueRange.min, currentValue);
        }

        if (currentValue < valueRange.min) {
            valueRange = new Range(currentValue, valueRange.max);
        }
    }

    public synchronized List<TimeSeries> getCurrentSeries() {
        if (frozen) {
            return snapshot;
        }
        else {
            snapshot = generateCurrentSeries();
            return snapshot;
        }
    }


    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }



    public synchronized List<TimeSeries> generateCurrentSeries() {

        // TODO: construct more effective algorithms to avoid re-creation of full set of time series

        List<TimeSeries> normalizedSeries = new ArrayList<TimeSeries>();
        List<TimeSeries> seriesToRemove = new ArrayList<TimeSeries>();

        long currentTs = System.currentTimeMillis();

        for (TimeSeries series : timeSeries) {

            long relativeTs = currentTs - series.time;

            if (relativeTs <= maxTimeDepth) {
                normalizedSeries.add(new TimeSeries(relativeTs, series.value));
            }
            else {
                seriesToRemove.add(series);
            }

        }

        timeSeries.removeAll(seriesToRemove);

        return normalizedSeries;

    }

    public void update(int value) {
       update(value, System.currentTimeMillis());
    }

    public Range getCurrentValuesRange() {
      return valueRange;
    }

    public Range getTimeRange() {
        return timeRange;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public String getFormattedValue(int value) {
        return String.format(valueFormatString, value);
    }

    @Override
    public String toString() {
        return "ValueWithHistory{" +
                "name='" + name + '\'' +
                ", currentValue=" + currentValue +
                ", timeSeries=" + timeSeries +
                ", maxTimeDepth=" + maxTimeDepth +
                ", timeRange=" + timeRange +
                ", valueRange=" + valueRange +
                ", valueFormatString='" + valueFormatString + '\'' +
                '}';
    }
}
