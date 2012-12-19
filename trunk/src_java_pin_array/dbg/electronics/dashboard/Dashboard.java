package dbg.electronics.dashboard;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author Dmitri Bogdel
 */
public class Dashboard extends Panel implements ChangeListener {

    private DashboardPainter painter;

    private void init() {

        painter = new DashboardPainter();

        setLayout(new BorderLayout());

        JPanel p = new JPanel();
        //p.add(new JLabel("Temperature:"));
        add(p);
        p.add("Center", painter);

        UIManager.put("swing.boldMetal", Boolean.FALSE);

        createFrame();

    }

    private void createFrame() {
        JFrame f = new JFrame("Dashboard");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add("Center", this);
        f.pack();
        f.setVisible(true);
    }

    public void stateChanged(ChangeEvent e) {

        if (e.getSource() != null && e.getSource() instanceof Integer) {

            painter.valuePercent = (Integer) e.getSource();

            repaint();

        }

    }

    public static void main(String[] args) {

        //System.setProperty("sun.java2d.opengl", "true");

        Dashboard d = new Dashboard();
        d.init();
        new ValueSource().launch(d);
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

            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());


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

        void launch(final ChangeListener changeListener) {

            new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted()) {

                        for (int i = 0; i < 100; i++) {
                            changeListener.stateChanged(new ChangeEvent(i));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 100; i > 0; i--) {
                            changeListener.stateChanged(new ChangeEvent(i));
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

