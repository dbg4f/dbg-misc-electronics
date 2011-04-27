package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;
import graph.shapes.primitives.PSegment;

import java.util.List;

/**
 * Segment
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class Segment extends Shape{
  public final static String CVS_ID = "$Id: $";

  private Point p1;
  private Point p2;

  public Segment(String name, Point p1, Point p2) {
    super(name);
    this.p1 = p1;
    this.p2 = p2;
  }

  public List<GraphicPrimitive> render() {
    return collectPrimitives(new PSegment(p1.x, p1.y, p2.x, p2.y));
  }
}
