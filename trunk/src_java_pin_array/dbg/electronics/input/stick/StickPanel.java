package dbg.electronics.input.stick;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 19.02.2011
 * Time: 11:55:25
 * To change this template use File | Settings | File Templates.
 */
public class StickPanel extends InputEventSink {

  private JFrame jframe;

  private MouseInputSink mouseSink;

  public StickPanel() {

    jframe = new JFrame();
    Container c = jframe.getContentPane();
    c.add(this);
    
    jframe.setBackground(Color.white);
    jframe.setSize(600, 600);
    jframe.setLocation(400, 300);
    jframe.setVisible(true);
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    //enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
    
    //addMouseListener(new MouseInputSink());

    mouseSink = new MouseInputSink(jframe);

    addMouseMotionListener(mouseSink);
    addMouseListener(mouseSink);



  }

  @Override
  protected void paintComponent(Graphics g) {
    g.drawLine(1, 1, 100, 100);

    g.drawString(mouseSink.getX() + " " + mouseSink.getY(), 100, 100);


    int cx = 300;
    int cy = 300;

    int x2 = cx + mouseSink.getX();
    int y2 = cy + mouseSink.getY();

    g.drawLine(cx, cy, cx, y2);
    g.drawLine(cx, cy, x2, cy);
    g.drawLine(cx, cy, x2, y2);

  }
}
