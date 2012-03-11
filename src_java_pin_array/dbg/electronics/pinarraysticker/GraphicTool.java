package dbg.electronics.pinarraysticker;


import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * GraphicsTool
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class GraphicTool extends JPanel implements ActionListener, Printable {
  public final static String CVS_ID = "$Id: $";

  protected JFrame jf;

    @Override
  public void paintComponent(Graphics g) {
    //g.drawLine(0, 0, 100, 100);
    
    g.setFont(new Font("Courier New", Font.PLAIN, 20));
    
    //g.drawString("AAA", 50, 50);

    //gearWheel(g);

    drawPinArray(g, 100, 100);

  }

  private void drawPinArray(Graphics g, int startx, int starty) {
        try {

          String fileSet = System.getProperty("file");

          String[] fileNames = fileSet.split(";");

          int index = 0;

          for (String file : fileNames) {

            Config c = new Config();

            c.read(file);

            g.setFont(new Font("Courier New", Font.PLAIN, c.fontSize));

            int scaleX = index % 2;

            int scaleY = index % 3;

            drawFromConfig(g, scaleX* 200 + startx, scaleY * 200 + starty, c);

            index++;

          }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
      
  }

  private void drawFromConfig(Graphics g, int startx, int starty, Config c) {
    if (c.width == 2) {

        int w = c.step + c.woffset * 2;
        int h = c.step * (c.height-1) + c.hoffset * 2;

        g.drawRect(startx, starty, w, h);

        int x1 = startx + c.woffset;
        int x2 = x1 + c.step;

        g.drawLine(x1, starty, x1, starty + h);
        g.drawLine(x2, starty, x2, starty + h);

        int hdiff = ((c.height - 1) * c.vstep - h) / 2;

        int annY = starty - hdiff;
        int annX1 = startx - c.distance*2;
        int annX2 = startx + w + c.distance*2;


        for(int i=0; i<c.height; i++) {

          //g.setColor(i%2 == 0 ? Color.GRAY : Color.BLACK);

          int yy = starty + c.hoffset + i * c.step                          ;
          g.drawLine(startx, yy, startx + w, yy);

          int annYC = annY + i * c.vstep;

          int xx1 = startx - c.distance;
          int xx2 = startx + w + c.distance;


          g.drawLine(xx1, yy, startx, yy);
          g.drawLine(xx2, yy, startx + w, yy);

          g.drawLine(annX1, annYC, xx1, yy);
          g.drawLine(annX2, annYC, xx2, yy);


          g.drawLine(annX1 - c.distanceText, annYC, annX1, annYC);
          g.drawLine(annX2 + c.distanceText, annYC, annX2, annYC);


          g.drawString(c.getText(i, 0), annX1 - c.distanceText - c.textLen, annYC + c.fontSize / 3);
          g.drawString(c.getText(c.height - i - 1, 1), annX2 + c.distanceText, annYC + c.fontSize / 3);

        }

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
    GraphicTool dp = new GraphicTool();
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
    
    if (System.getProperty("print") != null){
      dp.print();
    }
    
  }

  public void actionPerformed(ActionEvent e) {
    //System.out.println("e = " + e);
    jf.repaint();

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

    drawPinArray(g, 100, 100);

    /* tell the caller that this page is part of the printed document */
    return PAGE_EXISTS;

  }
}


