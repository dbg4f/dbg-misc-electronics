package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;
import graph.shapes.primitives.PSegment;

import java.util.List;

/**
 * CircleTangent
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class CircleTangent extends Shape {
  public final static String CVS_ID = "$Id: $";

  private double angle;
  private Circle circle;
  private int tail;

  public CircleTangent(String name, double angle, Circle circle, int tail) {
    super(name);
    this.angle = angle;
    this.circle = circle;
    this.tail = tail < circle.r ? circle.r : tail;
  }

  public CircleTangent(String name, double angle, Circle circle) {
    super(name);
    this.angle = angle;
    this.circle = circle;
    this.tail = circle.r;
  }

  public List<GraphicPrimitive> render2() {

    double x = circle.r / Math.cos(angle);
    double y = circle.r / Math.sin(angle);

    return collectPrimitives(new PSegment(circle.c.x + (int)x, circle.c.y, circle.c.x, circle.c.y + (int)y));
  }

  public List<GraphicPrimitive> render() {

    //double x = circle.r * Math.cos(angle);
    //double y = circle.r * Math.sin(angle);

    double l = Math.sqrt(circle.r * circle.r + tail * tail);

    double a1 = Math.atan(tail/circle.r);

    int x1 = (int)Math.round(l * Math.cos(a1 + angle));
    int y1 = (int)Math.round(l * Math.sin(a1 + angle));

    int x2 = (int)Math.round(l * Math.cos(angle - a1));
    int y2 = (int)Math.round(l * Math.sin(angle - a1));

    return collectPrimitives(new PSegment(x1 + circle.c.x, y1 + circle.c.y, x2 + circle.c.x, y2 + circle.c.y));
  }

}
