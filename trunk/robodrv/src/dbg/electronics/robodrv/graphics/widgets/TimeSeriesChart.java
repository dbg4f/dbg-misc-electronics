package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.Range;
import dbg.electronics.robodrv.graphics.DashboardWidget;
import dbg.electronics.robodrv.graphics.GraphicConstants;
import dbg.electronics.robodrv.graphics.TimeSeries;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.head.MultilineReportable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class TimeSeriesChart implements DashboardWidget, MultilineReportable {

    public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;

    private List<ValueWithHistory> valueWithHistoryList;
    private Color[] colors = GraphicConstants.COMMON_COLOR_ARRAY;
    private Range timesRange;

    private int brickWidth = 80;
    private int brickHeight = 120;
    private int bricksHorizontal = 12;
    private int bricksVertical = 4;
    private Range drawingRangeTimes;
    private Range drawingRangeValues;

    private int x, y;

    public List<ValueWithHistory> getValueWithHistoryList() {
        return valueWithHistoryList;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public void setValueWithHistoryList(List<ValueWithHistory> valueWithHistoryList) {
        this.valueWithHistoryList = valueWithHistoryList;
    }

    public void setTimesRange(Range timesRange) {
        this.timesRange = timesRange;
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

    public void init() {
        drawingRangeTimes = new Range(x, x + bricksHorizontal * brickWidth);
        drawingRangeValues = new Range(y, y + bricksVertical * brickHeight);
    }

    private Color getRowColor(int rowIndex) {
        if (colors != null && colors.length > rowIndex) {
            return colors[rowIndex];
        }
        else {
            return DEFAULT_TEXT_COLOR;
        }
    }


    @Override
    public void onDraw(Graphics2D g2) {

        g2.setColor(Color.DARK_GRAY);

        drwawVertical(g2, drawingRangeValues);

        drawHorizontal(g2, drawingRangeTimes);

        int i=0;

        for (ValueWithHistory valueWithHistory : valueWithHistoryList) {

            drawChart(g2, valueWithHistory, i++);

        }

    }

    private void drawChart(Graphics2D g2, ValueWithHistory withHistory, int index) {

        Range valuesRange = withHistory.getCurrentValuesRange();

        List<TimeSeries> values = withHistory.getCurrentSeries();

        if (values.size() == 0) {
            return;
        }

        int cx = timesRange.remapTo((int)values.get(0).time, drawingRangeTimes);
        int cy = valuesRange.remapTo(values.get(0).value, drawingRangeValues);

        g2.setColor(getRowColor(index));

        for (TimeSeries series : values) {
            int x2 = timesRange.remapTo((int)series.time, drawingRangeTimes);
            int y2 = valuesRange.remapTo(series.value, drawingRangeValues);
            g2.drawLine(cx, cy, x2, y2);
            cx = x2;
            cy = y2;
        }
    }

    private void drawMaxMin(Graphics2D g2, Range valuesRange, ValueWithHistory withHistory) {
        g2.drawString(withHistory.getFormattedValue(valuesRange.max), x, y);
        g2.drawString(withHistory.getFormattedValue(valuesRange.min), x, y + brickHeight * bricksVertical);
    }

    private void drawCurrentValue(Graphics2D g2, ValueWithHistory withHistory) {
        g2.setColor(Color.RED);

        g2.drawString(withHistory.getName() + " " + withHistory.getFormattedValue(withHistory.getCurrentValue()), x-20, y+20);
    }

    private void drawHorizontal(Graphics2D g2, Range drawingRangeTimes) {
        for (int j=1; j<bricksVertical; j++) {
            g2.drawLine(x, y + brickHeight * j, drawingRangeTimes.max, y + brickHeight * j);
        }
    }

    private void drwawVertical(Graphics2D g2, Range drawingRangeValues) {
        for (int i=1; i<bricksHorizontal; i++) {
            g2.drawLine(x + brickWidth * i, y, x + brickWidth * i, drawingRangeValues.max);
        }
    }

    private String formatAgendaRow(ValueWithHistory withHistory) {
        Range valuesRange = withHistory.getCurrentValuesRange();
        return String.format("%s [%s-%s]",
                withHistory.getFormattedValue(withHistory.getCurrentValue()),
                withHistory.getFormattedValue(valuesRange.min),
                withHistory.getFormattedValue(valuesRange.max));
    }

    @Override
    public String[] toStringArray() {

        List<String> agendaRows = new ArrayList<String>();

        for (ValueWithHistory withHistory : valueWithHistoryList) {
            agendaRows.add(formatAgendaRow(withHistory));
        }

        return agendaRows.toArray(new String[valueWithHistoryList.size()]);
    }
}
