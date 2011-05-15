package dbg.hid2pwm.ui;

import dbg.hid2pwm.StateChangeSink;
import dbg.hid2pwm.InputState;

import java.awt.event.*;

public class MouseInputSink implements MouseMotionListener, MouseListener {

  private StateChangeSink stateChangeSink;

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

  public MouseInputSink(StateChangeSink stateChangeSink) {
    this.stateChangeSink = stateChangeSink;
  }

  public void mouseDragged(MouseEvent e) {
    update(e);
  }

  private void update(MouseEvent e) {
    x = (e.getXOnScreen() - startX);
    y = (e.getYOnScreen() - startY);

    stateChangeSink.trigger(new InputState(x, y));
  }

  public void mouseMoved(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {
    System.out.println("mouseClicked e = " + e);
  }

  public void mousePressed(MouseEvent e) {
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
