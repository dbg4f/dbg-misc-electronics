package j3d.drawing;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 14.02.2010
 * Time: 11:19:27
 * To change this template use File | Settings | File Templates.
 */
public class Glyphs extends JPanel {


  static class GPoint {
    int x;
    int y;

    GPoint(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  List<GPoint> points = new ArrayList<GPoint>();

  Map<Character, List<GPoint>> GLYPHS = new LinkedHashMap<Character, List<GPoint>>() {
    {
      put('\u0410', new ArrayList<GPoint>() {
        {
          add(new GPoint(5, 10));
          add(new GPoint(5, 10));
        }
      });
    }
  };


  public Glyphs() {
    points = offScreen();
    System.out.println("Raster created");
    JFrame frame = new JFrame();
    frame.getContentPane().add(this);
   //frame.addKeyListener(this);
    frame.setBackground(Color.white);
    frame.setSize(600, 600);
    frame.setLocation(400, 300);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


  }

  @Override
  protected void paintComponent(Graphics g) {
    //super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.

    //g.setFont(new Font("Courier New", Font.PLAIN, 140));
    //g.setFont(new Font("Verdana", Font.PLAIN, 540));
    //g.setFont(new Font("Modern", Font.PLAIN, 540));

    //g.drawString("\u0410\u0411\u0412", 100, 500);


    //g.setColor(Color.white);
    for (int i=0; i<10000; i++) {
      if (i%10 != 0) {
        //g.drawLine(0, i, 10000, i);
        //g.drawLine(i, 0, i, 10000);
      }
    }

    for (GPoint p : points) {
      g.drawLine(p.x, p.y, p.x,  p.y);
    }

    //g.drawLine();


  }


  private static boolean isBlack(BufferedImage dest, int x, int y) {
    int[] c = new int[3];

    dest.getData().getPixel(x, y, c);

    DataBuffer buf = dest.getData().getDataBuffer();

    return buf.getElem(x*500 + y) == -1;

    //return c[0] == 255 && c[1] == 255 && c[2] == 255;
  }

  private static int[][] checkBuf(BufferedImage dest, int size) {
    DataBuffer buf = dest.getData().getDataBuffer();

    int banks = buf.getNumBanks();

    System.out.println("banks = " + banks);


    int offset = buf.getOffset();

    System.out.println("offset = " + offset);

    //int size = buf.getSize();

    //System.out.println("size = " + size);

    int[][] m = new int[size][];

    for (int i=0; i<size; i++) {
      m[i] = new int[size];
      for (int j=0; j<size; j++) {
        m[i][j] = buf.getElem(i*size + j);
      }
    }

    return m;
  }

  private static int[] centers(int[] line) {
    List<Integer> c = new ArrayList<Integer>();
    for (int i=1; i<line.length; i++) {
      if (line[i-1] != -1 && line[i] == -1) {
        for (int k=i; k<line.length; k++) {
          if (line[k-1] == -1 && line[k] != -1) {
            c.add((k+i)/2);
          }
        }
      }
    }

    int[] r = new int[c.size()];
    int i=0;
    for (int e : c) {
      r[i++] = e;
    }
    return r;
    //return c.toArray(new int[c.size()]);
  }

  private static List<GPoint> offScreen() {


    List<GPoint> points = new ArrayList<GPoint>();
    int type = BufferedImage.TYPE_INT_RGB;  // other options
    int size = 500;
    BufferedImage dest = new BufferedImage(size, size, type);
    Graphics2D g2 = dest.createGraphics();
    g2.setBackground(Color.WHITE);
    //paintComponent(g2);

    //g2.drawLine(10, 10, 20, 20);

    g2.setFont(new Font("Verdana", Font.PLAIN, 300));

    g2.drawString("\u0410", 10, 340);

    int[][] m = checkBuf(dest, size);

    for (int i=0; i<size; i++) {

      int[] c = centers(m[i]);

      //System.out.println("c.length = " + c.length);

      for (int cc : c) {
        points.add(new GPoint(cc, i));  
      }




      //for (int j=0; j<size; j++) {
        //if (/*isBlack(dest, i, j)*/m[i][j] == -1) {
        //}
      //}
      //System.out.println("i = " + i);
    }


    g2.dispose();

    return points;
  }

  public static void main(String[] args) {
    new Glyphs();



  }
                                                
}
