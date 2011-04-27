package graph;

import graph.shapes.GearPair;
import graph.shapes.Point;
import graph.shapes.primitives.GraphicPrimitive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.HashSet;
import java.util.Set;

/**
 * GraphicsTool
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class GraphicsTool extends JPanel implements ActionListener, Printable {
  public final static String CVS_ID = "$Id: $";

  private double currentAngle1 = 0.0;
  private double currentAngle2 = 0.0;
  protected JFrame jf;
  private boolean reportFlag = true;

  private boolean adjustMode = false;


  public Set<String> skip = new HashSet<String>() {
    {
      add("GEAR_OUTLINE");
    }
  };

  public void paintComponent(Graphics g) {
    //g.drawLine(0, 0, 100, 100);
    //g.drawString("AAA", 50, 50);

    //gearWheel(g);

    gearWheel3(g, currentAngle1, currentAngle2);

  }

  private void drawReport(Graphics g, Point start, String[] rep) {
    int textH = 10;
    int textGap = 7;
    for (int i = 0; i < rep.length; i++) {
      g.drawString(rep[i], start.x, start.y + i * (textH + textGap));
    }
  }

  private void gearWheel3(Graphics g, double angle1, double angle2) {
    GearPair p = new GearPair(new Point(200, 200), 16, 32, 0.9, 0.7, 100, 200, 32, 32);
    p.align(angle1 + 0.0, angle2 + 0.14);
    for (GraphicPrimitive pp : p.render()) {
      pp.draw(g);
    }

    drawReport(g, new Point(100, 400), p.report().split("\n"));

    if (reportFlag) {
      System.out.println(p.report());
      reportFlag = false;
    }

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

  public static void main(String args[]) {
    GraphicsTool dp = new GraphicsTool();
    Timer timer = new Timer(1000, dp);
    timer.start();
    dp.jf = new JFrame();
    Container c = dp.jf.getContentPane();
    dp.jf.setBackground(Color.white);
    c.add(dp);
    dp.jf.setSize(600, 600);
    dp.jf.setLocation(400, 300);
    dp.jf.setVisible(true);
    dp.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //dp.print();
  }

  public void actionPerformed(ActionEvent e) {
    //System.out.println("e = " + e);
    jf.repaint();

    if (adjustMode) {
      currentAngle2 += 0.01;
      System.out.println("currentAngle2 = " + currentAngle2);
    }
    else {
      currentAngle1 += 0.01;
      currentAngle2 -= 0.005;
    }

  }

  public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
    if (pageIndex > 0) { /* We have only one page, and 'page' is zero-based */
      return NO_SUCH_PAGE;
    }

    /* User (0,0) is typically outside the imageable area, so we must
     * translate by the X and Y values in the PageFormat to avoid clipping
     */
    Graphics2D g2d = (Graphics2D)g;
    g2d.translate(pf.getImageableX(), pf.getImageableY());

    /* Now we perform our rendering */

    gearWheel3(g, currentAngle1, currentAngle2);

    /* tell the caller that this page is part of the printed document */
    return PAGE_EXISTS;

  }
}


