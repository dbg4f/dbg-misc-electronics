package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.graphics.DashboardWidget;

import java.awt.*;

public class TextLines implements DashboardWidget {

    private String[] lines;

    private int x, y;

    private int fontSize = 20;

    private Font font = new Font("Courier New", Font.BOLD, fontSize);

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    @Override
    public void onDraw(Graphics2D g2) {

        g2.setFont(font);

        int currentY = y;

        for (String line : lines) {

            g2.drawString(line, x, currentY);

            currentY += fontSize/4;

        }

    }
}
