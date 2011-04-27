package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;
import graph.shapes.primitives.PCircle;

/**
 * Circle
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class Circle extends Shape{
  public final static String CVS_ID = "$Id: $";

  protected Point c;
  protected int r;

  public Circle(String name, Point c, int r) {
    super(name);
    this.c = c;
    this.r = r;
  }

  public Point getCenter() {
    return c;
  }

  public int getRadius() {
    return r;
  }

  public java.util.List<GraphicPrimitive> render() {
    return collectPrimitives(new PCircle(c.x, c.y, r));
  }
}
