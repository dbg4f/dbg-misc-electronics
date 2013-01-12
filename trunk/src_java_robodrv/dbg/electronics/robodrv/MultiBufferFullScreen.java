package dbg.electronics.robodrv;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;

public class MultiBufferFullScreen implements InputListener {


    private DashboardPainter dashboardPainter;

    private Frame mainFrame;

    public MultiBufferFullScreen() {

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice device = env.getDefaultScreenDevice();

        init(device);

    }

    public void init(GraphicsDevice device) {
        try {

            dashboardPainter = new DashboardPainter();

            new ValueSource().launch(this);

            GraphicsConfiguration gc = device.getDefaultConfiguration();
            mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);
            device.setFullScreenWindow(mainFrame);
            Rectangle bounds = mainFrame.getBounds();
            mainFrame.createBufferStrategy(2);
            BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();

            while(!Thread.currentThread().isInterrupted()) {
                Graphics g = bufferStrategy.getDrawGraphics();
                if (!bufferStrategy.contentsLost()) {

                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, bounds.width, bounds.height);

                    dashboardPainter.paint(g);

                    bufferStrategy.show();
                    g.dispose();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            device.setFullScreenWindow(null);
        }
    }

    @Override
    public void onEvent(InputEvent event) {
        dashboardPainter.valuePercent = event.getValue();
    }

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


    static class ValueSource {

        void launch(final InputListener inputListener) {

            new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted()) {

                        for (int i = 0; i < 100; i++) {
                            inputListener.onEvent(new InputEvent(i));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 100; i > 0; i--) {
                            inputListener.onEvent(new InputEvent(i));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }).start();


        }


    }



}
