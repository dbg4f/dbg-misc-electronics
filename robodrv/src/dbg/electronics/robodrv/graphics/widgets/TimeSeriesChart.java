package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.Range;
import dbg.electronics.robodrv.graphics.DashboardWidget;
import dbg.electronics.robodrv.graphics.TimeSeries;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.io.IoUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TimeSeriesChart implements DashboardWidget {

    private ValueWithHistory valueWithHistory;

    private int brickWidth = 80;
    private int brickHeight = 120;
    private int bricksHorizontal = 12;
    private int bricksVertical = 4;

    private int x, y;

    public void setValueWithHistory(ValueWithHistory valueWithHistory) {
        this.valueWithHistory = valueWithHistory;
    }

    public ValueWithHistory getValueWithHistory() {
        return valueWithHistory;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setBrickWidth(int brickWidth) {
        this.brickWidth = brickWidth;
    }

    public void setBrickHeight(int brickHeight) {
        this.brickHeight = brickHeight;
    }

    public void setBricksHorizontal(int bricksHorizontal) {
        this.bricksHorizontal = bricksHorizontal;
    }

    public void setBricksVertical(int bricksVertical) {
        this.bricksVertical = bricksVertical;
    }

    @Override
    public void onDraw(Graphics2D g2) {

        g2.setColor(Color.RED);

        g2.drawString(valueWithHistory.getFormattedValue(valueWithHistory.getCurrentValue()), x-20, y+20);

        g2.setColor(Color.DARK_GRAY);

        Range drawingRangeTimes = new Range(x, x + bricksHorizontal * brickWidth);
        Range drawingRangeValues = new Range(y, y + bricksVertical * brickHeight);

        for (int i=1; i<bricksHorizontal; i++) {
            g2.drawLine(x + brickWidth * i, y, x + brickWidth * i, drawingRangeValues.max);
        }

        for (int j=1; j<bricksVertical; j++) {
            g2.drawLine(x, y + brickHeight * j, drawingRangeTimes.max, y + brickHeight * j);
        }

        Range valuesRange = valueWithHistory.getCurrentValuesRange();
        Range timesRange = valueWithHistory.getTimeRange();

        g2.drawString(valueWithHistory.getFormattedValue(valuesRange.max), x, y);
        g2.drawString(valueWithHistory.getFormattedValue(valuesRange.min), x, y + brickHeight * bricksVertical);

        List<TimeSeries> values = valueWithHistory.getCurrentSeries();

        if (values.size() == 0) {
            return;
        }

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
