package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;

import java.util.List;
import java.util.ArrayList;

/**
 * Shape
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public abstract class Shape {
  public final static String CVS_ID = "$Id: $";

  private String name;

  protected List<GraphicPrimitive> primitives = new ArrayList<GraphicPrimitive>();

  protected Shape(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  protected List<GraphicPrimitive> collectPrimitives(GraphicPrimitive... p) {
    primitives.clear();
    for(GraphicPrimitive p1 : p) {
      primitives.add(p1);
    }
    return primitives;
  }

  abstract List<GraphicPrimitive> render();

}
