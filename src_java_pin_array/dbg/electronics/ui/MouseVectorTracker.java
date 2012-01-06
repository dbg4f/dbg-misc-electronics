package dbg.electronics.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

public class MouseVectorTracker extends JPanel implements MouseMotionListener, MouseListener {

 private JFrame jframe;

 private final VectorMovementHandler movementHandler;

 public MouseVectorTracker(VectorMovementHandler movementHandler) throws IOException, InterruptedException {

    this.movementHandler = movementHandler;

    init();

 }

 private void init() {
    jframe = new JFrame();
    Container c = jframe.getContentPane();
    c.add(this);

    jframe.setBackground(Color.white);
    jframe.setSize(600, 600);
    jframe.setLocation(400, 300);
    jframe.setVisible(true);
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    addMouseMotionListener(this);
    addMouseListener(this);
 }


 private void trigger(int x, int y) {

    movementHandler.onMove(x, y);
 }

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

 public void mouseDragged(MouseEvent e) {
    update(e);
 }

 private void update(MouseEvent e) {
    x = (e.getXOnScreen() - startX);
    y = (e.getYOnScreen() - startY);

    trigger(x, y);
 }

 public void mouseMoved(MouseEvent e) {
 }

 public void mouseClicked(MouseEvent e) {
 }

 public void mousePressed(MouseEvent e) {
    startX = e.getXOnScreen();
    startY = e.getYOnScreen();
    update(e);
 }

 public void mouseReleased(MouseEvent e) {
 }

 public void mouseEntered(MouseEvent e) {
 }

 public void mouseExited(MouseEvent e) {
 }


}

