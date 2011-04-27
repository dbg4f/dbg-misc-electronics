package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;
import graph.shapes.primitives.PSegment;

import static java.lang.Math.*;
import java.util.List;


/**
 * CircleInvolute
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class CircleInvolute extends Shape {
  public final static String CVS_ID = "$Id: $";

  private Circle circle;

  private double t1;
  private double t2;
  private int steps;
  private double shift;
  private boolean direction = true;


  public CircleInvolute(String name, Circle circle, double t1, double t2, int steps, double shift, boolean direction) {
    super(name);
    this.circle = circle;
    this.t1 = t1;
    this.t2 = t2;
    this.steps = steps;
    this.shift = shift;
    this.direction = direction;
  }

  public CircleInvolute(String name, Circle circle, double t1, double t2, int steps, double shift) {
    super(name);
    this.circle = circle;
    this.t1 = t1;
    this.t2 = t2;
    this.steps = steps;
    this.shift = shift;
  }

  private Point inv(double t) {

    double x = circle.r * (cos(t + shift) + t * sin(t + shift));
    double y = circle.r * (sin(t + shift) - t * cos(t + shift));

    y = direction ? y : -y;

    return new Point((int)round(x), (int)round(y));
  }

  public List<GraphicPrimitive> render() {
    Point start = inv(t1);
    Point end = inv(t1);

    double deltaT = (t2 - t1) / steps;
    double t = t1;

    primitives.clear();

    for (int i = 0; i <= steps; i++, t += deltaT) {
      end = inv(t);
      primitives.add(new PSegment(start.x + circle.c.x, start.y + circle.c.y, end.x + circle.c.x, end.y + circle.c.y));
      start = end;
    }

    return primitives;
  }
}
