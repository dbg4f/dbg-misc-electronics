package dbg.electronics.input.stick;

import javax.swing.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 19.02.2011
 * Time: 12:12:21
 * To change this template use File | Settings | File Templates.
 */
public class MouseInputSink implements MouseMotionListener, MouseListener {

  private JFrame frame;

  private int x;
  private int y;

  private int startX;
  private int startY;


  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public MouseInputSink(JFrame frame) {
    this.frame = frame;
  }

  public void mouseDragged(MouseEvent e) {
    //System.out.println("mouseDragged e = " + e);

    update(e);
    
  }

  private void update(MouseEvent e) {
    x = (e.getXOnScreen() - startX);
    y = (e.getYOnScreen() - startY);
    frame.repaint();
    //System.out.println("this = " + this);
  }

  public void mouseMoved(MouseEvent e) {
    //System.out.println("mouseMoved e = " + e);
  }

  public void mouseClicked(MouseEvent e) {
    System.out.println("mouseClicked e = " + e);
  }

  public void mousePressed(MouseEvent e) {
    //System.out.println("mousePressed e = " + e);
    startX = e.getXOnScreen();
    startY = e.getYOnScreen();
    update(e);
  }

  public void mouseReleased(MouseEvent e) {
    System.out.println("mouseReleased e = " + e);
  }

  public void mouseEntered(MouseEvent e) {
    System.out.println("mouseEntered e = " + e);
  }

  public void mouseExited(MouseEvent e) {
    System.out.println("mouseExited e = " + e);
  }

  @Override
  public String toString() {
    return "MouseInputSink{" +
            "x=" + x +
            ", y=" + y +
            ", startX=" + startX +
            ", startY=" + startY +
            '}';
  }
}
