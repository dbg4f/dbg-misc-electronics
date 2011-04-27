package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;
import graph.shapes.primitives.PCircle;
import graph.shapes.primitives.PSegment;

import java.awt.Graphics;
import java.util.List;

/**
 * CircleRadius
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class CircleRadius extends Shape{
  public final static String CVS_ID = "$Id: $";

  private Circle circle;
  private double angle;

  public CircleRadius(String name, Circle circle, double angle) {
    super(name);
    this.circle = circle;
    this.angle = angle;
  }

  public List<GraphicPrimitive> render() {
    int intersectX = (int)Math.round(circle.r * Math.cos(angle));
    int intersectY = (int)Math.round(circle.r * Math.sin(angle));
    return collectPrimitives(new PSegment(circle.c.x, circle.c.y, circle.c.x + intersectX, circle.c.y + intersectY));
  }
}
