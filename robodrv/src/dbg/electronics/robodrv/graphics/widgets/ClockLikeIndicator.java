package dbg.electronics.robodrv.graphics.widgets;

import dbg.electronics.robodrv.graphics.DashboardWidget;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class ClockLikeIndicator implements DashboardWidget {

    public static final int ARC_START_ANGLE = -30;
    public static final int ARC_ANGLE = 180 + 60;
    public static final int ARC_WIDTH = 10;

    private int valuePercent;
    private int x, y;
    private int dimension;

    Font font = new Font("Courier New", Font.BOLD, 36);

    public int getValuePercent() {
        return valuePercent;
    }

    public void setValuePercent(int valuePercent) {
        this.valuePercent = valuePercent;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    private void drawCircleIndicator(Graphics2D g2) {

        Point center = new Point(x, y);

        g2.setColor(Color.WHITE);
        Stroke stoke = g2.getStroke();
        g2.setStroke(new BasicStroke(ARC_WIDTH));

        g2.drawArc(center.x - dimension, center.y - dimension, dimension * 2, dimension * 2, ARC_START_ANGLE, ARC_ANGLE);
        g2.setStroke(stoke);

        int arrowAngle = ARC_START_ANGLE + ARC_ANGLE - ARC_ANGLE * valuePercent / 100;

        double angleRad = ((double)arrowAngle / 180.0) * Math.PI;
        Color color = g2.getColor();
        g2.setColor(Color.MAGENTA);
        double arrowX = dimension * Math.cos(angleRad);
        double arrowY = dimension * Math.sin(angleRad);

        g2.drawLine(center.x, center.y, center.x + (int) arrowX, center.y - (int) arrowY);

        int arrowTailLen = dimension / 5;

        g2.drawLine(center.x, center.y, center.x - (int) (arrowTailLen * Math.cos(angleRad)), center.y + (int) (arrowTailLen * Math.sin(angleRad)));

        g2.setColor(color);

        String text = String.valueOf(valuePercent);

        g2.setFont(font);

        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D boundsText = font.getStringBounds(text, frc);

        g2.drawString(text, center.x - (int) boundsText.getWidth() / 2, center.y + 3 * dimension / 4);

    }


    @Override
    public void onDraw(Graphics2D g2) {
        drawCircleIndicator(g2);
    }
}
