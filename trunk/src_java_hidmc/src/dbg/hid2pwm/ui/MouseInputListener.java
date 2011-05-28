package dbg.hid2pwm.ui;

import dbg.hid2pwm.StateChangeSink;
import dbg.hid2pwm.InputState;

import java.awt.event.*;

import org.apache.log4j.Logger;

public class MouseInputListener implements MouseMotionListener, MouseListener {

  private static final Logger log = Logger.getLogger(MouseInputListener.class);

  private StateChangeSink stateChangeSink;

  private int x;
  private int y;

  private int startX;
  private int startY;


  public void setStateChangeSink(StateChangeSink stateChangeSink) {
    this.stateChangeSink = stateChangeSink;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
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
    log.debug("mouseReleased e = " + e);
  }

  public void mouseEntered(MouseEvent e) {
    log.debug("mouseEntered e = " + e);
  }

  public void mouseExited(MouseEvent e) {
    log.debug("mouseExited e = " + e);
  }

  @Override
  public String toString() {
    return "MouseInputListener{" +
            "x=" + x +
            ", y=" + y +
            ", startX=" + startX +
            ", startY=" + startY +
            '}';
  }
}
