package dbg.electronics.input.stick;

import javax.swing.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 19.02.2011
 * Time: 11:59:14
 * To change this template use File | Settings | File Templates.
 */
public class InputEventSink extends JPanel implements ActionListener, KeyListener, MouseListener {

  public void actionPerformed(ActionEvent e) {
    System.out.println("actionPerformed e = " + e);
  }

  public void keyTyped(KeyEvent e) {
    System.out.println("keyTyped e = " + e);
  }

  public void keyPressed(KeyEvent e) {
    System.out.println("keyPressed e = " + e);
  }

  public void keyReleased(KeyEvent e) {
    System.out.println("keyReleased e = " + e);
  }

  public void mouseClicked(MouseEvent e) {
    System.out.println("mouseClicked e = " + e);
  }

  public void mousePressed(MouseEvent e) {
    System.out.println("mousePressed e = " + e);
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
  
}
