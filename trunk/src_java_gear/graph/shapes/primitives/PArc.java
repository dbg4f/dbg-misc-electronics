package graph.shapes.primitives;

import java.awt.*;

/**
 * PArc
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class PArc implements GraphicPrimitive{
  public final static String CVS_ID = "$Id: $";

  private int xc, yc;
  private int r;
  private int startAngle;
  private int angle;

  public PArc(int xc, int yc, int r, double start, double a) {
    this.xc = xc;
    this.yc = yc;
    this.r = r;
    this.startAngle = (int)Math.round((start / (2.0 * Math.PI)) * 360.0);
    this.angle = (int)Math.round((a / (2.0 * Math.PI)) * 360.0);
  }

  public void draw(Graphics g) {
    g.drawArc(xc - r, yc - r, 2 * r, 2 * r, startAngle, angle);
  }

  public String text() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
