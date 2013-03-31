package dbg.electronics.robodrv.graphics;


import dbg.electronics.robodrv.Range;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ValueWithHistory {

    private int currentValue;

    private ArrayList<TimeSeries> timeSeries = new ArrayList<TimeSeries>();

    private long maxTimeDepth = 1000*60; // 1 minute is max depth

    private Range timeRange = new Range(0, (int)maxTimeDepth);
    private Range valueRange = new Range(0, 0);

    private String valueFormatString = "%d";

    public void setMaxTimeDepth(long maxTimeDepth) {
        this.maxTimeDepth = maxTimeDepth;
    }

    public void setValueFormatString(String valueFormatString) {
        this.valueFormatString = valueFormatString;
    }

    public synchronized void update(int value, long timestamp) {

        timeSeries.add(new TimeSeries(timestamp, value));
        currentValue = value;

        if (currentValue > valueRange.max) {
            valueRange = new Range(valueRange.min, currentValue);
        }

        if (currentValue < valueRange.min) {
            valueRange = new Range(currentValue, valueRange.max);
        }

    }

    public synchronized List<TimeSeries> getCurrentSeries() {

        // TODO: construct more effective algorithms to avoid re-creation of full set of time series

        List<TimeSeries> normalizedSeries = new ArrayList<TimeSeries>();
        List<TimeSeries> seriesToRemove = new ArrayList<TimeSeries>();

        long currentTs = System.currentTimeMillis();

        normalizedSeries.add(new TimeSeries(0, currentValue));

        for (TimeSeries series : timeSeries) {

            long relativeTs = currentTs - series.time;

            if (relativeTs <= maxTimeDepth) {
                normalizedSeries.add(new TimeSeries(relativeTs, series.value));
            }
            else {
                seriesToRemove.add(series);
            }

        }

        // contains only one entry (ts = 0), need to add tail entry
        if (normalizedSeries.size() == 1) {
            normalizedSeries.add(new TimeSeries(maxTimeDepth, currentValue));
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

    public String getFormattedValue(int value) {
        return String.format(valueFormatString, value);
    }


}
