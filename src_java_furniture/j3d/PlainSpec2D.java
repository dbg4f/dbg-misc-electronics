package j3d;

import j3d.builders.RoomBuilder;
import j3d.primitives.Detail;
import j3d.primitives.DetailType;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.IOException;

/**
 * PlainSpec
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class PlainSpec2D extends JFrame implements ActionListener, Printable {

  private JFrame frame;

  public void actionPerformed(ActionEvent e) {
  }

  public void draw(Graphics g) {

    List<Detail> details = new RoomBuilder().createDetails();

    PlainSpec ps = new PlainSpec();

    Map<DetailType, List<Detail>> detByType = ps.getDetailsByType(details);

    Map<String, List<Detail>> detBySize = ps.groupBySize(details, detByType);

    drawDetails(detBySize, g);

    //g.drawLine(0, 0, 100, 100);
  }

  protected void drawDetails(Map<String, List<Detail>> detBySize, Graphics g) {

    int x = 20;

    for (Map.Entry<String, List<Detail>> entry : detBySize.entrySet()) {
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




  public void paint1(Graphics g) {
    //System.out.println("g = " + g);
    //g.setColor(Color.LIGHT_GRAY);
    //g.drawLine(0, 0, 100, 100);
    //g.drawString("AAA", 50, 50);

    draw(g);
  }


  @Override
  public void paintComponents(Graphics g) {
    //super.paintComponents(g);    //To change body of overridden methods use File | Settings | File Templates.
    draw(g);
  }

  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
    if (pageIndex > 0) {
      return NO_SUCH_PAGE;
    }

    Graphics2D g2d = (Graphics2D)graphics;
    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

    return PAGE_EXISTS;
  }

  public void print() {
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(this);
    boolean ok = job.printDialog();
    if (ok) {
      try {
        job.print();
      }
      catch (PrinterException ex) {
        /* The job did not successfully complete */
        ex.printStackTrace();
      }
    }

  }



  public static void main(String[] args) throws IOException {


    PlainSpec2D dp = new PlainSpec2D();


    //dp.frame = new JFrame("spec");
    Container c = dp./*frame.*/getContentPane();
    
    dp/*.frame*/.setBackground(Color.white);
    //c.add(dp);
//    dp.frame.pack();

    JLabel label = new JLabel("Label");
    label.setPreferredSize(new Dimension(3000,1000));
    JScrollPane jScrollPane = new JScrollPane();
    jScrollPane.setViewportView(label);
    dp/*.frame*/.add(jScrollPane, BorderLayout.CENTER);
    dp/*.frame*/.setSize(1600, 1600);
    dp/*.frame*/.setLocation(40, 30);
    dp/*.frame*/.setVisible(true);
    dp/*.frame*/.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    dp/*.frame*/.repaint();
    //dp.frame.p
    //dp.print();

/*
   JFrame frame = new JFrame("Tabbed Pane Sample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    JLabel label = new JLabel("Label");
    label.setPreferredSize(new Dimension(1000,1000));
    JScrollPane jScrollPane = new JScrollPane();
    jScrollPane.setViewportView(label);
    frame.add(jScrollPane, BorderLayout.CENTER);
    frame.setSize(400, 150);
    frame.setVisible(true);

     */

  }

}