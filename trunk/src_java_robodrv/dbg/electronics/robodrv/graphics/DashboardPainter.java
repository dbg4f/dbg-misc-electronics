package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.graphics.widgets.ClockLikeIndicator;

import java.awt.*;
import java.util.Map;

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

    DashboardData dashboardData = new DashboardData();

    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        Dimension size = getSize();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ClockLikeIndicator indicator = new ClockLikeIndicator();

        indicator.setValuePercent(dashboardData.getTestValuePercent());
        indicator.setX(200);
        indicator.setY(300);
        indicator.setDimension(100);

        //drawCircleIndicator(g2);

        indicator.onDraw(g2);

        drawHidControls(g2);


    }

    private void drawHidControls(Graphics2D g2) {

        Font font = new Font("Courier New", Font.BOLD, 20);
        g2.setFont(font);


        int x = 100;
        int y = 400;

        for (Map.Entry<String, String> entry : dashboardData.getJsIndicators().entrySet()) {

            g2.drawString(entry.getKey() + "   " + entry.getValue(), x, y);

            y+=25;

        }


    }

}
