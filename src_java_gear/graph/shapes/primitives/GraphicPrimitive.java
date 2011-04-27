package graph.shapes.primitives;

import java.awt.*;

/**
 * GraphicPrimitive
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public interface GraphicPrimitive {
  public final static String CVS_ID = "$Id: $";

  void draw(Graphics g);

  String text();

}
