package j3d;

import j3d.primitives.Detail;
import j3d.primitives.OrthogonalPlate;
import j3d.builders.DetailsViewFilter;
import j3d.builders.RoomBuilder;

import javax.swing.*;
import javax.vecmath.Vector2f;
import javax.vecmath.Point2f;
import javax.vecmath.Point2i;
import javax.vecmath.Color3f;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ProjectionDisplayer extends JFrame implements ActionListener, KeyListener {

  private List<Detail> details;


  public ProjectionDisplayer(List<Detail> details) throws HeadlessException {
    this.details = details;

    this.setTitle("Snapshot");

    // Create and initialize menu bar
    this.setJMenuBar(createMenuBar());

    // Create scroll pane, and embedded image panel
    ImagePanel imagePanel = new ImagePanel();
    JScrollPane scrollPane = new JScrollPane(imagePanel);
    scrollPane.getViewport().setPreferredSize(new Dimension(1100, 1000));

    // Add scroll pane to the frame and make it visible
    this.getContentPane().add(scrollPane);
    this.pack();
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    addKeyListener(this);
  }

  public static void main(String[] args) {
    new ProjectionDisplayer(new DetailsViewFilter().filter(new RoomBuilder().createDetails()));
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == 116) {
      details = new DetailsViewFilter().filter(new RoomBuilder().createDetails());
      repaint();
    }
  }

  public void keyReleased(KeyEvent e) {
  }

  private class ImagePanel extends JPanel {

    public void paint(Graphics g) {

      Point2i maxXY = new Point2i();
      Point2i minXY = new Point2i();
      Point2i maxXZ = new Point2i();
      Point2i minXZ = new Point2i();
      Point2i maxYZ = new Point2i();
      Point2i minYZ = new Point2i();

      Map<String, Detail> detailMap = new LinkedHashMap<String, Detail>();

      for (Detail d : details) {
        detailMap.put(d.getName(), d);
      }


      Map<String, Point2i[]> explXY = explodeDetails(OrthogonalPlate.XY);
      calcBoundingRect(explXY.values(), minXY, maxXY);
      //System.out.println("XY = " + minXY + " " + maxXY + " " + (maxXY.getX() - minXY.getX()) + "x" +  (maxXY.getY() - minXY.getY()));

      Map<String, Point2i[]> explXZ = explodeDetails(OrthogonalPlate.XZ);
      calcBoundingRect(explXZ.values(), minXZ, maxXZ);
      //System.out.println("XZ = " + minXZ + " " + maxXZ + " " + (maxXZ.getX() - minXZ.getX()) + "x" +  (maxXZ.getY() - minXZ.getY()));

      Map<String, Point2i[]> explYZ = explodeDetails(OrthogonalPlate.YZ);
      calcBoundingRect(explYZ.values(), minYZ, maxYZ);
      //System.out.println("YZ = " + minYZ + " " + maxYZ + " " + (maxYZ.getX() - minYZ.getX()) + "x" +  (maxYZ.getY() - minYZ.getY()));

      Point2i sizeXY = new Point2i(maxXY.getX() - minXY.getX(), maxXY.getY() - minXY.getY());
      Point2i startXY = new Point2i(-minXY.getX() + 10, -minXY.getY() + 10);

      for (Map.Entry<String, Point2i[]> entry : explXY.entrySet()) {
        drawExploded(g, entry.getValue(), startXY, detailMap.get(entry.getKey()).getBox().getColor());
      }

      Point2i startXZ = new Point2i(-minXZ.getX() + 10, -minXZ.getY() + sizeXY.getY() + 30);

      for (Map.Entry<String, Point2i[]> entry : explXZ.entrySet()) {
        drawExploded(g, entry.getValue(), startXZ, detailMap.get(entry.getKey()).getBox().getColor());
      }

      Point2i startYZ = new Point2i(-minYZ.getX() + 30 + sizeXY.getX(), -minYZ.getY() + sizeXY.getY() + 30); 

      for (Map.Entry<String, Point2i[]> entry : explYZ.entrySet()) {
        drawExploded(g, entry.getValue(), startYZ, detailMap.get(entry.getKey()).getBox().getColor());
      }

      Point2i startText = new Point2i(sizeXY.getX() + 60 + (maxYZ.getX() - minYZ.getX()), 40);

      int ty = startText.getY();

      Font font = new Font("Courier New", Font.PLAIN, 20);
      g.setFont(font);

      for (Detail d : details) {
        g.setColor(d.getBox().getColor().get());
        g.fillRect(startText.getX(), ty-12, 12, 12);
        g.setColor(Color.black);
        g.drawString(d.getOrderedSizesString() + " " + d.getDetailType() + "/" + d.getName(), startText.getX() + 15, ty);
        ty += 22;
      }

    }


    public Map<String, Point2i[]> explodeDetails(OrthogonalPlate orientation) {
      Map<String, Point2i[]> explodedList = new LinkedHashMap<String, Point2i[]>();

      for (Detail d : details) {
        explodedList.put(d.getName(), explode(d, orientation));
      }

      return explodedList;
    }

    public void calcBoundingRect(Collection<Point2i[]> explodedList, Point2i min, Point2i max) {
      min.setX(Integer.MAX_VALUE);
      min.setY(Integer.MAX_VALUE);
      max.setX(Integer.MIN_VALUE);
      max.setY(Integer.MIN_VALUE);

      for (Point2i[] exploded : explodedList) {
        for (int i=0; i<4; i++) {
          if (exploded[i].getX() > max.getX()) {
            max.setX(exploded[i].getX());
          }
          if (exploded[i].getY() > max.getY()) {
            max.setY(exploded[i].getY());
          }
          if (exploded[i].getX() < min.getX()) {
            min.setX(exploded[i].getX());
          }
          if (exploded[i].getY() < min.getY()) {
            min.setY(exploded[i].getY());
          }
        }
      }
    }

    private Point2i[] explode(Detail d, OrthogonalPlate orientation) {
      Point2f pos = orientation.makeProjection(d.getBox().getPosition());
      Vector2f dim = orientation.makeProjection(d.getBox().getDim());
      return new Point2i[] {
        new Point2i(scale(pos.getX()), scale(pos.getY())),
        new Point2i(scale(pos.getX() + dim.getX()), scale(pos.getY())),
        new Point2i(scale(pos.getX() + dim.getX()), scale(pos.getY() + dim.getY())),
        new Point2i(scale(pos.getX()), scale(pos.getY() + dim.getY())),
      };
    }

    private void drawExploded(Graphics g, Point2i[] r, Point2i shift, Color3f color) {
      g.setColor(color.get());
      g.drawLine(r[0].getX() + shift.getX(), r[0].getY() + shift.getY(), r[1].getX() + shift.getX(), r[1].getY() + shift.getY());
      g.drawLine(r[1].getX() + shift.getX(), r[1].getY() + shift.getY(), r[2].getX() + shift.getX(), r[2].getY() + shift.getY());
      g.drawLine(r[2].getX() + shift.getX(), r[2].getY() + shift.getY(), r[3].getX() + shift.getX(), r[3].getY() + shift.getY());
      g.drawLine(r[3].getX() + shift.getX(), r[3].getY() + shift.getY(), r[0].getX() + shift.getX(), r[0].getY() + shift.getY());
    }

    private int scale(float v) {
      return Math.round(v * Float.parseFloat(System.getProperty("scale.2d", "900")));
    }


  }

  private JMenuItem printItem;

  private JMenuItem closeItem;

  public void actionPerformed(ActionEvent event) {
    Object target = event.getSource();

  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    printItem = new JMenuItem("Print...");
    printItem.addActionListener(this);
    closeItem = new JMenuItem("Close");
    closeItem.addActionListener(this);
    fileMenu.add(printItem);
    fileMenu.add(new JSeparator());
    fileMenu.add(closeItem);
    menuBar.add(fileMenu);
    return menuBar;
  }

}