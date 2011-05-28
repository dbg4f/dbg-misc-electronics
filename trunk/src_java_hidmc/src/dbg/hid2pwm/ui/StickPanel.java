package dbg.hid2pwm.ui;

import dbg.hid2pwm.StateChangeSink;
import dbg.hid2pwm.InputState;

import javax.swing.*;
import java.awt.*;

public class StickPanel extends InputEventSink implements StateChangeSink {

  private JFrame jframe;

  private MouseInputListener mouseInputListener;

  private InputState inputState;

  private String msg;

  public void setMouseInput(MouseInputListener mouseInputListener) {
    this.mouseInputListener = mouseInputListener;
  }

  public void init() {
    jframe = new JFrame();
    Container c = jframe.getContentPane();
    c.add(this);

    jframe.setBackground(Color.white);
    jframe.setSize(600, 600);
    jframe.setLocation(400, 300);
    jframe.setVisible(true);
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    addMouseMotionListener(mouseInputListener);
    addMouseListener(mouseInputListener);
  }


  public void trigger(InputState inputState) {

    // TODO: synchronize access to input state
    this.inputState = inputState;

    jframe.repaint();
  }

  public void display(String msg) {
    this.msg = msg;

    jframe.repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.drawLine(1, 1, 100, 100);



    int cx = 300;
    int cy = 300;

    if (inputState != null) {

      g.drawString(inputState.getRangeX() + " " + inputState.getRangeY() + " " + msg, 100, 100);


      int x2 = cx + inputState.getRangeX();//mouseInputListener.getX();
      int y2 = cy + inputState.getRangeY();//mouseInputListener.getY();

      g.drawLine(cx, cy, cx, y2);
      g.drawLine(cx, cy, x2, cy);
      g.drawLine(cx, cy, x2, y2);

    }

  }
}
