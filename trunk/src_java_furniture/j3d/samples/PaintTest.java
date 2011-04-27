package j3d.samples;

import j3d.primitives.Detail;
import j3d.primitives.DetailType;
import j3d.primitives.PlacedDetail;
import j3d.builders.RoomBuilder;
import j3d.PlainSpec;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class PaintTest extends JFrame implements KeyListener {

    public PaintTest()
    {
        setupScreen();
        addKeyListener(this);
    }

    private void setupScreen()
    {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BackgroundPanel bgPanel = new BackgroundPanel();
        bgPanel.setPreferredSize(new Dimension(1000, 3000));
        bgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JScrollPane contentPane = new JScrollPane(bgPanel);
        contentPane.setPreferredSize(new Dimension(250, 250));
        this.getContentPane().add(contentPane);
        //this.getContentPane().add(bgPanel);
        this.pack();
        this.show();
    }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    //To change body of implemented methods use File | Settings | File Templates.
    System.out.println("e = " + e);
    repaint();
  }

  private class BackgroundPanel extends JPanel
    {

      public void draw(Graphics g) {

        List<Detail> details = new RoomBuilder().createDetails();

        PlainSpec ps = new PlainSpec();

        Map<DetailType, java.util.List<Detail>> detByType = ps.getDetailsByType(details);

        Map<String, java.util.List<Detail>> detBySize = ps.groupBySize(details, detByType);

        drawDetails(detBySize, g);

        //g.drawLine(0, 0, 100, 100);
      }

      protected void drawByType(Graphics g) {
        java.util.List<Detail> details = new RoomBuilder().createDetails();

        PlainSpec ps = new PlainSpec();

        Map<DetailType, java.util.List<Detail>> detByType = ps.getDetailsByType(details);

        System.out.println("detByType = " + detByType);

        List<PlacedDetail> placedDetails = ps.placeDetails(detByType);

        for (PlacedDetail placedDetail : placedDetails) {
          placedDetail.draw(g);
          System.out.println("placedDetail = " + placedDetail);
        }

      }


      protected void drawDetails(Map<String, java.util.List<Detail>> detBySize, Graphics g) {

        int x = 20;

        for (Map.Entry<String, java.util.List<Detail>> entry : detBySize.entrySet()) {
          String s = entry.getKey() + " = " + entry.getValue().size() + "  ";


          for (Detail d : entry.getValue()) {
            s += (d.getDetailType() + "/" + d.getName() + ", ");
          }

          if (entry.getValue().size() != 0) {
            Detail d = entry.getValue().get(0);
            float[] sizes = d.getSize();
            float s1 = Math.abs(sizes[0]) * 1000f;
            float s2 = Math.abs(sizes[1]) * 1000f;
            int d1 = Math.round(s1) > Math.round(s2) ? Math.round(s1) : Math.round(s2);
            int d2 = Math.round(s1) > Math.round(s2) ? Math.round(s2) : Math.round(s1);
            int y = 10;
            for (Detail dd : entry.getValue()) {
              g.drawRect(y+1-1, x+10, d1/10, d2/10);
              y += (d1/10 + 10);
            }

            x += (d2/10 + 30);
          }

          g.drawString(s, 10, x+1-1);
          x += 10;
        }


        g.drawRect(10 , x+10,  280, 207);
        g.drawRect(300 , x+10, 280, 207);
        g.drawRect(590 , x+10, 280, 207);

      }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            //draw(g);
          drawByType(g);
        }
    }

    public static void main(String[] args)
    {
        PaintTest pt = new PaintTest();
    }
}