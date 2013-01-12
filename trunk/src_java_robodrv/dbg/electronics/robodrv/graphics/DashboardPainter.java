package dbg.electronics.robodrv.graphics;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
* Created with IntelliJ IDEA.
* User: dmitri
* Date: 1/12/13
* Time: 9:47 PM
* To change this template use File | Settings | File Templates.
*/
class DashboardPainter extends Component {

    public static final int WIDTH = 450;
    public static final int HEIGHT = 325;

    public int valuePercent;

    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        Dimension size = getSize();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //g.setColor(Color.BLACK);
        //g.fillRect(0, 0, getWidth(), getHeight());


        g2.setColor(Color.WHITE);
        Stroke stoke = g2.getStroke();
        g2.setStroke(new BasicStroke(10));

        int startAngle = -30;
        int arcAngle = 180 + 60;

        g2.drawArc(100, 100, 200, 200, startAngle, arcAngle);
        g2.setStroke(stoke);


        //g2.drawString("123", 100, 100);


        int angle = startAngle + arcAngle - arcAngle * valuePercent / 100;

        double angleRad = ((double)angle / 180.0) * Math.PI;
        Color color = g2.getColor();
        g2.setColor(Color.MAGENTA);
        double arrowX = 100 * Math.cos(angleRad);
        double arrowY = 100 * Math.sin(angleRad);

        g2.drawLine(200, 200, 200 + (int) arrowX, 200 - (int) arrowY);
        g2.drawLine(200, 200, 200 - (int) (20 * Math.cos(angleRad)), 200 + (int) (20 * Math.sin(angleRad)));
        g2.setColor(color);

        String text = String.valueOf(valuePercent);

        //System.out.println("angle = " + angle + " " + angleRad + " x=" + arrowX + " y=" + arrowY);

        Font font = new Font("Courier New", Font.BOLD, 36);
        g2.setFont(font);

        FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
        Rectangle2D boundsText = font.getStringBounds(text, frc);

        g2.drawString(text, 200 - (int) boundsText.getWidth() / 2, 280);


    }


}
