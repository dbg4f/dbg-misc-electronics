package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.graphics.DashboardWidget;
import dbg.electronics.robodrv.graphics.GraphicConstants;
import dbg.electronics.robodrv.head.MultilineReportable;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextLines implements DashboardWidget {

    public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
    private MultilineReportable multilineReportable;

    private Color[] colors = GraphicConstants.COMMON_COLOR_ARRAY;

    private int x, y;

    private int fontSize = 20;

    private Font font = new Font("Courier New", Font.BOLD, fontSize);

    public void setMultilineReportable(MultilineReportable multilineReportable) {
        this.multilineReportable = multilineReportable;
    }

    public void setColors(Color... colors) {
        this.colors = colors;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
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

        g2.setColor(DEFAULT_TEXT_COLOR);

        g2.setFont(font);

        int currentY = y;

        int i=0;

        for (String line : multilineReportable.toStringArray()) {

            g2.setColor(getRowColor(i));

            g2.drawString(line, x, currentY);

            currentY += fontSize * 1.2;

            i++;
        }

    }
}
