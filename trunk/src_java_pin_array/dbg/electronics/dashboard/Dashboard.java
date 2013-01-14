package dbg.electronics.dashboard;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

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

        System.setProperty("sun.java2d.opengl", "true");

        Dashboard d = new Dashboard();
        d.init();
        new ValueSource().launch(d);
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

