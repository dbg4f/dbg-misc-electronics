package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.graphics.DashboardWidget;
import dbg.electronics.robodrv.head.MultilineReportable;

import java.awt.*;

public class TextLines implements DashboardWidget {

    private MultilineReportable multilineReportable;

    private int x, y;

    private int fontSize = 20;

    private Font font = new Font("Courier New", Font.BOLD, fontSize);

    public void setMultilineReportable(MultilineReportable multilineReportable) {
        this.multilineReportable = multilineReportable;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void onDraw(Graphics2D g2) {

        g2.setFont(font);

        int currentY = y;

        for (String line : multilineReportable.toStringArray()) {

            g2.drawString(line, x, currentY);

            currentY += fontSize * 1.2;

        }

    }
}
