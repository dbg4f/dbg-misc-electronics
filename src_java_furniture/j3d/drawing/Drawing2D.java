package j3d.drawing;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.*;

/**
 * Drawing2D
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class Drawing2D extends JPanel implements KeyListener {

  private final static int DRAW_SCALE = 3;
  private final static int START_X = 50;
  private final static int START_Y = 100;

  List<Figure> figures = new ArrayList<Figure>();

  Set<String> markedCorners = new HashSet<String>();

  char currentSelectorValue = '1';

  class Figure {

    final String name;
    final int width;
    final int height;
    final int x;
    final int y;
    final int xSizeOffs;
    final int ySizeOffs;
    final String selector;

    Figure(String line) {
      String[] components = line.split(",");
      name      = components[0].trim();
      width     = Integer.parseInt(components[1].trim());
      height    = Integer.parseInt(components[2].trim());
      x         = Integer.parseInt(components[3].trim());
      y         = Integer.parseInt(components[4].trim());
      xSizeOffs = Integer.parseInt(components[5].trim());
      ySizeOffs = Integer.parseInt(components[6].trim());
      selector  = components[7].trim();
    }

    private void drawLineWithArrows(int x1, int y1, int x2, int y2, ScaledGraphics g) {
      g.drawLine(x1, y1, x2, y2);
      if (y1 == y2) {
        g.drawLine(x1, y1, x1 + 6 * DRAW_SCALE, y1 + 2 * DRAW_SCALE);
        g.drawLine(x1, y1, x1 + 6 * DRAW_SCALE, y1 - 2 * DRAW_SCALE);
        g.drawLine(x2, y2, x2 - 6 * DRAW_SCALE, y2 + 2 * DRAW_SCALE);
        g.drawLine(x2, y2, x2 - 6 * DRAW_SCALE, y2 - 2 * DRAW_SCALE);
      }
      else {
        g.drawLine(x1, y1, x1 + 2 * DRAW_SCALE, y1 + 6 * DRAW_SCALE);
        g.drawLine(x1, y1, x1 - 2 * DRAW_SCALE, y1 + 6 * DRAW_SCALE);
        g.drawLine(x2, y2, x2 + 2 * DRAW_SCALE, y2 - 6 * DRAW_SCALE);
        g.drawLine(x2, y2, x2 - 2 * DRAW_SCALE, y2 - 6 * DRAW_SCALE);
      }
    }

    class ScaledGraphics {
      
      private Graphics g;
      private int scale;

      ScaledGraphics(Graphics g, int scale) {
        this.g = g;
        this.scale = scale;
      }

      void drawLine(int i, int i1, int i2, int i3) {
        g.drawLine(i/scale + START_X, i1/scale + START_Y, i2/scale + START_X, i3/scale + START_Y);
      }

      void drawThickLine(int i, int i1, int i2, int i3) {
        g.drawLine(i/scale + START_X + 1, i1/scale + START_Y + 1, i2/scale + START_X + 1, i3/scale + START_Y + 1);
        g.drawLine(i/scale + START_X,     i1/scale + START_Y    , i2/scale + START_X,     i3/scale + START_Y);
        g.drawLine(i/scale + START_X - 1, i1/scale + START_Y - 1, i2/scale + START_X - 1, i3/scale + START_Y - 1);
      }

      void drawString(String text, int x, int y) {
        g.drawString(text, x/scale + START_X, y/scale + START_Y);
      }

      void setColor(Color c) {
        g.setColor(Color.BLACK);
      }

    }

    private void drawRect(ScaledGraphics g, int x1, int y1, int width, int height) {

      g.drawThickLine(x1,         y1,           x1 + width, y1);
      g.drawThickLine(x1 + width, y1,           x1 + width, y1 + height);
      g.drawThickLine(x1 + width, y1 + height,  x1,         y1 + height);
      g.drawThickLine(x1,         y1 + height,  x1,         y1);
      markCorners(g, x1, y1, width, height);
    }

    private void markCorners(ScaledGraphics g, int x1, int y1, int width, int height) {
      checkAndMarkCorner(g, x1, y1);
      checkAndMarkCorner(g, x1+width, y1);
      checkAndMarkCorner(g, x1+width, y1+height);
      checkAndMarkCorner(g, x1, y1+height);
    }

    private void checkAndMarkCorner(ScaledGraphics g, int x, int y) {
      String mark = x + "," + y;
      if (!markedCorners.contains(mark)) {
        g.setColor(Color.CYAN);
        //g.drawString(mark, x + 2 * DRAW_SCALE, y + 2 * DRAW_SCALE);
        markedCorners.add(mark);
      }
    }

    private void drawSizes(ScaledGraphics g) {
      g.setColor(Color.RED);

      if (xSizeOffs > 0) {
        g.drawString(String.valueOf(width), x + width / 2, y - xSizeOffs - 2 * DRAW_SCALE);
        g.drawLine(x, y, x, y - xSizeOffs - 4 * DRAW_SCALE);
        g.drawLine(x + width,  y, x + width, y - xSizeOffs - 4 * DRAW_SCALE);
        drawLineWithArrows(x, y - xSizeOffs, x + width, y - xSizeOffs, g);
      }
      else if (xSizeOffs < 0) {
        g.drawString(String.valueOf(width), x + width / 2, y + height + (-xSizeOffs) - 2 * DRAW_SCALE);
        g.drawLine(x, y + height, x, y + height - xSizeOffs + 4 * DRAW_SCALE);
        g.drawLine(x + width,  y + height, x + width, y + height - xSizeOffs + 4 * DRAW_SCALE);
        drawLineWithArrows(x, y + height - xSizeOffs, x + width, y + height - xSizeOffs, g);
      }

      if (ySizeOffs > 0) {
        g.drawString(String.valueOf(height), x + width + ySizeOffs + 2 * DRAW_SCALE, y + height / 2);
        g.drawLine(x + width, y, x + width + ySizeOffs + 4 * DRAW_SCALE, y);
        g.drawLine(x + width, y + height, x + width + ySizeOffs + 4 * DRAW_SCALE, y + height);
        drawLineWithArrows(x + width + ySizeOffs, y, x + width + ySizeOffs, y + height, g);
      }
      else if (ySizeOffs < 0) {
        g.drawString(String.valueOf(height), x + ySizeOffs + 2 * DRAW_SCALE, y + height / 2);
        g.drawLine(x, y, x + ySizeOffs - 4 * DRAW_SCALE, y);
        g.drawLine(x, y + height, x + ySizeOffs - 4 * DRAW_SCALE, y + height);
        drawLineWithArrows(x + ySizeOffs, y, x + ySizeOffs, y + height, g);
      }

    }

    public void draw(Graphics g1) {

      markedCorners.clear();

      g1.setFont(new Font("Courier New", Font.PLAIN, 14));

      ScaledGraphics g = new ScaledGraphics(g1, DRAW_SCALE);

      g1.drawString("" + currentSelectorValue, 20, 20);

      g.setColor(Color.BLACK);
      drawRect(g, x, y, width, height);

      g.setColor(Color.BLUE);
      g.drawString(name, x + width / 5, y + height / 2);

      drawSizes(g);

    }

  }

  public Drawing2D() {
    loadFigures();
    JFrame frame = new JFrame();
    frame.getContentPane().add(this);
    frame.addKeyListener(this);
    frame.setBackground(Color.white);
    frame.setSize(600, 600);
    frame.setLocation(400, 300);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
  }

  private List<Figure> loadFigures() {

    String fileName = "figures.csv";

    figures.clear();
    
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      String line;
      while((line = reader.readLine()) != null) {
        line = line.replaceAll(";", ",");
        if (line.contains(",")) {
          figures.add(new Figure(line));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return figures;
  }

  public void keyTyped(KeyEvent e) {    
  }

  public void keyPressed(KeyEvent e) {

    System.out.println("e = " + e);

    if (e.getKeyCode() == 116) {
      loadFigures();
      repaint();
    }
    else if (e.getKeyChar() >= '1' && e.getKeyChar() <= '9'){
      currentSelectorValue = e.getKeyChar();
      repaint();
    }
    else if (e.getKeyChar() == 's') {
      save();
    }

  }

  private void save() {
    File file = new File("AAA-" + currentSelectorValue + ".bmp");
    try {

      int type = BufferedImage.TYPE_INT_RGB;  // other options
      BufferedImage dest = new BufferedImage(getWidth(), getHeight(), type);
      Graphics2D g2 = dest.createGraphics();
      g2.setBackground(Color.WHITE);
      paintComponent(g2);
      g2.dispose();
      ImageIO.write(dest, "bmp", file);  // ignore returned boolean

    }
    catch (IOException e) {
      System.out.println("Write error for " + file.getPath() + ": " + e.getMessage());
    }
  }

  public void keyReleased(KeyEvent e) {
  }

  @Override
  protected void paintComponent(Graphics g) {

    g.clearRect(0, 0, 3000, 3000);
        
    for (Figure f : figures) {
      if (f.selector.charAt(0) == currentSelectorValue) {
        f.draw(g);
      }
    }


  }

  


  public static void main(String[] args) {
    new Drawing2D();
  }

}
