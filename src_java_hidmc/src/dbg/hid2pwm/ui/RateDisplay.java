package dbg.hid2pwm.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class RateDisplay implements Runnable {

    private final RatePanel panel;


    public RateDisplay(RatePanel panel) {
        this.panel = panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public void run() {

        try {

            ServerSocket serverSocket = new ServerSocket(5555);

            while (!Thread.currentThread().isInterrupted()) {

                System.out.println("Listening");

                Socket clientSocket = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    System.out.println("inputLine = " + inputLine);

                    if ("q".equalsIgnoreCase(inputLine)) {
                        clientSocket.close();
                        break;
                    }

                    try {
                        panel.changeRate(Integer.valueOf(inputLine));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? " + SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RatePanel ratePanel = new RatePanel();
        f.add(ratePanel);
        f.pack();
        f.setVisible(true);

        Timer t = new Timer(1000, new TimerListener(ratePanel));
        t.start();

        new Thread(new RateDisplay(ratePanel)).start();

    }
}

class TimerListener implements ActionListener {

    private final RatePanel panel;

    TimerListener(RatePanel panel) {
        this.panel = panel;
    }

    public void actionPerformed(ActionEvent e) {

        //panel.changeRate(panel.getRate() + 5);

    }
}

class RatePanel extends JPanel {

    private int rate = 1;

    private static final int F_WIDTH = 250;
    private static final int F_HEIGHT = 350;

    public RatePanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public Dimension getPreferredSize() {
        return new Dimension(F_WIDTH, F_HEIGHT);
    }

    public void changeRate(int newValue) {
        if (newValue > 100) {
            rate = 100;
        } else if (newValue < 0) {
            rate = 0;
        } else {
            rate = newValue;
        }

        repaint();

    }

    public int getRate() {
        return rate;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        int rectH = 2 * F_HEIGHT / 3;

        g.drawRect(F_WIDTH / 4, F_HEIGHT / 10, F_WIDTH / 2, rectH);

        int scaledH = rectH * rate / 100;

        g.setColor(Color.GRAY);

        g.fillRect(F_WIDTH / 4, F_HEIGHT / 10 + rectH - scaledH, F_WIDTH / 2, scaledH);

        Font font = new Font("Courier New", Font.BOLD, 45);

        g.setColor(Color.BLACK);

        g.setFont(font);

        g.drawString("" + rate, F_WIDTH / 2, rectH + F_HEIGHT / 5);

    }
}