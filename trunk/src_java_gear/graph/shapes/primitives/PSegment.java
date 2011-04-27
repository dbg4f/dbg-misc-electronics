package graph.shapes.primitives;

import java.awt.*;

/**
 * PSegment
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class PSegment implements GraphicPrimitive{
  public final static String CVS_ID = "$Id: $";

  private int x1, x2, y1, y2;

  public PSegment(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;
  }

  public void draw(Graphics g) {
    g.drawLine(x1, y1, x2, y2);
  }

  public String text() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
