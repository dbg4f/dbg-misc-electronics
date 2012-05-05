package dbg.electronics.pinarraysticker;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 5/5/12
 * Time: 7:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class GrayCode extends JPanel {

    protected JFrame jf;

    public static String[] GRAY4 = {
            "0000",
            "0001",
            "0011",
            "0010",
            "0110",
            "0111",
            "0101",
            "0100",
            "1100",
            "1101",
            "1111",
            "1110",
            "1010",
            "1011",
            "1001",
            "1000",
    };

    public static void main(String args[]) {
        GrayCode dp = new GrayCode();
        dp.jf = new JFrame();
        Container c = dp.jf.getContentPane();
        dp.jf.setBackground(Color.white);
        c.add(dp);
        dp.jf.setSize(600, 600);
        dp.jf.setLocation(400, 300);
        dp.jf.setVisible(true);
        dp.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    @Override
    public void paintComponent(Graphics g) {
        //g.drawLine(0, 0, 100, 100);

        //g.setFont(new Font("Courier New", Font.PLAIN, 20));

        //g.drawString("AAA", 50, 50);


        int len = Scale.MM(3);
        int width = Scale.MM(2.5);

        int xs = 100;
        int ys = 100;

        g.drawRect(xs, ys, len * GRAY4.length, width * 4);

        for (int i = 0; i<GRAY4.length; i++) {
            for (int j=0; j<4; j++) {
                if (GRAY4[i].charAt(j) == '1') {
                    g.fillRect(xs + i*len, ys + j*width, len, width);
                }
                else {
                    //g.drawRect(xs + i*len, ys + j*width, len, width);
                }

            }
        }

        //gearWheel(g);

        //drawPinArray(g, 100, 100);

    }

}
