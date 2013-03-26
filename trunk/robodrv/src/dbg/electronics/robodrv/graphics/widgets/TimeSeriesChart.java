package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.Range;
import dbg.electronics.robodrv.graphics.DashboardWidget;
import dbg.electronics.robodrv.graphics.TimeSeries;
import dbg.electronics.robodrv.io.IoUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TimeSeriesChart implements DashboardWidget {

    private List<TimeSeries> values = new ArrayList<TimeSeries>();

    private int brickWidth = 80;
    private int brickHeight = 120;
    private int bricksHorizontal = 12;
    private int bricksVertical = 4;

    private Range timesRange;
    private Range valuesRange;

    private int x, y;
    
    public void setValues(List<TimeSeries> values) {
        this.values = values;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void init() throws IOException {

        long timeMax = Integer.MIN_VALUE;
        long timeMin = Integer.MAX_VALUE;
        int valuesMax = Integer.MIN_VALUE;
        int valuesMin = Integer.MAX_VALUE;

        for (String line : IoUtils.readLines("test-set.txt")) {

            TimeSeries series = new TimeSeries(line);
            values.add(series);

            if (series.time > timeMax) {
                timeMax = series.time;
            }

            if (series.time < timeMin) {
                timeMin = series.time;
            }

            if (series.value > valuesMax) {
                valuesMax = series.value;
            }

            if (series.value < valuesMin) {
                valuesMin = series.value;
            }
        }

        timesRange = new Range((int)timeMin, (int)timeMax);
        valuesRange = new Range(valuesMin, valuesMax);

    }

    @Override
    public void onDraw(Graphics2D g2) {

        g2.setColor(Color.LIGHT_GRAY);

        for (int i=1; i<bricksHorizontal; i++) {
            g2.drawLine(x + brickWidth * i, y, x + brickWidth * i, y + bricksVertical * brickHeight);
        }

        for (int j=1; j<bricksVertical; j++) {
            g2.drawLine(x, y + brickHeight * j, x + bricksHorizontal * brickWidth, y + brickHeight * j);
        }

        g2.drawString(String.valueOf(valuesRange.max), x, y);
        g2.drawString(String.valueOf(valuesRange.min), x, y + brickHeight * bricksVertical);

        Range drawingRangeTimes = new Range(x, x + bricksHorizontal * brickWidth);
        Range drawingRangeValues = new Range(y, y + bricksVertical * brickHeight);

        int cx = timesRange.remapTo((int)values.get(0).time, drawingRangeTimes);
        int cy = valuesRange.remapTo(values.get(0).value, drawingRangeValues);

        g2.setColor(Color.BLUE);

        for (TimeSeries series : values) {
            int x2 = timesRange.remapTo((int)series.time, drawingRangeTimes);
            int y2 = valuesRange.remapTo(series.value, drawingRangeValues);
            g2.drawLine(cx, cy, x2, y2);
            cx = x2;
            cy = y2;
        }


    }
    
    


}
