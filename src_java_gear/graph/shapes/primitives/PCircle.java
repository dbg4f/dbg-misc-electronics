package graph.shapes.primitives;

import java.awt.*;

/**
 * PCircle
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class PCircle implements GraphicPrimitive {
  public final static String CVS_ID = "$Id: $";

  protected int xc, yc;
  protected int r;

  public PCircle(int xc, int yc, int r) {
    this.xc = xc;
    this.yc = yc;
    this.r = r;
  }

  public void draw(Graphics g) {
    g.drawOval(xc - r, yc - r, 2 * r, 2 * r);
  }

  public String text() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
